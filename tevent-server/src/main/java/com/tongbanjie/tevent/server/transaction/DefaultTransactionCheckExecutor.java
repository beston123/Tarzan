/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tongbanjie.tevent.server.transaction;

import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.rpc.RpcServer;
import com.tongbanjie.tevent.rpc.exception.RpcException;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.header.CheckTransactionStateHeader;
import com.tongbanjie.tevent.server.ServerController;
import com.tongbanjie.tevent.server.client.ClientChannelInfo;
import com.tongbanjie.tevent.store.Result;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 用来主动回查Producer的事务状态
 *
 */
public class DefaultTransactionCheckExecutor implements TransactionCheckExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTransactionCheckExecutor.class);

    private final ServerController serverController;

    private final RpcServer rpcServer;

    public DefaultTransactionCheckExecutor(final ServerController serverController) {
        this.serverController = serverController;
        this.rpcServer = serverController.getRpcServer();
    }


    @Override
    public void gotoCheck(String producerGroup, MQMessage mqMessage) {
        // 第一步、查询Producer
        final ClientChannelInfo clientChannelInfo =
                this.serverController.getClientManager().pickClientRandomly(producerGroup);
        if (null == clientChannelInfo) {
            LOGGER.warn("check a producer transaction state, but not find any channel of this group[{}]",
                    producerGroup);
            return;
        }

        // 第二步、查询消息
        Result<RocketMQMessage> result =
                this.serverController.getStoreManager().getStoreService().get(mqMessage.getId());
        if (null == result.getData()) {
            LOGGER.warn("check a producer transaction state, but not find message by id: {}", mqMessage.getId());
            return;
        }

        // 第三步、向Producer发起请求
        sendTransactionCheckRequest(clientChannelInfo.getChannel(), result.getData());
    }

    private void sendTransactionCheckRequest(Channel channel, RocketMQMessage rocketMQMessage){
        final CheckTransactionStateHeader requestHeader = new CheckTransactionStateHeader();
        requestHeader.setMqType(MQType.ROCKET_MQ);
        requestHeader.setMessageKey(rocketMQMessage.getMessageKey());
        requestHeader.setTransactionId(rocketMQMessage.getId());

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.CHECK_TRANSACTION_STATE,
                requestHeader);

        try {
            rpcServer.invokeOneway(channel, request, 5000);
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException", e);
        } catch (RpcException e) {
            LOGGER.error("RpcException", e);
        }
//        try {
//            channel.writeAndFlush(fileRegion).addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture future) throws Exception {
//                    selectMapedBufferResult.release();
//                    if (!future.isSuccess()) {
//                        log.error("invokeProducer failed,", future.cause());
//                    }
//                }
//            });
//        }
//        catch (Throwable e) {
//            log.error("invokeProducer exception", e);
//            selectMapedBufferResult.release();
//        }
    }
}
