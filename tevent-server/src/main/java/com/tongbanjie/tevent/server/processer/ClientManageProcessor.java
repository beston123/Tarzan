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
package com.tongbanjie.tevent.server.processer;

import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.ResponseCode;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;
import com.tongbanjie.tevent.rpc.protocol.header.RegisterRequestHeader;
import com.tongbanjie.tevent.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.heartbeat.HeartbeatData;
import com.tongbanjie.tevent.server.ServerController;
import com.tongbanjie.tevent.server.client.ClientChannelInfo;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientManageProcessor implements NettyRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientManageProcessor.class);

    private final ServerController serverController;

    public ClientManageProcessor(final ServerController serverController) {
        this.serverController = serverController;
    }

    @Override
    public RpcCommand processRequest(ChannelHandlerContext ctx, RpcCommand request)
            throws RpcCommandException {
        switch (request.getCmdCode()) {
            case RequestCode.HEART_BEAT:
                return this.heartBeat(ctx, request);
            case RequestCode.UNREGISTER_CLIENT:
                return this.unregisterClient(ctx, request);
            default:
                LOGGER.warn("Unknown request code "+request.getCmdCode());
                break;
        }
        return null;
    }

//    private List<ConsumeMessageHook> consumeMessageHookList;
//
//    public boolean hasConsumeMessageHook() {
//        return consumeMessageHookList != null && !this.consumeMessageHookList.isEmpty();
//    }
//
//
//    public void registerConsumeMessageHook(List<ConsumeMessageHook> consumeMessageHookList) {
//        this.consumeMessageHookList = consumeMessageHookList;
//    }


//    public void executeConsumeMessageHookAfter(final ConsumeMessageContext context) {
//        if (hasConsumeMessageHook()) {
//            for (ConsumeMessageHook hook : this.consumeMessageHookList) {
//                try {
//                    hook.consumeMessageAfter(context);
//                }
//                catch (Throwable e) {
//                }
//            }
//        }
//    }


    public RpcCommand unregisterClient(ChannelHandlerContext ctx, RpcCommand request)
            throws RpcCommandException {
        final RpcCommand response = RpcCommandBuilder.buildResponse();
        final RegisterRequestHeader requestHeader = (RegisterRequestHeader) request.decodeCustomHeader(RegisterRequestHeader.class);

        ClientChannelInfo clientChannelInfo = new ClientChannelInfo(//
            ctx.channel(),//
            requestHeader.getClientId(),//
            request.getVersion()//
            );

        final String group = requestHeader.getGroup();
        if (group != null) {
            this.serverController.getClientManager().unregister(group, clientChannelInfo);
        }

        response.setCmdCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    public RpcCommand heartBeat(ChannelHandlerContext ctx, RpcCommand request) {
        RpcCommand response = RpcCommandBuilder.buildResponse();

        HeartbeatData heartbeatData = request.getBody(HeartbeatData.class);
        String group = heartbeatData.getGroup();

        ClientChannelInfo clientChannelInfo = new ClientChannelInfo(//
            ctx.channel(),//
            heartbeatData.getClientId(),//
            request.getVersion()//
            );

        this.serverController.getClientManager().register(group, clientChannelInfo);

        response.setCmdCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }
}
