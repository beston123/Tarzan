package com.tongbanjie.tevent.registry.zookeeper;

import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RegistryType;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 客户端zk注册服务 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public class ClientZooKeeperRegistry extends AbstractZooKeeperRegistry {

    /**
     * 已发现的地址列表
     * 使用不可变List，防止并发访问出现IndexOutOfBoundsException
     */
    protected List<Address> discovered = Collections.EMPTY_LIST;

    private final Lock discoveredLock = new ReentrantLock();

    public ClientZooKeeperRegistry(String zkAddress){
        super(RegistryType.CLIENT, zkAddress);
        setAsClient();
    }

    private void setAsClient(){
        super.setRegisterRootPath(ZkConstants.CLIENTS_ROOT)
                .setRegisterChildPath(ZkConstants.CLIENT_CHILD_PATH)
                .setDiscoverPath(ZkConstants.SERVERS_ROOT);
    }

    @Override
    protected void onDiscoverChanged(List<String> childrenPathList){
        try{
            discoveredLock.tryLock(20, TimeUnit.SECONDS);
            if (CollectionUtils.isEmpty(childrenPathList)) {
                discovered = Collections.EMPTY_LIST;
            }else{
                discovered = toAddressList(childrenPathList);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            discoveredLock.unlock();
        }
    }

    private List<Address> toAddressList(List<String> childrenPathList){
        List<Address> addressOnZk = new ArrayList<Address>();
        for(String childPath : childrenPathList){
            Address address = getByChildPath(childPath);
            if(address != null && address.isEnable()){
                addressOnZk.add(address);
            }
        }
        // 返回不可变List
        return Collections.unmodifiableList(addressOnZk);
    }

    private Address getByChildPath(String childPath){
        return zkClient.readData(this.getDiscoverPath() + ZkConstants.PATH_SEPARATOR + childPath, true);
    }

    @Override
    public List<Address> getDiscovered() {
        return this.discovered;
    }

    /**
     * 扫描被禁用的Server
     */
    public void scanDiabledServers(){
        if(!zkClient.exists(ZkConstants.SERVERS_ROOT)){
            LOGGER.warn("Servers root not exist!");
            return;
        }
        List<String> childPathList = zkClient.getChildren(getDiscoverPath());

        List<String> disabled = getDisabled(childPathList);
        if(CollectionUtils.isEmpty(disabled)){
            return;
        }
        List<Address> discovered = getDiscovered();
        boolean hasDisabled = false;
        for(Address address : discovered){
            if( disabled.contains(address.getAddress()) ){
                hasDisabled = true;
                break;
            }
        }
        if(hasDisabled){
            LOGGER.info("Exist new disabled servers.");
            onDiscoverChanged(childPathList);
        }
    }

    private List<String> getDisabled(List<String> childPathList){
        List<String> disabled = new ArrayList<String>();
        for(String childPath : childPathList){
            Address address = getByChildPath(childPath);
            if(!address.isEnable()){
                disabled.add(address.getAddress());
            }
        }
        return disabled;
    }

}
