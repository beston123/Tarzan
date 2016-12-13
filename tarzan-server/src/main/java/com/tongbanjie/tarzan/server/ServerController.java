/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.tongbanjie.tarzan.server;

import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.common.Service;
import com.tongbanjie.tarzan.common.util.NamedThreadFactory;
import com.tongbanjie.tarzan.common.util.NetworkUtils;
import com.tongbanjie.tarzan.registry.Address;
import com.tongbanjie.tarzan.registry.RecoverableRegistry;
import com.tongbanjie.tarzan.registry.ServerAddress;
import com.tongbanjie.tarzan.registry.zookeeper.ServerZooKeeperRegistry;
import com.tongbanjie.tarzan.rpc.RpcServer;
import com.tongbanjie.tarzan.rpc.netty.NettyRpcServer;
import com.tongbanjie.tarzan.rpc.netty.NettyServerConfig;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.server.client.ClientChannelManageService;
import com.tongbanjie.tarzan.server.client.ClientManager;
import com.tongbanjie.tarzan.server.processer.ClientManageProcessor;
import com.tongbanjie.tarzan.server.processer.QueryMessageProcessor;
import com.tongbanjie.tarzan.server.processer.SendMessageProcessor;
import com.tongbanjie.tarzan.server.transaction.TransactionCheckService;
import com.tongbanjie.tarzan.store.StoreManager;
import com.tongbanjie.tarzan.common.util.DistributedIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.*;

/**
 * 服务进程控制器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 */
