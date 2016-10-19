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
package com.tongbanjie.tevent.client.processer;

import com.tongbanjie.tevent.client.ClientController;
import com.tongbanjie.tevent.client.example.TransactionCheckListenerExample;
import com.tongbanjie.tevent.client.sender.MQMessageSender;
import com.tongbanjie.tevent.client.sender.RocketMQMessageSender;
import com.tongbanjie.tevent.common.body.RocketMQBody;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;
import com.tongbanjie.tevent.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.ResponseCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.header.CheckTransactionStateHeader;
import com.tongbanjie.tevent.rpc.util.RpcHelper;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;


public class ServerRequestProcessor implements NettyRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRequestProcessor.class);

    private final ClientController clientController;

    private final ConcurrentHashMap<String/* group */, MQMessageSender> messageSenderTable;

    private final RpcClient rpcClient;
    
    public ServerRequestProcessor(ClientController clientController) {
        this.clientController = clientController;
        this.messageSenderTable = this.clientController.getMessageSenderTable();
        this.rpcClient = this.clientController.getRpcClient();
    }


    @Override
    public RpcCommand processRequest(ChannelHandlerContext ctx, RpcCommand request) throws RpcCommandException {
        switch (request.getCmdCode()) {
        case RequestCode.CHECK_TRANSACTION_STATE:
            return this.checkTransactionState(ctx, request);
        default:
            LOGGER.warn("Invalid request，requestCode：" + request.getCmdCode());
            break;
        }
        return RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST,
                "Invalid request，requestCode：" + request.getCmdCode());
    }


    public RpcCommand checkTransactionState(ChannelHandlerContext ctx, RpcCommand request) throws RpcCommandException {
        final CheckTransactionStateHeader requestHeader =
                (CheckTransactionStateHeader) request.decodeCustomHeader(CheckTransactionStateHeader.class);

        validateCheckTransactionRequest(requestHeader);

        switch (requestHeader.getMqType()){
            case ROCKET_MQ:
                return checkTransactionStateForRocketMQ(ctx, request, requestHeader);
            default:
                break;
        }
        return null;
    }

    private void validateCheckTransactionRequest(CheckTransactionStateHeader requestHeader) throws RpcCommandException{
        if(requestHeader == null){
            throw new RpcCommandException("Param error: requestHeader can not be null");
        }
        if(requestHeader.getMqType() == null){
            throw new RpcCommandException("Param error: mqType can not be null");
        }
    }
    private RpcCommand checkTransactionStateForRocketMQ(ChannelHandlerContext ctx, RpcCommand request,
                                                        CheckTransactionStateHeader requestHeader) throws RpcCommandException {
        final RocketMQBody requestBody = request.getBody(RocketMQBody.class);
        if(requestBody == null){
            throw new RpcCommandException("Param error: request can not be null");
        }
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Received a checkTransactionState request, messageKey:{}, topic:{}, transactionId:{}",
                    requestHeader.getMessageKey(), requestBody.getTopic(), requestHeader.getTransactionId() );
        }
        String group = requestBody.getProducerGroup();
        if (group != null) {
            //按group查询生产者
            MQMessageSender mqMessageSender = this.messageSenderTable.get(group);
            if (mqMessageSender != null) {
                final String addr = RpcHelper.parseChannelRemoteAddr(ctx.channel());
                mqMessageSender.checkTransactionState(addr, requestBody, requestHeader, this.rpcClient);
            }
            else {
                LOGGER.debug("checkTransactionState, pick producer by group[{}] failed", group);
            }
        }
        else {
            throw new RpcCommandException("checkTransactionState failed, producer group can not be null");
        }
        return null;

    }


}
