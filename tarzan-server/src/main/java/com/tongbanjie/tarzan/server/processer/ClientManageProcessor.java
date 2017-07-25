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
package com.tongbanjie.tarzan.server.processer;

import com.tongbanjie.tarzan.rpc.util.RpcHelper;
import com.tongbanjie.tarzan.server.client.ClientChannelInfo;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;
import com.tongbanjie.tarzan.rpc.protocol.header.RegisterRequestHeader;
import com.tongbanjie.tarzan.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.common.body.HeartbeatData;
import com.tongbanjie.tarzan.server.client.ClientManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 客户端连接请求处理者<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
@Component
public class ClientManageProcessor implements NettyRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientManageProcessor.class);

    @Autowired
    private ClientManager clientManager;

    @Override
    public RpcCommand processRequest(ChannelHandlerContext ctx, RpcCommand request)
            throws RpcCommandException {
        switch (request.getCmdCode()) {
            case RequestCode.HEART_BEAT:
                return this.heartBeat(ctx, request);
            case RequestCode.UNREGISTER_CLIENT:
                return this.unregisterClient(ctx, request);
            case RequestCode.HEALTH_CHECK:
                return this.healthCheck(ctx, request);
            default:
                LOGGER.warn("Unknown request code "+request.getCmdCode());
                break;
        }
        return RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST,
                "Invalid request，requestCode：" + request.getCmdCode());
    }

    private RpcCommand unregisterClient(ChannelHandlerContext ctx, RpcCommand request)
            throws RpcCommandException {
        final RpcCommand response = RpcCommandBuilder.buildResponse();
        final RegisterRequestHeader requestHeader = (RegisterRequestHeader) request.decodeCustomHeader(RegisterRequestHeader.class);

        ClientChannelInfo clientChannelInfo = new ClientChannelInfo(//
            ctx.channel(),//
            requestHeader.getClientId(),//
            request.getVersion()//
            );
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Get unRegister request from address [{}].", RpcHelper.parseChannelRemoteAddr(ctx.channel()));
        }
        final String group = requestHeader.getGroup();
        if (group != null) {
            this.clientManager.unregister(group, clientChannelInfo);
        }

        response.setCmdCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    private RpcCommand heartBeat(ChannelHandlerContext ctx, RpcCommand request) {
        RpcCommand response = RpcCommandBuilder.buildResponse();

        HeartbeatData heartbeatData = request.getBody(HeartbeatData.class);
        List<String> groups = heartbeatData.getGroups();

        ClientChannelInfo clientChannelInfo = new ClientChannelInfo(//
            ctx.channel(),//
            heartbeatData.getClientId(),//
            request.getVersion()//
            );
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Get heartbeat request from address [{}].", RpcHelper.parseChannelRemoteAddr(ctx.channel()));
        }
        for(String group : groups){
            this.clientManager.register(group, clientChannelInfo);
        }

        response.setCmdCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    public RpcCommand healthCheck(ChannelHandlerContext ctx, RpcCommand request) {
        RpcCommand response = RpcCommandBuilder.buildResponse();
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Get healthCheck request from address [{}].", RpcHelper.parseChannelRemoteAddr(ctx.channel()));
        }
        response.setCmdCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }
}
