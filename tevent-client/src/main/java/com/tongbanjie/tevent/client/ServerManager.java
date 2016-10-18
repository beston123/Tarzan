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


import com.tongbanjie.tevent.client.sender.MQMessageSender;
import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RecoverableRegistry;
import com.tongbanjie.tevent.registry.cluster.LoadBalance;
import com.tongbanjie.tevent.registry.cluster.RandomLoadBalance;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.exception.*;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.body.HeartbeatData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ServerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManager.class);

    private final RecoverableRegistry clientRegistry;

    private final RpcClient rpcClient;

    private final ConcurrentHashMap<String/* group */, MQMessageSender> messageSenderTable;

    private LoadBalance<Address> loadBalance = new RandomLoadBalance();

    public ServerManager(ClientController clientController) {
        this.clientRegistry = clientController.getClientRegistry();
        this.rpcClient = clientController.getRpcClient();
        this.messageSenderTable = clientController.getMessageSenderTable();
    }

    public void sendHeartbeatToAllServer(){
        List<Address> copy = clientRegistry.getDiscovered();
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Start to send heartbeat to {} servers.",  copy.size());
        }
        for(Address address : copy){
            sendHeartbeatToServer(address);
        }
    }

    private void sendHeartbeatToServer(final Address serverAddr){
        for(String producerGroup : this.messageSenderTable.keySet()){
            HeartbeatData heartbeatData = new HeartbeatData();
            heartbeatData.setClientId("");//TODO clientId
            heartbeatData.setGroup(producerGroup);
            try {
                sendHeartbeat(serverAddr, heartbeatData, 3000);
            } catch (RpcConnectException e) {
                LOGGER.warn("Send heartbeat to server " + serverAddr +
                        " exception, maybe lose connection with the sever.", e);
            } catch (RpcException e) {
                LOGGER.warn("Send heartbeat to server " + serverAddr + " exception.", e);
            } catch (Exception e) {
                LOGGER.warn("Send heartbeat to server " + serverAddr + " exception.", e);
            }
        }

    }

    private void sendHeartbeat(final Address serverAddr,
                              final HeartbeatData heartbeatData,//
                              final long timeoutMillis//
    ) throws InterruptedException, RpcConnectException, RpcTooMuchRequestException, RpcSendRequestException, RpcTimeoutException {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Send heartbeat to server {}.", serverAddr);
        }
        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.HEART_BEAT, null);
        request.setBody(heartbeatData);

        //用oneWay方式即可
        this.rpcClient.invokeOneway(serverAddr.getAddress(), request, timeoutMillis);

//        RpcCommand response = this.rpcClient.invokeSync(serverAddr.getAddress(), request, timeoutMillis);
//        assert response != null;
//        switch (response.getCmdCode()) {
//            case ResponseCode.SUCCESS: {
//                LOGGER.debug(">>>Send heartbeat '{}' to server {} success!",
//                        heartbeatData.getGroup(), serverAddr);
//                return;
//            }
//            default:
//                break;
//        }
    }

    public Address discover() {
        List<Address> copy = clientRegistry.getDiscovered();

        Address address = loadBalance.select(copy);

        if(address == null){
            LOGGER.warn("Can not find a server.");
            return null;
        }
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Find a server {}", address);
        }
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
