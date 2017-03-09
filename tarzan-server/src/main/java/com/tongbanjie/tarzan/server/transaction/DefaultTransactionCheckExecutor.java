package com.tongbanjie.tarzan.server.transaction;

import com.tongbanjie.tarzan.server.ServerController;
import com.tongbanjie.tarzan.common.body.RocketMQBody;
import com.tongbanjie.tarzan.common.message.MQMessage;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.common.message.RocketMQMessage;
import com.tongbanjie.tarzan.rpc.InvokeCallback;
import com.tongbanjie.tarzan.rpc.ResponseFuture;
import com.tongbanjie.tarzan.common.exception.RpcException;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.CheckTransactionStateHeader;
import com.tongbanjie.tarzan.server.client.ClientChannelInfo;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 向消息生产者回查事务状态 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
@Component
public class DefaultTransactionCheckExecutor implements TransactionCheckExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTransactionCheckExecutor.class);

    @Autowired
    private ServerController serverController;

    public DefaultTransactionCheckExecutor() {
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
