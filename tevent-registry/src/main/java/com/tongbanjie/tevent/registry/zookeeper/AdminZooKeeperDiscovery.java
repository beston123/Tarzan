package com.tongbanjie.tevent.registry.zookeeper;

import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.Discovery;
import com.tongbanjie.tevent.registry.Recoverable;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *  Admin zk 发现服务<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/17
 */
public class AdminZooKeeperDiscovery implements Discovery, Recoverable, ZooKeeperListener {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AdminZooKeeperDiscovery.class);

    /**
     * zk地址
     */
    private String zkAddress;

    /**
     * zk client
     */
    protected ZkClient zkClient;

    /**
     * 连接状态
     */
    private boolean connected = false;

    /**
     * 发现列表
     */
    private Map<String /* parentPath */, List<Address>> discoveredTable = new HashMap<String, List<Address>>();

    public AdminZooKeeperDiscovery(){}

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public void start() throws Exception {

        // 1、创建 ZooKeeper 客户端
        zkClient = new ZkClient(this.zkAddress, ZkConstants.SESSION_TIMEOUT, ZkConstants.CONNECTION_TIMEOUT);
        this.connected = true;
        LOGGER.debug("connect to zookeeper {} success.", zkAddress);

        // 1、发现客户端和服务端
        discoverAll();

        // 2、添加Zk监听器
        addZkListeners();
    }

    @Override
    public void shutdown(){
        //取消所有订阅
        zkClient.unsubscribeAll();
        //关闭连接
        zkClient.close();

        LOGGER.info("close this connection to zookeeper {}.", zkAddress);
    }

    protected void addZkListeners(){
        // 1、订阅client节点变化
        zkClient.subscribeChildChanges(ZkConstants.CLIENTS_ROOT, new IZkChildListener() {

            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                onChildChanged(parentPath, currentChildren);
            }
        });

        // 2、订阅server节点变化
        zkClient.subscribeChildChanges(ZkConstants.SERVERS_ROOT, new IZkChildListener() {

            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                onChildChanged(parentPath, currentChildren);
            }
        });

        // 3、订阅zk连接状态变化
        zkClient.subscribeStateChanges(new IZkStateListener() {

            /**
             * session状态变化时 触发
             * @param keeperState
             * @throws Exception
             */
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                if (keeperState == Watcher.Event.KeeperState.Disconnected) {
                    onSessionDisconnected();
                } else if (keeperState == Watcher.Event.KeeperState.SyncConnected) {
                    onSessionSyncConnected();
                }
            }

            /**
             * 与zk建立新Session
             * @throws Exception
             */
            @Override
            public void handleNewSession() throws Exception {
                onNewSession();
            }

            /**
             * 与zk建立session失败
             * @param throwable
             * @throws Exception
             */
            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {
                onSessionConnectError(throwable);
            }
        });
    }

    /**
     * 指定目录下 节点列表变化时
     * @param parentPath        对应的父节点的路径
     * @param currentChildren   子节点的相对路径
     * @throws Exception
     */
    @Override
    public void onChildChanged(String parentPath, List<String> currentChildren){
        if (currentChildren == null) {
            LOGGER.info("Parent path '{}' has been deleted." + parentPath);
        } else {
            LOGGER.info("Children changed in path {}, children count: {}", parentPath, currentChildren.size());
        }
        List<Address> discovered;
        if (CollectionUtils.isEmpty(currentChildren)) {
            discovered = Collections.EMPTY_LIST;
        }else{
            discovered = toAddressList(parentPath, currentChildren);
        }
        discoveredTable.put(parentPath, discovered);
    }

    private List<Address> toAddressList(String parentPath, List<String> childrenPathList){
        List<Address> addressOnZk = new ArrayList<Address>();

        for(String childPath : childrenPathList){
            Address address = zkClient.readData(parentPath + ZkConstants.PATH_SEPARATOR + childPath, true);
            if(address != null && address.isEnable()){
                addressOnZk.add(address);
            }
        }
        // 返回不可变List
        return Collections.unmodifiableList(addressOnZk);
    }

    /**
     * 与zk 断开连接上时 触发
     */
    @Override
    public void onSessionDisconnected(){
        connected = false;
        LOGGER.warn(">>>Zookeeper session disconnected.");
    }

    /**
     * 与zk 连接上时 触发
     */
    @Override
    public void onSessionSyncConnected(){
        connected = true;

        LOGGER.info(">>>Zookeeper session connected.");

        //与zk重新连接后，马上恢复
        recover();
    }

    /**
     * 与zk 建立新Session
     */
    @Override
    public void onNewSession(){
        //不作任何操作，因为在 onSessionSyncConnected() 已处理
        LOGGER.info(">>>Zookeeper session created.");
    }

    /**
     * 与zk 建立连接异常时 触发
     */
    @Override
    public void onSessionConnectError(Throwable throwable) throws Exception {
        LOGGER.error(">>>Zookeeper session establish error.", throwable);
        throw new Exception("Zookeeper session establish error.", throwable);
    }


    @Override
    public boolean isConnected(){
        return this.connected;
    }

    @Override
    public List<Address> getDiscovered(String parentPath) {
        return discoveredTable.get(parentPath);
    }

    @Override
    public List<Address> getDiscovered() {
        throw new RuntimeException("Use method 'getDiscovered(String parentPath)' instead.") ;
    }

    @Override
    public List<Address> discover(String parentPath){
        if (!zkClient.exists(parentPath)) {
            return Collections.emptyList();
        }
        List<Address> addressList = new ArrayList<Address>();
        List<String> childrenPathList = zkClient.getChildren(parentPath);
        for(String childPath : childrenPathList){
            Address address = zkClient.readData(parentPath + ZkConstants.PATH_SEPARATOR + childPath, true);
            if(address != null){
                addressList.add(address);
            }
        }
        return addressList;
    }

    @Override
    public void recover(){
        //重新发现
        discoverAll();
    }

    private void discoverAll() {
        if (zkClient.exists(ZkConstants.CLIENTS_ROOT)) {
            List<String> clientPathList = zkClient.getChildren(ZkConstants.CLIENTS_ROOT);
            onChildChanged(ZkConstants.CLIENTS_ROOT, clientPathList);
        }

        if (zkClient.exists(ZkConstants.SERVERS_ROOT)) {
            List<String> clientPathList = zkClient.getChildren(ZkConstants.SERVERS_ROOT);
            onChildChanged(ZkConstants.SERVERS_ROOT, clientPathList);
        }
    }

}
