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
package com.tongbanjie.tevent.client;


import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RecoverableRegistry;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.exception.RpcException;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.ResponseCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.heartbeat.HeartbeatData;
import io.netty.channel.Channel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ServerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManager.class);

    private static final long LockTimeoutMillis = 3000;

    private final Lock groupChannelLock = new ReentrantLock();

    private final RecoverableRegistry clientRegistry;

    private final ClientConfig clientConfig;

    private final RpcClient rpcClient;

    private final Random random = new Random();

    public ServerManager(ClientController clientController) {
        this.clientRegistry = clientController.getClientRegistry();
        this.clientConfig = clientController.getClientConfig();
        this.rpcClient = clientController.getRpcClient();
    }


    public void sendHeartbeatToAllServer(){
        List<Address> copy = clientRegistry.getDiscovered();
        LOGGER.debug("Start send heartbeat to {} servers....",  copy.size());
        for(Address address : copy){
            HeartbeatData heartbeatData = new HeartbeatData();
            heartbeatData.setClientId("");//TODO clientId
            heartbeatData.setGroup("");
            try {
                sendHeartbeat(address, heartbeatData, 5000);
            } catch (RpcException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendHeartbeat(final Address serverAddr,
                              final HeartbeatData heartbeatData,//
                              final long timeoutMillis//
    ) throws RpcException, InterruptedException {

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.HEART_BEAT, null);
        request.setBody(heartbeatData);
        RpcCommand response = this.rpcClient.invokeSync(serverAddr.getAddress(), request, timeoutMillis);
        assert response != null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS: {
                LOGGER.debug(">>>Send heartbeat '{}' to server {} success!",
                        heartbeatData.getGroup(), serverAddr);
                return;
            }
            default:
                break;
        }
    }

    public Address discoverOneServer() {
        List<Address> copy = clientRegistry.getDiscovered();
        int size = copy.size();
        Address address = null;
        if(size == 0){
            LOGGER.warn("Can not find a server.");
            return null;
        }else if(size == 1) {
            // 若只有一个地址，则获取该地址
            address = copy.get(0);
        } else {
            // 若存在多个地址，则随机获取一个地址
            // TODO 此处要做负载均衡
            address = copy.get(random.nextInt(size));
        }
        LOGGER.debug("Find a server {}", address);
        return address;
    }

//    public void register(final String group, final ClientChannelInfo clientChannelInfo) {
//        try {
//            ClientChannelInfo clientChannelInfoFound = null;
//
//            if (this.groupChannelLock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
//                try {
//                    HashMap<Channel, ClientChannelInfo> channelTable = this.groupChannelTable.get(group);
//                    if (null == channelTable) {
//                        channelTable = new HashMap<Channel, ClientChannelInfo>();
//                        this.groupChannelTable.put(group, channelTable);
//                    }
//
//                    clientChannelInfoFound = channelTable.get(clientChannelInfo.getChannel());
//                    if (null == clientChannelInfoFound) {
//                        channelTable.put(clientChannelInfo.getChannel(), clientChannelInfo);
//                        LOGGER.info("New client connected, group: {} channel: {}", group,
//                                clientChannelInfo.toString());
//                    }
//                }
//                finally {
//                    this.groupChannelLock.unlock();
//                }
//
//                if (clientChannelInfoFound != null) {
//                    LOGGER.debug("Get heartbeat from client, group: {} channel: {}", group, clientChannelInfoFound);
//                    clientChannelInfoFound.setLastUpdateTimestamp(System.currentTimeMillis());
//                }
//            }
//            else {
//                LOGGER.warn("ClientManager register lock timeout");
//            }
//        }
//        catch (InterruptedException e) {
//            LOGGER.error("", e);
//        }
//    }
//
//    public void unregister(final String group, final ClientChannelInfo clientChannelInfo) {
//        try {
//            if (this.groupChannelLock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
//                try {
//                    HashMap<Channel, ClientChannelInfo> channelTable = this.groupChannelTable.get(group);
//                    if (null != channelTable && !channelTable.isEmpty()) {
//                        ClientChannelInfo old = channelTable.remove(clientChannelInfo.getChannel());
//                        if (old != null) {
//                            LOGGER.info("unregister a client[{}] from groupChannelTable {}", group,
//                                    clientChannelInfo.toString());
//                        }
//
//                        if (channelTable.isEmpty()) {
//                            this.groupChannelTable.remove(group);
//                            LOGGER.info("unregister a client group[{}] from groupChannelTable", group);
//                        }
//                    }
//                }
//                finally {
//                    this.groupChannelLock.unlock();
//                }
//            }
//            else {
//                LOGGER.warn("ClientManager unregister client lock timeout");
//            }
//        }
//        catch (InterruptedException e) {
//            LOGGER.error("", e);
//        }
//    }
//


}
