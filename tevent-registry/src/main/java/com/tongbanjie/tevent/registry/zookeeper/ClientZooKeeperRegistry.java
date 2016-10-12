package com.tongbanjie.tevent.registry.zookeeper;

import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RegistryType;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public class ClientZooKeeperRegistry extends AbstractZooKeeperRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientZooKeeperRegistry.class);

    //已发现的地址
    protected final List<Address> discoveredServers = new CopyOnWriteArrayList<Address>();

    private Random random = new Random();

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
    public Address discover() {
        int size = discoveredServers.size();
        Address address = null;
        if(size == 0){
            LOGGER.warn("Can not find a server.");
            return null;
        }else if(size == 1) {
            // 若只有一个地址，则获取该地址
            address = discoveredServers.get(0);
        } else {
            // 若存在多个地址，则随机获取一个地址
            // TODO 此处要做负载均衡
            address = discoveredServers.get(random.nextInt(size));
        }
        LOGGER.debug("Find a server {}", address);
        return address;
    }

    @Override
    protected void updateDiscovered(List<String> childrenPathList){
        if (CollectionUtils.isEmpty(childrenPathList)) {
            discoveredServers.clear();
            return;
        }
        // 1 获取所用子节点的数据
        List<Address> addressOnZk = new ArrayList<Address>(childrenPathList.size());
        for(String childPath : childrenPathList){
            Address address = zkClient.readData(this.getDiscoverPath() + "/" + childPath, true);
            if(address != null){
                addressOnZk.add(address);
            }
        }

        Iterator<Address> iterator = discoveredServers.iterator();
        //2 剔除已经失效的节点
        while(iterator.hasNext()){
            Address address = iterator.next();
            if(!addressOnZk.contains(address)){
                iterator.remove();
            }
        }
        //3 添加 新增的节点
        for(Address address : addressOnZk){
            if(!discoveredServers.contains(address)){
                discoveredServers.add(address);
            }
        }
    }
}
