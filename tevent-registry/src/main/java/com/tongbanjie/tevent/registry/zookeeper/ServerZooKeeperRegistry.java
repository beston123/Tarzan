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
    protected void updateDiscovered(List<String> childrenPathList){
        if (CollectionUtils.isEmpty(childrenPathList)) {
            discovered.clear();
            return;
        }
        // 1 获取所用子节点的数据
        List<Address> addressOnZk = new ArrayList<Address>(childrenPathList.size());
        for(String childPath : childrenPathList){
            Address address = zkClient.readData(this.getDiscoverPath() + ZkConstants.PATH_SEPARATOR + childPath, true);
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

    @Override
    public List<Address> getDiscovered() {
        return this.discovered;
    }

}
