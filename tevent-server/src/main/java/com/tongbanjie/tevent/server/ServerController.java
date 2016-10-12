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
package com.tongbanjie.tevent.server;


import com.tongbanjie.tevent.common.NamedThreadFactory;
import com.tongbanjie.tevent.common.config.ServerConfig;
import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RecoverableRegistry;
import com.tongbanjie.tevent.registry.RegistryType;
import com.tongbanjie.tevent.registry.ServiceRegistry;
import com.tongbanjie.tevent.registry.zookeeper.ServerZooKeeperRegistry;
import com.tongbanjie.tevent.registry.zookeeper.ZkConstants;
import com.tongbanjie.tevent.registry.zookeeper.ZooKeeperServiceRegistry;
import com.tongbanjie.tevent.rpc.RpcServer;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.netty.NettyClientConfig;
import com.tongbanjie.tevent.rpc.netty.NettyRpcServer;
import com.tongbanjie.tevent.rpc.netty.NettyServerConfig;
import com.tongbanjie.tevent.server.client.ClientHousekeepingService;
import com.tongbanjie.tevent.server.client.ClientManager;
import com.tongbanjie.tevent.server.processer.ClientManageProcessor;
import com.tongbanjie.tevent.server.processer.SendMessageProcessor;
import com.tongbanjie.tevent.server.util.ServerUtils;
import com.tongbanjie.tevent.store.DefaultEventStore;
import com.tongbanjie.tevent.store.EventStore;
import com.tongbanjie.tevent.store.config.EventStoreConfig;
import com.tongbanjie.tevent.store.util.DistributedIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 服务进程控制器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 */
public class ServerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    /********************** 配置 ***********************/
    // 服务器配置
    private final ServerConfig serverConfig;

    // 通信层配置
    private final NettyServerConfig nettyServerConfig;

    private final NettyClientConfig nettyClientConfig;

    //数据存储层配置
    private final EventStoreConfig eventStoreConfig;

    /********************** client manager ***********************/
    // 客户端连接管理
    private final ClientManager clientManager;

    // 检测所有客户端连接
    private final ClientHousekeepingService clientHousekeepingService;

    /********************** 服务 ***********************/
    //服务注册
    private RecoverableRegistry serverRegistry;

    //事件存储
    private EventStore eventStore;

    //远程通信层对象
    private RpcServer rpcServer;

    /********************** 线程池 ***********************/
    // 处理发送消息线程池
    private ExecutorService sendMessageExecutor;

    // 处理管理Client线程池
    private ExecutorService clientManageExecutor;

    // 对消息写入进行流控
    private final BlockingQueue<Runnable> sendThreadPoolQueue;

    //服务器地址
    private Address serverAddress;

    public ServerController(final ServerConfig serverConfig, //
                            final NettyServerConfig nettyServerConfig, //
                            final NettyClientConfig nettyClientConfig, //
                            final EventStoreConfig eventStoreConfig //
    ) {
        this.serverConfig = serverConfig;
        this.nettyServerConfig = nettyServerConfig;
        this.nettyClientConfig = nettyClientConfig;
        this.eventStoreConfig = eventStoreConfig;

        this.clientManager = new ClientManager();
        this.clientHousekeepingService = new ClientHousekeepingService(this);

        this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.serverConfig.getSendThreadPoolQueueCapacity());

        this.serverRegistry = new ServerZooKeeperRegistry(serverConfig.getRegistryAddress());

    }


    public boolean initialize() {
        boolean result = true;

//        result = result && this.topicConfigManager.load();
//        result = result && this.subscriptionGroupManager.load();

        if (result) {
            try {
                this.eventStore =
                        new DefaultEventStore(this.eventStoreConfig, this.serverConfig);
            }
            catch (IOException e) {
                result = false;
                e.printStackTrace();
            }
        }

        result = result && this.eventStore.load();

        if (result) {
            this.rpcServer = new NettyRpcServer(this.nettyServerConfig, this.clientHousekeepingService);

            this.sendMessageExecutor = new ThreadPoolExecutor(//
                this.serverConfig.getSendMessageThreadPoolNums(),//
                this.serverConfig.getSendMessageThreadPoolNums(),//
                1000 * 60,//
                TimeUnit.MILLISECONDS,//
                this.sendThreadPoolQueue,//
                new NamedThreadFactory("SendMessageThread_"));

            this.clientManageExecutor =
                    Executors.newFixedThreadPool(this.serverConfig.getClientManageThreadPoolNums(), new NamedThreadFactory(
                            "ClientManageThread_"));

            this.registerProcessor();

            //设置分布式Id生成期 机器Id［每个JVM进程唯一］
            DistributedIdGenerator.setUniqueWorkId(serverConfig.getServerId());

            //服务器地址 [ip]:[port]
            String ip = ServerUtils.getLocalHostIp();
            if(ip == null){
                throw new RuntimeException("Get localHost ip failed.");
            }
            serverAddress = new Address(ServerUtils.getLocalHostIp(), nettyServerConfig.getListenPort());
            //注册地址
            try {
                serverRegistry.start();
                serverRegistry.register(serverAddress);
            } catch (Exception e) {
                result = false;
                LOGGER.error("The registry connect failed, address: "+serverConfig.getRegistryAddress(), e);
                e.printStackTrace();
            }
        }

        return result;
    }


    public void registerProcessor() {
        SendMessageProcessor sendProcessor = new SendMessageProcessor(this);
        //sendProcessor.registerSendMessageHook(sendMessageHookList);

        this.rpcServer.registerProcessor(RequestCode.SEND_MSG, sendProcessor, this.sendMessageExecutor);
        this.rpcServer.registerProcessor(RequestCode.TRANSACTION_MSG, sendProcessor, this.sendMessageExecutor);


        ClientManageProcessor clientProcessor = new ClientManageProcessor(this);
//        clientProcessor.registerConsumeMessageHook(this.consumeMessageHookList);
        this.rpcServer.registerProcessor(RequestCode.HEART_BEAT, clientProcessor, this.clientManageExecutor);
        this.rpcServer.registerProcessor(RequestCode.UNREGISTER_CLIENT, clientProcessor, this.clientManageExecutor);

    }


    public void shutdown() {
        if (this.rpcServer != null) {
            this.rpcServer.shutdown();
        }

        if (this.eventStore != null) {
            this.eventStore.shutdown();
        }

        if (this.sendMessageExecutor != null) {
            this.sendMessageExecutor.shutdown();
        }

        if (this.clientManageExecutor != null){
            this.clientManageExecutor.shutdown();
        }

        if (this.clientHousekeepingService != null) {
            this.clientHousekeepingService.shutdown();
        }

    }

    public void start() throws Exception {
        if (this.eventStore != null) {
            this.eventStore.start();
        }

        if (this.rpcServer != null) {
            this.rpcServer.start();
        }

        if (this.clientHousekeepingService != null) {
            this.clientHousekeepingService.start();
        }

    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public NettyServerConfig getNettyServerConfig() {
        return nettyServerConfig;
    }

    public NettyClientConfig getNettyClientConfig() {
        return nettyClientConfig;
    }

    public EventStoreConfig getEventStoreConfig() {
        return eventStoreConfig;
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public RecoverableRegistry getServerRegistry() {
        return serverRegistry;
    }

    public EventStore getEventStore() {
        return eventStore;
    }

    public RpcServer getRpcServer() {
        return rpcServer;
    }

    public Address getServerAddress() {
        return serverAddress;
    }
}
