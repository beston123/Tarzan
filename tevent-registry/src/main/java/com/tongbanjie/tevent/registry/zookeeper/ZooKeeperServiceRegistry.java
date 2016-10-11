package com.tongbanjie.tevent.registry.zookeeper;

import com.tongbanjie.tevent.registry.ServiceRegistry;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry{

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private ZkClient zkClient;

    private String zkAddress;

    private String selfAddress;

    public ZooKeeperServiceRegistry(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public void start() throws Exception{
        // 1、创建 ZooKeeper 客户端
        zkClient = new ZkClient(this.zkAddress, ZkConstants.ZK_SESSION_TIMEOUT, ZkConstants.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper {} success.", zkAddress);

        // 2、创建 root 节点（持久）
        if (!zkClient.exists(ZkConstants.ZK_REGISTRY_PATH)) {
            zkClient.createPersistent(ZkConstants.ZK_REGISTRY_PATH);
            LOGGER.debug("create registry node: {}", ZkConstants.ZK_REGISTRY_PATH);
        }

        // 3、创建 servers 节点（持久）
        if (!zkClient.exists(ZkConstants.ZK_SERVERS_PATH)) {
            zkClient.createPersistent(ZkConstants.ZK_SERVERS_PATH);
            LOGGER.debug("create servers node: {}", ZkConstants.ZK_SERVERS_PATH);
        }

//        // 创建 client 节点（持久）
//        if (!zkClient.exists(ZkConstants.ZK_CLIENTS_PATH)) {
//            zkClient.createPersistent(ZkConstants.ZK_CLIENTS_PATH);
//            LOGGER.debug("create clients node: {}", ZkConstants.ZK_CLIENTS_PATH);
//        }

        // 3、监听zk链接状态
        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                if (keeperState == Watcher.Event.KeeperState.Disconnected) {
                    //
                } else if (keeperState == Watcher.Event.KeeperState.SyncConnected) {
                    //重新链接后，麻烦注册自己
                    LOGGER.info(">>>Zookeeper reconnected. Call register(" + selfAddress + ")");
                    register(selfAddress);
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

    public void register(String serverAddress) {
        // 创建 address 节点（临时）
        String addressNode = zkClient.createEphemeralSequential(ZkConstants.ZK_CHILD_SERVER_PATH, serverAddress);
        LOGGER.debug("create address node: {}", addressNode);

        this.selfAddress = serverAddress;
    }

    public void unregister(){

    }

    public void printChildren(String path){
        List<String> list = zkClient.getChildren(path);
        for(String s : list){
            LOGGER.debug("--------------"+s);
        }
    }



}
