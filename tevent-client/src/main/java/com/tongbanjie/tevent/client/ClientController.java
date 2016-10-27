package com.tongbanjie.tevent.client;

import com.tongbanjie.tevent.client.cluster.ClusterClient;
import com.tongbanjie.tevent.client.cluster.FailoverClusterClient;
import com.tongbanjie.tevent.client.processer.ServerRequestProcessor;
import com.tongbanjie.tevent.client.sender.MQMessageSender;
import com.tongbanjie.tevent.common.ServiceState;
import com.tongbanjie.tevent.common.util.NamedSingleThreadFactory;
import com.tongbanjie.tevent.common.util.NamedThreadFactory;
import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RecoverableRegistry;
import com.tongbanjie.tevent.cluster.loadbalance.LoadBalance;
import com.tongbanjie.tevent.cluster.loadbalance.LoadBalanceFactory;
import com.tongbanjie.tevent.cluster.loadbalance.LoadBalanceStrategy;
import com.tongbanjie.tevent.registry.zookeeper.ClientZooKeeperRegistry;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.netty.NettyClientConfig;
import com.tongbanjie.tevent.rpc.netty.NettyRpcClient;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public class ClientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

    /********************** 配置 ***********************/

    private final ClientConfig clientConfig;

    private final NettyClientConfig nettyClientConfig;

    private final ConcurrentHashMap<String/* group */, MQMessageSender> messageSenderTable = new ConcurrentHashMap<String,MQMessageSender>();

    //private final ConcurrentHashMap<Integer/* serverId */, String/* address */> serverAddressTable = new ConcurrentHashMap<Integer, String>();


    /********************** 服务 ***********************/
    //服务注册
    private final RecoverableRegistry clientRegistry;

    //远程通信层对象
    private RpcClient rpcClient;

    // 服务端连接管理
    private ServerManager serverManager;

    //集群客户端，支持failover和loadBalance
    private ClusterClient clusterClient;

    /********************** 线程池 ***********************/
    // 处理发送消息线程池
    private ExecutorService sendMessageExecutor;

    // 对消息写入进行流控
    private final BlockingQueue<Runnable> sendThreadPoolQueue;

    //客户端定时线程
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
            new NamedSingleThreadFactory("ClientScheduledThread"));

    /**********************  ***********************/
    private ServiceState serviceState = ServiceState.NOT_START;

    public ClientController(ClientConfig clientConfig, NettyClientConfig nettyClientConfig) {
        this.clientConfig = clientConfig;
        this.nettyClientConfig = nettyClientConfig;

        this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.clientConfig.getSendThreadPoolQueueCapacity());
        this.clientRegistry = new ClientZooKeeperRegistry(this.clientConfig.getRegistryAddress());

        this.rpcClient = new NettyRpcClient(this.nettyClientConfig);

        this.init();
    }

    private void init() {

        this.clusterClient = new FailoverClusterClient(new ThreadLocal<LoadBalance<Address>>(){
            @Override
            protected LoadBalance<Address> initialValue() {
                return LoadBalanceFactory.getLoadBalance(LoadBalanceStrategy.WeightedRandom);
            }
        }, this.rpcClient, this.clientRegistry);

        this.serverManager = new ServerManager(this);
    }

    private void registerProcessor() {
        this.sendMessageExecutor = new ThreadPoolExecutor(//
                this.clientConfig.getSendMessageThreadPoolNums(),//
                this.clientConfig.getSendMessageThreadPoolNums(),//
                1000 * 60,//
                TimeUnit.MILLISECONDS,//
                this.sendThreadPoolQueue,//
                new NamedThreadFactory("SendMessageThread_"));

        ServerRequestProcessor serverRequestProcessor = new ServerRequestProcessor(this);
        this.rpcClient.registerProcessor(RequestCode.CHECK_TRANSACTION_STATE, serverRequestProcessor, this.sendMessageExecutor);
    }

    public void registerMQMessageSender(String group, MQMessageSender mqMessageSender){
        this.messageSenderTable.put(group, mqMessageSender);
    }

    public void start() throws Exception {

        synchronized (this) {
            switch (this.serviceState) {
                case NOT_START:
                    serviceState = ServiceState.IN_STARTING;
                    try {
                        doStart();
                        serviceState = ServiceState.RUNNING;
                        LOGGER.info("Client start successfully. ");
                    } catch (Exception e) {
                        serviceState = ServiceState.FAILED;
                        throw e;
                    }
                    break;
                case IN_STARTING:
                case RUNNING:
                case FAILED:
                default:
                    LOGGER.warn("Client start concurrently. Current service state: " + this.serviceState);
                    break;
            }
        }

    }

    private void doStart() throws Exception{
        //rpcClient
        if (this.rpcClient != null) {
            this.rpcClient.start();
        }

        this.registerProcessor();

        //start Registry
        try {
            clientRegistry.start();
        } catch (Exception e) {
            throw new RuntimeException("The registry connect failed, address: " + clientConfig.getRegistryAddress(), e);
        }

        //定时向所有服务端发送心跳
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    ClientController.this.serverManager.sendHeartbeatToAllServer();
                } catch (Exception e) {
                    LOGGER.error("ScheduledTask sendHeartbeatToAllServer exception", e);
                }
            }
        }, 5 * 1000, clientConfig.getHeartbeatInterval(), TimeUnit.MILLISECONDS);

    }


    public void shutdown() {
        if (this.rpcClient != null) {
            this.rpcClient.shutdown();
        }

        if (this.sendMessageExecutor != null) {
            this.sendMessageExecutor.shutdown();
        }

        if(this.clientRegistry != null){
            this.clientRegistry.shutdown();
        }

        if(this.scheduledExecutorService != null){
            this.scheduledExecutorService.shutdown();
        }
    }


    public NettyClientConfig getNettyClientConfig() {
        return nettyClientConfig;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public RpcClient getRpcClient() {
        return rpcClient;
    }

    public ClusterClient getClusterClient() {
        return clusterClient;
    }

    public RecoverableRegistry getClientRegistry() {
        return clientRegistry;
    }

    public ConcurrentHashMap<String, MQMessageSender> getMessageSenderTable() {
        return messageSenderTable;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public ServiceState getServiceState() {
        return serviceState;
    }

}
