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
package com.tongbanjie.tarzan.client;

import com.tongbanjie.tarzan.rpc.exception.RpcConnectException;
import com.tongbanjie.tarzan.common.exception.RpcException;
import com.tongbanjie.tarzan.registry.Address;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.common.body.HeartbeatData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 服务端 管理者<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/15
 */
public class ServerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManager.class);

    private static final int BATCH_SIZE = 64;

    private final ClientController clientController;

    public ServerManager(ClientController clientController) {
        this.clientController = clientController;
    }

    public void sendHeartbeatToAllServer(){
        List<Address> copy = this.clientController.getClientRegistry().getDiscovered();
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Start to send heartbeat to {} servers.",  copy.size());
        }
        for(Address address : copy){
            sendHeartbeatToServer(address);
        }
    }

    /**
     * 心跳发送到Server
     * 每 BATCH_SIZE 个group合并成一个心跳发送
     * @param serverAddr
     */
    private void sendHeartbeatToServer(final Address serverAddr){
        Set<String> groupSet = this.clientController.getMessageSenderTable().keySet();
        List<HeartbeatData> heartbeatDataList = new ArrayList<HeartbeatData>(128);
        int groupCount = 0;
        HeartbeatData tempHeartbeat = null;
        for(String producerGroup : groupSet){
            if(groupCount % BATCH_SIZE == 0){
                tempHeartbeat = new HeartbeatData();
                tempHeartbeat.setClientId(this.clientController.getClientConfig().getClientId());
                heartbeatDataList.add(tempHeartbeat);
            }
            tempHeartbeat.addGroup(producerGroup);
            groupCount++;
        }
        for(HeartbeatData heartbeatData : heartbeatDataList){
            sendHeartbeat(serverAddr, heartbeatData, 3000);
        }
    }

    private void sendHeartbeat(final Address serverAddr,
                              final HeartbeatData heartbeatData,
                              final long timeoutMillis ) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Send heartbeat to server {}.", serverAddr);
        }
        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.HEART_BEAT, null);
        request.setBody(heartbeatData);

        try {
            //用oneWay方式即可
            this.clientController.getRpcClient().invokeOneWay(serverAddr.getAddress(), request, timeoutMillis);
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
