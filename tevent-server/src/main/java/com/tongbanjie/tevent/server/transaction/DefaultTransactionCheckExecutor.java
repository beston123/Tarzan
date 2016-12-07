package com.tongbanjie.tevent.server.transaction;

import com.tongbanjie.tevent.common.body.RocketMQBody;
import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.rpc.InvokeCallback;
import com.tongbanjie.tevent.rpc.ResponseFuture;
import com.tongbanjie.tevent.common.exception.RpcException;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.ResponseCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.header.CheckTransactionStateHeader;
import com.tongbanjie.tevent.server.ServerController;
import com.tongbanjie.tevent.server.client.ClientChannelInfo;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 向消息生产者回查事务状态 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public class DefaultTransactionCheckExecutor implements TransactionCheckExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTransactionCheckExecutor.class);

    private final ServerController serverController;

    public DefaultTransactionCheckExecutor(final ServerController serverController) {
        this.serverController = serverController;
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
        
        // 第二步、检查消息类型，向Producer发起请求
        if(mqMessage instanceof RocketMQMessage){
            sendTransactionCheckRequest(clientChannelInfo.getChannel(), (RocketMQMessage) mqMessage);
        }else{
            LOGGER.warn("check a producer transaction state, unsupported mq type message, id: {}", mqMessage.getId());
        }
    }

    private void sendTransactionCheckRequest(final Channel channel, final RocketMQMessage rocketMQMessage){
        final CheckTransactionStateHeader requestHeader = new CheckTransactionStateHeader();
        requestHeader.setMqType(MQType.ROCKET_MQ);
        requestHeader.setMessageKey(rocketMQMessage.getMessageKey());
        requestHeader.setTransactionId(rocketMQMessage.getId());

        final RocketMQBody mqBody = new RocketMQBody();
        mqBody.setTopic(rocketMQMessage.getTopic());
        mqBody.setProducerGroup(rocketMQMessage.getProducerGroup());
        mqBody.setMessageBody(rocketMQMessage.getMessageBody());
        mqBody.setMessageKey(rocketMQMessage.getMessageKey());

        final Long tid = rocketMQMessage.getId();
        final RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.CHECK_TRANSACTION_STATE,
                requestHeader, mqBody);
        try {
            this.serverController.getRpcServer().invokeAsync(channel, request, 10 * 1000, new InvokeCallback(){
                @Override
                public void operationComplete(ResponseFuture responseFuture) {
                    RpcCommand response = responseFuture.getResponseCommand();
                    if(response == null){
                        if(responseFuture.isSendRequestOK()){
                            LOGGER.info("Send transactionCheck request success, transactionId:{}", tid);
                        }else{
                            LOGGER.error("Send transactionCheck request failed, transactionId:{}", tid);
                        }
                    }else{
                        if(response.getCmdCode() == ResponseCode.SUCCESS){
                            LOGGER.info("Get transactionCheck response, result is success, transactionId:{}",  tid);
                        }else{
                            LOGGER.warn("Get transactionCheck response, result is error, errorCode:{}, transactionId:{}",
                                    request.getCmdCode(),  tid);
                        }
                    }
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("InterruptedException", e);
        } catch (RpcException e) {
            LOGGER.error("RpcException", e);
        }

    }
}
