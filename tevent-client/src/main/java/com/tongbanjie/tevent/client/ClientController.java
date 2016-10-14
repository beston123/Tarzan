package com.tongbanjie.tevent.client;

import com.tongbanjie.tevent.client.processer.ServerRequestProcessor;
import com.tongbanjie.tevent.client.sender.MQMessageSender;
import com.tongbanjie.tevent.common.util.NamedThreadFactory;
import com.tongbanjie.tevent.common.util.RemotingUtils;
import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RecoverableRegistry;
import com.tongbanjie.tevent.registry.zookeeper.ClientZooKeeperRegistry;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.netty.NettyClientConfig;
import com.tongbanjie.tevent.rpc.netty.NettyRpcClient;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private final Lock lockHeartbeat = new ReentrantLock();

    private final ConcurrentHashMap<Integer/* brokerId */, String/* address */> serverAddressTable = new ConcurrentHashMap<Integer, String>();


    /********************** client manager ***********************/
    // 服务端连接管理
    private ServerManager serverManager;
//
//    // 检测所有客户端连接
//    private final Timer clientHousekeepingService;

    /********************** 服务 ***********************/
    //服务注册
    private final RecoverableRegistry clientRegistry;

    //远程通信层对象
    private RpcClient rpcClient;

    /********************** 线程池 ***********************/
    // 处理发送消息线程池
    private ExecutorService sendMessageExecutor;

    // 对消息写入进行流控
    private final BlockingQueue<Runnable> sendThreadPoolQueue;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
                return new Thread(r, "ClientScheduledThread");
        }
    });

    private Address clientAddress;

    public ClientController(ClientConfig clientConfig, NettyClientConfig nettyClientConfig) {
        this.clientConfig = clientConfig;
        this.nettyClientConfig = nettyClientConfig;
        this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.clientConfig.getSendThreadPoolQueueCapacity());
        this.clientRegistry = new ClientZooKeeperRegistry(this.clientConfig.getRegistryAddress());
    }

    public boolean initialize() {
        boolean result = true;

        if (result) {
            this.rpcClient = new NettyRpcClient(this.nettyClientConfig);
            //this.rpcClient.start();

            this.serverManager = new ServerManager(this);

            this.sendMessageExecutor = new ThreadPoolExecutor(//
                    this.clientConfig.getSendMessageThreadPoolNums(),//
                    this.clientConfig.getSendMessageThreadPoolNums(),//
                    1000 * 60,//
                    TimeUnit.MILLISECONDS,//
                    this.sendThreadPoolQueue,//
                    new NamedThreadFactory("SendMessageThread_"));

//            this.clientManageExecutor = Executors.newFixedThreadPool(
//                    this.serverConfig.getClientManageThreadPoolNums(),
//                    new NamedThreadFactory("ClientManageThread_"));

            this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    try {
                        ClientController.this.serverManager.sendHeartbeatToAllServer();
                    } catch (Exception e) {
                        LOGGER.error("ScheduledTask sendHeartbeatToAllServer exception", e);
                    }
                }
            }, 30 * 1000, 30 * 1000, TimeUnit.MILLISECONDS);

            this.registerProcessor();

            //服务器地址 [ip]:[port]
            String localIp = RemotingUtils.getLocalHostIp();
            if(localIp  == null){
                throw new RuntimeException("Get localHost ip failed.");
            }
            //TODO 不需要注册client 到zk
            clientAddress = new Address(localIp, 1111);
            //注册地址
            try {
                clientRegistry.start();
                clientRegistry.register(clientAddress);
            } catch (Exception e) {
                result = false;
                LOGGER.error("The registry connect failed, address: " + clientConfig.getRegistryAddress(), e);
            }
        }

        return result;
    }

    public void registerProcessor() {
        ServerRequestProcessor serverRequestProcessor = new ServerRequestProcessor(this);
        this.rpcClient.registerProcessor(RequestCode.CHECK_TRANSACTION_STATE, serverRequestProcessor, this.sendMessageExecutor);

    }

    public void shutdown() {
        if (this.rpcClient != null) {
            this.rpcClient.shutdown();
        }

        if (this.sendMessageExecutor != null) {
            this.sendMessageExecutor.shutdown();
        }

//        if (this.clientHousekeepingService != null) {
//            this.clientHousekeepingService.shutdown();
//        }

    }

    public void start() throws Exception {

        if (this.rpcClient != null) {
            this.rpcClient.start();
        }

//        if (this.clientHousekeepingService != null) {
//            this.clientHousekeepingService.start();
//        }

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

    public Address getClientAddress() {
        return clientAddress;
    }

    public RecoverableRegistry getClientRegistry() {
        return clientRegistry;
    }
}
