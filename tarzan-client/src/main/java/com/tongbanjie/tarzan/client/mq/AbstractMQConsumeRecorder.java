package com.tongbanjie.tarzan.client.mq;

import com.tongbanjie.tarzan.client.*;
import com.tongbanjie.tarzan.client.cluster.ClusterClient;
import com.tongbanjie.tarzan.common.message.MQConsume;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.rpc.exception.RpcConnectException;
import com.tongbanjie.tarzan.rpc.exception.RpcSendRequestException;
import com.tongbanjie.tarzan.rpc.exception.RpcTimeoutException;
import com.tongbanjie.tarzan.rpc.exception.RpcTooMuchRequestException;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.RecordConsumeHeader;
import com.tongbanjie.tarzan.rpc.protocol.header.MessageResultHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 〈MQ消费结果记录者 抽象类〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/12
 */
public abstract class AbstractMQConsumeRecorder<T extends MQConsume> implements MQConsumeRecorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMQConsumeRecorder.class);

    private final AtomicBoolean isStart = new AtomicBoolean(false);

    private final ClientConfig clientConfig;

    private MQType mqType;

    private ClusterClient clusterClient;

    private ClientController clientController;

    public AbstractMQConsumeRecorder(ClientConfig clientConfig, MQType mqType){
        this.clientConfig = clientConfig;
        this.mqType = mqType;
    }

    public void start() throws ClientException {
        if(isStart.compareAndSet(false, true)){
            if(this.mqType == null){
                throw new ClientException("Init MQ client failed. MQType can not be null");
            }
            clientController = ClientControllerFactory.getInstance().getAndCreate(clientConfig);
            clientController.start();
            clusterClient = clientController.getClusterClient();
        }
    }

    protected MessageResult consumed(T consume, boolean consumeStatus){
        final RecordConsumeHeader requestHeader = new RecordConsumeHeader();
        requestHeader.setTid(consume.getTid());
        requestHeader.setMqType(this.mqType);
        requestHeader.setConsumeStatus(consumeStatus);
        requestHeader.setMessageId(consume.getMessageId());
        requestHeader.setMessageKey(consume.getMessageKey());
        requestHeader.setConsumerGroup(consume.getConsumerGroup());
        requestHeader.setTopic(consume.getTopic());
        requestHeader.setTags(consume.getTags());
        requestHeader.setReconsumeTimes(consume.getReconsumeTimes());
        requestHeader.setConsumer(consume.getConsumer());

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.RECORD_CONSUME, requestHeader, null);

        MessageResult result;
        try {
            RpcCommand response = this.clusterClient.invokeSync(clientConfig.getSendMessageTimeout(), request);
            if (response.getCmdCode() == ResponseCode.SUCCESS){
                MessageResultHeader responseHeader = (MessageResultHeader)response.decodeCustomHeader(MessageResultHeader.class);
                result = MessageResult.buildSucc(responseHeader.getMsgId());
            }else{
                result = MessageResult.buildFail("系统异常, code:"+response.getCmdCode()
                        +", error:"+ response.getRemark());
            }
        } catch (Exception e){
            result = exceptionToResult(e);
        }
        return result;
    }

    /**
     * 异常打印，并转换为MessageResult
     * @param e
     * @return
     */
    private MessageResult exceptionToResult(Throwable e){
        MessageResult result;
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            LOGGER.error("系统异常,执行被中断", e);
            result = MessageResult.buildFail("系统异常");
        } else if (e instanceof RpcConnectException) {
            LOGGER.error(RpcConnectException.ERROR_MSG, e);
            result = MessageResult.buildFail(RpcConnectException.ERROR_MSG);
        } else if (e instanceof RpcTimeoutException) {
            LOGGER.error(RpcTimeoutException.ERROR_MSG, e);
            result = MessageResult.buildFail(RpcTimeoutException.ERROR_MSG);
        } else if (e instanceof RpcSendRequestException) {
            LOGGER.error(RpcSendRequestException.ERROR_MSG, e);
            result = MessageResult.buildFail(RpcSendRequestException.ERROR_MSG);
        } else if (e instanceof RpcTooMuchRequestException) {
            LOGGER.error(RpcTooMuchRequestException.ERROR_MSG, e);
            result = MessageResult.buildFail(RpcTooMuchRequestException.ERROR_MSG);
        } else {
            LOGGER.error("系统异常", e);
            result = MessageResult.buildFail("系统异常");
        }
        return result;
    }

    public void shutdown(){
        clientController.shutdown();
    }
}
