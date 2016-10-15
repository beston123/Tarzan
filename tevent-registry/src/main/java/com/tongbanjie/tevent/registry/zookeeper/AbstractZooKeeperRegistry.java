package com.tongbanjie.tevent.registry.zookeeper;

import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RegistryType;
import com.tongbanjie.tevent.registry.RecoverableRegistry;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public abstract class AbstractZooKeeperRegistry implements RecoverableRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractZooKeeperRegistry.class);

    protected RegistryType registryType;

    protected String zkAddress;

    protected final ZkClient zkClient;

    //已注册的地址
    protected final Map<Address /* address */, String/* absolutePath */> registered = new ConcurrentHashMap<Address, String>();

    private String registerRootPath;

    private String registerChildPath;

    private String discoverPath;

    protected AbstractZooKeeperRegistry(RegistryType registryType, String zkAddress) {
        this.registryType = registryType;
        this.zkAddress = zkAddress;
        // 创建 ZooKeeper 客户端
        zkClient = new ZkClient(this.zkAddress, ZkConstants.SESSION_TIMEOUT, ZkConstants.CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper {} success.", zkAddress);
    }

    @Override
    public void start() throws Exception {
        // 1、创建根目录
        if (!zkClient.exists(this.registerRootPath)) {
            zkClient.createPersistent(registerRootPath, true);
            LOGGER.debug("create {} node: {}", registryType, registerRootPath);
        }

        // 2、发现客户端
        discoverAll();

        // 3、添加Zk监听器
        addZkListeners();
    }

    @Override
    public void shutdown(){
        //取消注册
        unregisterAll();
        //取消所有订阅
        zkClient.unsubscribeAll();
        //关闭连接
        zkClient.close();
    }

    protected void addZkListeners(){
        String listenPath = this.discoverPath;

        // 2、订阅server节点变化
        zkClient.subscribeChildChanges(listenPath, new IZkChildListener() {

            /**
             * 用来处理zk server发送过来的通知
             * @param parentPath        对应的父节点的路径
             * @param currentChildren   子节点的相对路径
             * @throws Exception
             */
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                if (currentChildren == null) {
                    LOGGER.info("Parent path '{}' has been deleted." + parentPath);
                } else {
                    LOGGER.info("Children changed in path {}, children count: {}", parentPath, currentChildren.size());
                }
                //更新本地地址列表
                updateDiscovered(currentChildren);
            }
        });

        // 3、订阅zk链接状态变化
        zkClient.subscribeStateChanges(new IZkStateListener() {

            /**
             * session状态变化时 触发
             * @param keeperState
             * @throws Exception
             */
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                if (keeperState == Watcher.Event.KeeperState.Disconnected) {
                    LOGGER.warn(">>>Zookeeper session disconnected.");
                    //与zk断开后，可能未删除

                } else if (keeperState == Watcher.Event.KeeperState.SyncConnected) {
                    LOGGER.info(">>>Zookeeper session connected.");
                    //与zk重新链接后，马上恢复
                    recover();
                }
            }

            /**
             * 与zk建立链接上时 触发
             * @throws Exception
             */
            @Override
            public void handleNewSession() throws Exception {
                //不作任何操作，因为在handleStateChanged已处理
                LOGGER.info(">>>Zookeeper session reconnected.");
            }

            /**
             * 与zk建立session失败
             * @param throwable
             * @throws Exception
             */
            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {
                LOGGER.error(">>>Zookeeper session establish error.", throwable);
                throw new Exception("Zookeeper session establish error.", throwable);
            }
        });
    }


    @Override
    public String register(String path, Address address) {
        String absolutePath = zkClient.createEphemeralSequential(path, address);
        LOGGER.info("create address node: {}", absolutePath);

        //更新已注册地址
        registered.put(address, absolutePath);
        return absolutePath;
    }

    @Override
    public String register(Address address){
        return register(registerChildPath, address);
    }

    @Override
    public boolean unregister(String path) {
        boolean result = zkClient.delete(path);
        LOGGER.info("Unregister {} from zk {}", path, zkAddress);
        return result;
    }

    @Override
    public void recover(){
        //重新发现
        discoverAll();
        //重新注册
        registerAll();
    }

    protected void discoverAll() {
        String discoverPath = getDiscoverPath();
        if (!zkClient.exists(discoverPath)) {
            return;
        }
        List<String> childrenPathList = zkClient.getChildren(discoverPath);

        updateDiscovered(childrenPathList);
    }

    abstract void updateDiscovered(List<String> childrenPathList);


    protected void registerAll(){
        //反注册，在闪断的情况下，zk上的临时节点未及时删除
        unregisterAll();
        //重新注册
        Set<Address> addressSet = registered.keySet();
        Iterator<Address> ite = addressSet.iterator();
        while (ite.hasNext()){
            Address address = ite.next();
            register(this.registerChildPath, address);
        }
    }

    protected void unregisterAll(){
        Set<Map.Entry<Address, String>> entrySet = registered.entrySet();
        Iterator<Map.Entry<Address, String>> ite = entrySet.iterator();
        while (ite.hasNext()){
            Map.Entry<Address, String> entry = ite.next();
            unregister(entry.getValue());
        }
    }

    public String getDiscoverPath() {
        return discoverPath;
    }

    public String getRegisterChildPath() {
        return registerChildPath;
    }

    public String getRegisterRootPath() {
        return registerRootPath;
    }

    public AbstractZooKeeperRegistry setRegisterRootPath(String registerRootPath) {
        this.registerRootPath = registerRootPath;
        return this;
    }

    public AbstractZooKeeperRegistry setRegisterChildPath(String registerChildPath) {
        this.registerChildPath = registerChildPath;
        return this;
    }

    public AbstractZooKeeperRegistry setDiscoverPath(String discoverPath) {
        this.discoverPath = discoverPath;
        return this;
    }
}
