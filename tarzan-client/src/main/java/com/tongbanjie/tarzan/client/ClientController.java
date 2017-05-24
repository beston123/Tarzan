package com.tongbanjie.tarzan.client;

import com.tongbanjie.tarzan.client.cluster.ClusterClient;
import com.tongbanjie.tarzan.client.cluster.FailoverClusterClient;
import com.tongbanjie.tarzan.client.processer.ServerRequestProcessor;
import com.tongbanjie.tarzan.client.sender.MQMessageSender;
import com.tongbanjie.tarzan.common.Service;
import com.tongbanjie.tarzan.common.ServiceState;
import com.tongbanjie.tarzan.common.util.NamedSingleThreadFactory;
import com.tongbanjie.tarzan.common.util.NamedThreadFactory;
import com.tongbanjie.tarzan.registry.Address;
import com.tongbanjie.tarzan.registry.ClientAddress;
import com.tongbanjie.tarzan.registry.RecoverableRegistry;
import com.tongbanjie.tarzan.cluster.loadbalance.LoadBalance;
import com.tongbanjie.tarzan.cluster.loadbalance.LoadBalanceFactory;
import com.tongbanjie.tarzan.cluster.loadbalance.LoadBalanceStrategy;
import com.tongbanjie.tarzan.registry.zookeeper.ClientZooKeeperRegistry;
import com.tongbanjie.tarzan.rpc.RpcClient;
import com.tongbanjie.tarzan.rpc.netty.NettyClientConfig;
import com.tongbanjie.tarzan.rpc.netty.NettyRpcClient;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Client控制器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public class ClientController implements Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

    /********************** 配置 ***********************/

    private final ClientConfig clientConfig;

    private final NettyClientConfig nettyClientConfig;

    //消息发送者 producerGroup->MQMessageSender
    private final ConcurrentHashMap<String/* producer group */, MQMessageSender> messageSenderTable = new ConcurrentHashMap<String,MQMessageSender>();

    /********************** 服务 ***********************/
    //服务注册
    private final RecoverableRegistry clientRegistry;

    //远程通信层对象
    private final RpcClient rpcClient;

    // 服务端连接管理
    private ServerManager serverManager;

    //集群客户端，支持failover和loadBalance
    private ClusterClient clusterClient;

    /********************** 线程池 ***********************/
    // 处理发送消息线程池
    private ExecutorService sendMessageExecutor;

    //处理事务反查请求线程池
    private ExecutorService checkTransactionExecutor;

    //对消息写入进行流控
    private final BlockingQueue<Runnable> sendThreadPoolQueue;

    //客户端定时线程
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
            new NamedSingleThreadFactory("ClientScheduledThread"));

    /********************** 服务状态 ***********************/
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

        this.checkTransactionExecutor = new ThreadPoolExecutor(//
                2, //
                8, //
                1000 * 60, //
                TimeUnit.MILLISECONDS, //
                new LinkedBlockingQueue<Runnable>(this.clientConfig.getCheckTransactionRequestCapacity()),
                new NamedThreadFactory("CheckTransactionThread_"));
    }

    private void registerProcessor() {
        this.sendMessageExecutor = new ThreadPoolExecutor(//
                this.clientConfig.getSendMessageThreadPoolNum(),//
                this.clientConfig.getSendMessageThreadPoolNum(),//
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

    public void start() throws ClientException {

        synchronized (this) {
            switch (this.serviceState) {
                case NOT_START:
                    serviceState = ServiceState.STARTING;
                    try {
                        doStart();
                        serviceState = ServiceState.RUNNING;
                        LOGGER.info("Client start successfully. ");
                    } catch (Exception e) {
                        serviceState = ServiceState.FAILED;
                        if(e instanceof ClientException){
                            throw (ClientException)e;
                        }else{
                            throw new ClientException("Client start exception,", e);
                        }
                    }
                    break;
                case STARTING:
                case RUNNING:
                case FAILED:
                default:
                    LOGGER.warn("Client start concurrently. Current service state: " + this.serviceState);
                    break;
            }
        }

    }

    private void doStart() throws ClientException{
        //1、启动RPC客户端
        if (this.rpcClient != null) {
            this.rpcClient.start();
        }

        //2、注册处理器
        this.registerProcessor();

        //3.1 连接注册中心
        try {
            clientRegistry.start();
        } catch (Exception e) {
            throw new ClientException("The registry connect failed, address: " + clientConfig.getRegistryAddress(), e);
        }
        //3.2 注册服务器地址
        clientRegistry.register(new ClientAddress(clientConfig.getAppName(), clientConfig.getClientId()));

        //4、定时向所有服务端发送心跳
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

        if (this.checkTransactionExecutor != null) {
            this.checkTransactionExecutor.shutdown();
        }

        if (this.clientRegistry != null){
            this.clientRegistry.shutdown();
        }

        if (this.scheduledExecutorService != null){
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

    public ExecutorService getCheckTransactionExecutor() {
        return checkTransactionExecutor;
    }
}
