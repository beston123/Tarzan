package com.tongbanjie.tevent.registry.zookeeper;

import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RegistryType;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public class ServerZooKeeperRegistry extends AbstractZooKeeperRegistry{

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerZooKeeperRegistry.class);

    //已发现的地址
    protected List<Address> discovered = new ArrayList<Address>();

    private Random random = new Random();

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
    public Address discover() {
        List<Address> copy = discovered;
        int size = copy.size();
        Address address = null;
        if(size == 0){
            LOGGER.warn("Can not find a server.");
            return null;
        }else if(size == 1) {
            // 若只有一个地址，则获取该地址
            address = copy.get(0);
        } else {
            // 若存在多个地址，则随机获取一个地址
            // TODO 此处要做负载均衡
            address = copy.get(random.nextInt(size));
        }
        LOGGER.debug("Find a server {}", address);
        return address;
    }

    @Override
    protected void updateDiscovered(List<String> childrenPathList){
        if (CollectionUtils.isEmpty(childrenPathList)) {
            discovered.clear();
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
        // 2 更新本地列表
        discovered = addressOnZk;

//        Iterator<Address> iterator = discovered.iterator();
//        //2 剔除已经失效的节点
//        while(iterator.hasNext()){
//            Address address = iterator.next();
//            if(!addressOnZk.contains(address)){
//                iterator.remove();
//            }
//        }
//        //3 添加 新增的节点
//        for(Address address : addressOnZk){
//            if(!discovered.contains(address)){
//                discovered.add(address);
//            }
//        }

    }

}
