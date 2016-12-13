package com.tongbanjie.tarzan.admin.component;

import com.tongbanjie.tarzan.cluster.loadbalance.LoadBalance;
import com.tongbanjie.tarzan.cluster.loadbalance.LoadBalanceFactory;
import com.tongbanjie.tarzan.cluster.loadbalance.LoadBalanceStrategy;
import com.tongbanjie.tarzan.registry.Address;
import com.tongbanjie.tarzan.registry.ServerAddress;
import com.tongbanjie.tarzan.registry.zookeeper.AdminZooKeeperDiscovery;
import com.tongbanjie.tarzan.registry.zookeeper.ZkConstants;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 〈Admin 发现〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/18
 */
@Component
public class AdminDiscovery extends AdminZooKeeperDiscovery  {

    private LoadBalance<Address> loadBalance = LoadBalanceFactory.getLoadBalance(LoadBalanceStrategy.RoundRobin);

    public boolean deleteServerId(int serverId){
        String path = ZkConstants.SERVER_IDS_ROOT + ZkConstants.PATH_SEPARATOR + serverId;
        if (zkClient.exists(path)) {
            return zkClient.delete(path);
        }else{
            return true;
        }
    }

    public ServerAddress getByServerId(int serverId){
        String path = ZkConstants.SERVER_IDS_ROOT + ZkConstants.PATH_SEPARATOR + serverId;
        return zkClient.readData(path, true);
    }

    public ServerAddress getOneServer(){
        List<Address> list  = getDiscovered(ZkConstants.SERVERS_ROOT);
        Address address = loadBalance.select(list);
        if(address != null){
            return (ServerAddress) address;
        }
        return null;
    }

}
