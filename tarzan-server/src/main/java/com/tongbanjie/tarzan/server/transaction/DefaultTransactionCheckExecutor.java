package com.tongbanjie.tarzan.server.transaction;

import com.tongbanjie.tarzan.common.body.MQBody;
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
import org.apache.commons.lang3.Validate;
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

    @Override
    public void gotoCheck(String producerGroup, MQType mqType, MQMessage mqMessage) {
        Validate.notEmpty(producerGroup, "生产者Group不能为空");
        Validate.notNull(mqType, "MQ类型不能为空");
        Validate.notNull(mqMessage, "MQ消息不能为空");

        MQBody mqBody = buildMQBody(mqType, mqMessage);
        final CheckTransactionStateHeader requestHeader = new CheckTransactionStateHeader();
        requestHeader.setMqType(mqType);
        requestHeader.setMessageKey(mqMessage.getMessageKey());
        requestHeader.setTransactionId(mqMessage.getId());

        final RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.CHECK_TRANSACTION_STATE,
                requestHeader, mqBody);
        checkTransaction(producerGroup, request, mqMessage.getId());
    }

    private MQBody buildMQBody(MQType mqType, MQMessage mqMessage){
        switch (mqType){
            case ROCKET_MQ:
                Validate.isTrue(mqMessage instanceof RocketMQMessage, "MQ消息格式不正确");
                return RocketMQBody.build((RocketMQMessage) mqMessage);
            case KAFKA:
                //TODO
            default:
                break;
        }
        LOGGER.warn("Check a producer transaction state, unsupported mq message, mqType:" + mqType + ", id:{}", mqMessage.getId());
        return null;
    }

    /**
     * 检查事务
     * @param producerGroup
     * @param request
     * @param tid
     */
    private void checkTransaction(final String producerGroup, final RpcCommand request, final long tid){
        //1、随机选取一个Producer
        final ClientChannelInfo clientChannelInfo =
                this.serverController.getClientManager().pickClientRandomly(producerGroup);
        if (null == clientChannelInfo) {
            LOGGER.warn("Check a producer transaction state, but not find any channel of this group[{}]",
                    producerGroup);
            return;
        }
        Channel channel = clientChannelInfo.getChannel();

        //2、发送查询事务请求
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