public class ServerController implements Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    /********************** 配置 ***********************/
    // 服务器配置
    private final ServerConfig serverConfig;

    // 通信层配置
    private final NettyServerConfig nettyServerConfig;

    /********************** 客户端管理 ***********************/
    // 客户端连接管理
    private final ClientManager clientManager;

    // 检测所有客户端连接
    private final ClientChannelManageService clientChannelManageService;

    /********************** 服务 ***********************/
    //服务注册
    private final RecoverableRegistry serverRegistry;

    //事件存储
    private StoreManager storeManager;

    //远程通信层对象
    private RpcServer rpcServer;

    /********************** 线程池 ***********************/
    // 处理发送消息线程池
    private ExecutorService sendMessageExecutor;

    // 对消息写入进行流控
    private final BlockingQueue<Runnable> sendThreadPoolQueue;

    // 处理管理Client线程池
    private ExecutorService clientManageExecutor;

    /********************** 其他 ***********************/
    //定时任务管理器
    private ScheduledServiceManager scheduledServiceManager;

    //服务器地址
    private ServerAddress serverAddress;

    public ServerController(final ServerConfig serverConfig, //
                            final NettyServerConfig nettyServerConfig//
    ) {
        this.serverConfig = serverConfig;
        this.nettyServerConfig = nettyServerConfig;

        this.clientManager = new ClientManager(this.serverConfig);
        this.clientChannelManageService = new ClientChannelManageService(this);

        this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.serverConfig.getSendThreadPoolQueueCapacity());

        this.serverRegistry = new ServerZooKeeperRegistry(this.serverConfig.getRegistryAddress());

    }


    public boolean initialize() {
        boolean result = true;

        /**
         * 1、加载存储管理器
         */
        try {
            ApplicationContext act = new ClassPathXmlApplicationContext(Constants.TARZAN_STORE_CONTEXT);
            this.storeManager = act.getBean(StoreManager.class);
        } catch (BeansException e) {
            result = false;
            LOGGER.error("Load store manager failed.", e);
        }

        if (result) {
            /**
             * 2、初始化RpcServer
             */
            this.rpcServer = new NettyRpcServer(this.nettyServerConfig, this.clientChannelManageService);
            //注册请求处理器
            this.registerProcessor();

            /**
             * 3、初始化定时任务
             */
            this.scheduledServiceManager = new ScheduledServiceManager();
            this.scheduledServiceManager
                    .add(new TransactionCheckService(this))     //事务状态检查服务
                    .add(new MessageResendService(this))        //消息重发Job
                    .add(this.clientChannelManageService);      //检测所有客户端连接

            /**
             * 4、初始化注册中心，并注册地址到注册中心
             */
            try {
                serverRegistry.start();
                registerServer();
            } catch (ServerException e) {
                throw e;
            }catch (Exception e) {
                throw new ServerException("The registry connect failed, address: " + serverConfig.getRegistryAddress(), e);
            }

            //设置分布式Id生成器的WorkId
            DistributedIdGenerator.setUniqueWorkId(serverConfig.getServerId());

        }

        return result;
    }

    /**
     * 注册ServerId和地址
     * @return
     */
    private boolean registerServer() {
        //1、获取服务器地址 [ip]:[port]
        String localIp = NetworkUtils.getLocalHostIp();
        if (localIp == null) {
            throw new ServerException("Get localHost ip failed.");
        }
        serverAddress = new ServerAddress(localIp, nettyServerConfig.getListenPort());
        serverAddress.setWeight(serverConfig.getServerWeight());
        serverAddress.setServerId(serverConfig.getServerId());

        if (serverRegistry.isConnected()) {
            //2、注册serverId
            boolean registerFlag = ((ServerZooKeeperRegistry) this.serverRegistry)
                    .registerId(serverAddress.getServerId(), serverAddress);
            if (!registerFlag) {
                throw new ServerException("The server id '" + serverAddress.getServerId() +
                        "' already in use, it must be unique in cluster.");
            }
            //3、注册服务器地址
            serverRegistry.register(serverAddress);
            return true;
        }
        throw new ServerException("Register failed, address: " + serverConfig.getRegistryAddress()
                + ", server id: " + serverAddress.getServerId());
    }

    /**
     * 注册请求处理器
     */
    private void registerProcessor() {
        this.sendMessageExecutor = new ThreadPoolExecutor(//
                this.serverConfig.getSendMessageThreadPoolNum(),//
                this.serverConfig.getSendMessageThreadPoolNum(),//
                1000 * 60,//
                TimeUnit.MILLISECONDS,//
                this.sendThreadPoolQueue,//
                new NamedThreadFactory("SendMessageThread_"));

        this.clientManageExecutor = Executors.newFixedThreadPool(
                this.serverConfig.getClientManageThreadPoolNum(),
                new NamedThreadFactory("ClientManageThread_"));

        SendMessageProcessor sendProcessor = new SendMessageProcessor(this);
        this.rpcServer.registerProcessor(RequestCode.SEND_MESSAGE, sendProcessor, this.sendMessageExecutor);
        this.rpcServer.registerProcessor(RequestCode.TRANSACTION_MESSAGE, sendProcessor, this.sendMessageExecutor);

        QueryMessageProcessor queryProcessor = new QueryMessageProcessor(this);
        this.rpcServer.registerProcessor(RequestCode.QUERY_MESSAGE, queryProcessor, this.sendMessageExecutor);

        ClientManageProcessor clientProcessor = new ClientManageProcessor(this);
        this.rpcServer.registerProcessor(RequestCode.HEART_BEAT, clientProcessor, this.clientManageExecutor);
        this.rpcServer.registerProcessor(RequestCode.UNREGISTER_CLIENT, clientProcessor, this.clientManageExecutor);

    }

    public void start() throws Exception {
        if (this.storeManager != null) {
            this.storeManager.start();
        }

        if (this.rpcServer != null) {
            this.rpcServer.start();
        }

        if(this.scheduledServiceManager != null){
            this.scheduledServiceManager.start();
        }

    }

    public void shutdown() {
        if (this.rpcServer != null) {
            this.rpcServer.shutdown();
        }

        if (this.storeManager != null) {
            this.storeManager.shutdown();
        }

        if (this.sendMessageExecutor != null) {
            this.sendMessageExecutor.shutdown();
        }

        if (this.scheduledServiceManager != null) {
            this.scheduledServiceManager.shutdown();
        }

        if (this.serverRegistry != null){
            this.serverRegistry.shutdown();
        }
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public NettyServerConfig getNettyServerConfig() {
        return nettyServerConfig;
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public RecoverableRegistry getServerRegistry() {
        return serverRegistry;
    }

    public StoreManager getStoreManager() {
        return storeManager;
    }

    public RpcServer getRpcServer() {
        return rpcServer;
    }

    public Address getServerAddress() {
        return serverAddress;
    }

    public ExecutorService getSendMessageExecutor() {
        return sendMessageExecutor;
    }

}
