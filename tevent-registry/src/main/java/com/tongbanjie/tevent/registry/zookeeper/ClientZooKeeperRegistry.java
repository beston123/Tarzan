package com.tongbanjie.tevent.registry.zookeeper;

import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RegistryType;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    protected List<Address> discovered = new ArrayList<Address>();

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
    protected void updateDiscovered(List<String> childrenPathList){
        if (CollectionUtils.isEmpty(childrenPathList)) {
            discovered.clear();
            return;
        }
        // 1 获取所用子节点的数据
        List<Address> addressOnZk = new ArrayList<Address>();
        for(String childPath : childrenPathList){
            Address address = zkClient.readData(this.getDiscoverPath() + ZkConstants.PATH_SEPARATOR + childPath, true);
            if(address != null){
                addressOnZk.add(address);
            }
        }
        // 2 更新本地列表
        discovered = addressOnZk;
    }


    @Override
    public List<Address> getDiscovered() {
        return this.discovered;
    }
}
