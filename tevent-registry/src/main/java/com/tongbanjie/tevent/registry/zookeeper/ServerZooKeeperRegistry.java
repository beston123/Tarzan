package com.tongbanjie.tevent.registry.zookeeper;

import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RegistryType;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 服务端zk注册服务 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public class ServerZooKeeperRegistry extends AbstractZooKeeperRegistry{

    //已发现的地址
    protected List<Address> discovered = new ArrayList<Address>();

    public ServerZooKeeperRegistry(String zkAddress){
        super(RegistryType.SERVER, zkAddress);
        setAsServer();
    }

    private void setAsServer(){
        super.setRegisterRootPath(ZkConstants.SERVERS_ROOT)
                .setRegisterChildPath(ZkConstants.SERVER_CHILD_PATH)
                .setDiscoverPath(ZkConstants.CLIENTS_ROOT);
    }

    @Override
    protected void onDiscoverChanged(List<String> childrenPathList){
        if (CollectionUtils.isEmpty(childrenPathList)) {
            discovered = Collections.EMPTY_LIST;
        }else{
            discovered = toAddressList(childrenPathList);
        }
    }

    private List<Address> toAddressList(List<String> childrenPathList){
        List<Address> addressOnZk = new ArrayList<Address>();
        for(String childPath : childrenPathList){
            Address address = zkClient.readData(this.getDiscoverPath() + ZkConstants.PATH_SEPARATOR + childPath, true);
            if(address != null){
                addressOnZk.add(address);
            }
        }
        // 返回不可变List
        return Collections.unmodifiableList(addressOnZk);
    }

    @Override
    public List<Address> getDiscovered() {
        return this.discovered;
    }

    public boolean registerId(int id, Address address){
        String path = ZkConstants.SERVER_IDS_ROOT + ZkConstants.PATH_SEPARATOR + id;
        if(!zkClient.exists(path)){
            if(!zkClient.exists(ZkConstants.SERVER_IDS_ROOT)){
                zkClient.createPersistent(ZkConstants.SERVER_IDS_ROOT);
            }
            zkClient.createPersistent(path, address.getAddress());
            LOGGER.info("create persistent node {} success", path);
            return true;
        }

        String existedAddr = zkClient.readData(path, true);
        if(address.getAddress().equals(existedAddr)){
            return true;
        }else{
            LOGGER.error("The server id '{}' has been used by server '{}'.", id, existedAddr);
            return false;
        }
    }

}
