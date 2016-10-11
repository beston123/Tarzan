package com.tongbanjie.tevent.registry.zookeeper;

import com.tongbanjie.tevent.registry.ServiceDiscovery;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery{

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    private List<String> serverAddressList = new CopyOnWriteArrayList<String>();

    private String zkAddress;

    private ZkClient zkClient;

    private Random random = new Random();

    public ZooKeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public void start(){
        // 1、创建 ZooKeeper 客户端
        zkClient = new ZkClient(this.zkAddress, ZkConstants.ZK_SESSION_TIMEOUT, ZkConstants.ZK_CONNECTION_TIMEOUT);
        LOGGER.info("connect zookeeper {} success.", this.zkAddress);

        // 2、发现客户端
        discoverAll(ZkConstants.ZK_SERVERS_PATH);

        // 3、订阅server节点变化
        zkClient.subscribeChildChanges(ZkConstants.ZK_SERVERS_PATH, new IZkChildListener() {
            /**
             * handleChildChange： 用来处理服务器端发送过来的通知
             * parentPath：对应的父节点的路径
             * currentChildren：子节点的相对路径
             */
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                LOGGER.info("Children changed in path " + parentPath
                        + ", children count: " + currentChildren.size());
                updateLocalAddress(currentChildren);
            }
        });

        // 3、监听zk链接状态
        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                if (keeperState == Watcher.Event.KeeperState.Disconnected) {
                    //
                } else if (keeperState == Watcher.Event.KeeperState.SyncConnected) {
                    //
                    LOGGER.info(">>>Zookeeper reconnected. Call discoverAll("+ZkConstants.ZK_SERVERS_PATH+")");
                    discoverAll(ZkConstants.ZK_SERVERS_PATH);
                }
            }

            @Override
            public void handleNewSession() throws Exception {
                //
            }

            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {

            }
        });
    }

    private void updateLocalAddress(List<String> pathList){
        //获取所用节点的数据
        List<String> addressList = new ArrayList<String>(pathList.size());
        for(String childPath : pathList){
            String address = zkClient.readData(ZkConstants.ZK_SERVERS_PATH + "/" + childPath, true);
            if(address != null){
                addressList.add(address);
            }
        }
        Iterator<String> iterator = serverAddressList.iterator();
        //剔除已经失效的节点
        while(iterator.hasNext()){
            String address = iterator.next();
            if(!addressList.contains(address)){
                iterator.remove();
            }
        }
        //新增节点
        for(String address : addressList){
            if(!serverAddressList.contains(address)){
                serverAddressList.add(address);
            }
        }
    }

    public void discoverAll(String path) {
        // 获取 servers 节点
        String serversPath = path;
        if (!zkClient.exists(serversPath)) {
            throw new RuntimeException(String.format("can not find servers node on path: %s", serversPath));
        }
        List<String> pathList = zkClient.getChildren(serversPath);
        if (CollectionUtils.isNotEmpty(pathList)) {
            updateLocalAddress(pathList);
        }
    }

    public String discover(String path) {
        int size = serverAddressList.size();
        if(size == 0){
            return null;
        }
        // 获取 address 节点
        String address;
        if(size == 1) {
            // 若只有一个地址，则获取该地址
            address = serverAddressList.get(0);
        } else {
            // 若存在多个地址，则随机获取一个地址
            address = serverAddressList.get(random.nextInt(size));
        }
        return address;
    }

}
