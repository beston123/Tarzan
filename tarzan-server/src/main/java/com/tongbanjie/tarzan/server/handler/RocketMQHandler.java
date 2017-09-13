package com.tongbanjie.tarzan.server.handler;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.body.RocketMQBody;
import com.tongbanjie.tarzan.common.exception.SystemException;
import com.tongbanjie.tarzan.common.message.*;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.MessageResultHeader;
import com.tongbanjie.tarzan.rpc.protocol.header.QueryMessageHeader;
import com.tongbanjie.tarzan.server.ServerController;
import com.tongbanjie.tarzan.store.service.RocketMQStoreService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RocketMQ消息处理者<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
public class RocketMQHandler implements MQMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQHandler.class);

    private final Map<String/* Group */, MQProducer> producerTable = new ConcurrentHashMap<String, MQProducer>();

    private final Lock lock = new ReentrantLock();

    private final RocketMQStoreService mQStoreService;

    private final ServerController serverController;

    private final String nameSrvAddr;

    public RocketMQHandler(ServerController serverController) {
        this.serverController = serverController;
        this.mQStoreService = (RocketMQStoreService) this.serverController.getStoreManager().getMQStoreService(MQType.ROCKET_MQ);
        this.nameSrvAddr = this.serverController.getServerConfig().getRocketMQNamesrv();
    }

    @Override
    public RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request) {
        RpcCommand response;
        final RocketMQBody mqBody = request.getBody(RocketMQBody.class);

        RocketMQMessage mqMessage = RocketMQMessage.build(mqBody, TransactionState.COMMIT);
        /*************** 1、持久化消息 ***************/
        Result<Long> putResult = mQStoreService.put(mqMessage);
        if(putResult.isSuccess()){
            Long transactionId = putResult.getData();
            MessageResultHeader responseHeader = new MessageResultHeader();
            responseHeader.setTransactionId(transactionId);
            response = RpcCommandBuilder.buildSuccess(responseHeader);
            /*************** 2、异步发送消息 ***************/
            mqMessage.setId(transactionId);
            sendMessageAsync(mqMessage, transactionId);
        }else{
            LOGGER.error("发送消息失败, topic:{}, messageKey:{}, error:{}",
                    mqMessage.getTopic(), mqMessage.getMessageKey(), putResult.getErrorDetail());
            response = RpcCommandBuilder.buildFail("发送消息失败," + putResult.getErrorDetail());
        }
        return response;
    }

    @Override
    public Result<String/* msgId */> sendMessage(MQMessage mqMessage){
        Result<String> result;
        if(!(mqMessage instanceof RocketMQMessage)){
            return Result.buildFail("SendError", "消息格式错误");
        }
        RocketMQMessage rocketMQMessage = (RocketMQMessage) mqMessage;
        try{
            SendResult sendResult = sendToMQ(rocketMQMessage);
            Validate.notNull(sendResult);
            if(com.alibaba.rocketmq.client.producer.SendStatus.SEND_OK == sendResult.getSendStatus()){
                result = Result.buildSucc(sendResult.getMsgId());
            }else{
                LOGGER.warn("消息发送可能失败 messageKey:{}, msgId:{}, sendStatus:{}",
                        mqMessage.getMessageKey(), sendResult.getMsgId(), sendResult.getSendStatus());
                result = Result.buildFail("SendError", "消息发送可能失败, 发送状态："+sendResult.getSendStatus());
            }
        }catch (Exception e){
            LOGGER.error("消息发送失败, messageKey:" +mqMessage.getMessageKey()
                    +", topic:"+rocketMQMessage.getTopic(), e);
            result = Result.buildFail("SendError", "消息发送失败", e.getMessage());
        }
        return result;
    }

    /**
     * 发送消息到MQ
     * @param mqMessage
     * @return
     */
    private SendResult sendToMQ(RocketMQMessage mqMessage){
        /*************** 1、查找 MQ发送者 ***************/
        MQProducer producer;
        try {
            producer = getMQProducer(mqMessage.getProducerGroup());
        } catch (MQClientException e) {
            throw new SystemException("Get RocketMQ producer failed, group {}" + mqMessage.getProducerGroup(), e);
        }

        /*************** 2、组装并发送消息 ***************/
        Message msg = new Message(mqMessage.getTopic(), mqMessage.getTags(),
                mqMessage.getMessageKey(), mqMessage.getMessageBody());
        if(mqMessage.getId() != null){
            msg.putUserProperty(Constants.TARZAN_MQ_TID, String.valueOf(mqMessage.getId()));
        }
        try {
            return producer.send(msg);
        } catch (Exception e) {
            throw new SystemException("Send to RocketMQ failed, nameSrvAddress "+this.nameSrvAddr, e);
        }
    }

    @Override
    public RpcCommand prepareMessage(ChannelHandlerContext ctx, RpcCommand request) {
        final RpcCommand response;
        final RocketMQBody mqBody = request.getBody(RocketMQBody.class);

        RocketMQMessage mqMessage = RocketMQMessage.build(mqBody, TransactionState.PREPARE);

        //持久化 消息
        Result<Long> putResult = mQStoreService.put(mqMessage);
        if(putResult.isSuccess()){
            Long transactionId = putResult.getData();
            MessageResultHeader responseHeader = new MessageResultHeader();
            responseHeader.setTransactionId(transactionId);
            response = RpcCommandBuilder.buildSuccess(responseHeader);
        }else{
            LOGGER.error("准备事务消息失败, topic:{}, messageKey:{}, error:{}",
                    mqMessage.getTopic(), mqMessage.getMessageKey(), putResult.getErrorDetail());
            response = RpcCommandBuilder.buildFail("准备事务消息失败," + putResult.getErrorDetail());
        }

        return response;
    }

    @Override
    public RpcCommand commitMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId) {
        /*************** 1、检查消息是否需要提交 ***************/
        Result<RocketMQMessage> getResult = mQStoreService.get(transactionId);
        if(!getResult.isSuccess()){
            LOGGER.error("提交事务消息失败, transactionId: " + transactionId + ", error: " + getResult.getErrorDetail());
            return RpcCommandBuilder.buildFail("提交事务消息失败," + getResult.getErrorDetail());
        }

        RocketMQMessage mqMessage = getResult.getData();
        if(mqMessage == null){
            LOGGER.warn("提交事务消息失败, 该消息不存在, transactionId:" + transactionId);
            return RpcCommandBuilder.buildResponse(ResponseCode.NOT_EXIST, "该事务消息不存在");
        }else if(mqMessage.getTransactionState() == TransactionState.COMMIT.getCode()) {
            return RpcCommandBuilder.buildSuccess();
        }

        /*************** 2、提交消息 ***************/
        final RpcCommand response;
        final RocketMQBody newMqBody = request.getBody(RocketMQBody.class);

        RocketMQMessage forUpdate = new RocketMQMessage();
        forUpdate.setId(transactionId);
        forUpdate.setTransactionState(TransactionState.COMMIT.getCode());
        if(newMqBody !=null && newMqBody.getMessageBody() != null){
            //更新消息体
            forUpdate.setMessageBody(newMqBody.getMessageBody());
        }

        Result<Void> commitResult = mQStoreService.update(transactionId, forUpdate);

        if(commitResult.isSuccess()){
            response = RpcCommandBuilder.buildSuccess();
        }else{
            LOGGER.error("提交事务消息失败, transactionId: " + transactionId+", error: "+ commitResult.getErrorDetail());
            response = RpcCommandBuilder.buildFail("提交事务消息失败," + commitResult.getErrorDetail());
        }
        /*************** 3、异步发送消息 ***************/
        sendMessageAsync(mqMessage, transactionId);
        return response;
    }

    /**
     *  异步发送消息，并记录发送状态
     * @param mqMessage
     * @param transactionId
     */
    private void sendMessageAsync(final RocketMQMessage mqMessage, final Long transactionId){
        Runnable sendTask = new Runnable() {
            @Override
            public void run() {
                Result<String> result = sendMessage(mqMessage);
                RocketMQMessage forUpdate = new RocketMQMessage();
                forUpdate.setId(transactionId);
                if(result.isSuccess()){
                    forUpdate.setSendStatus(SendStatus.SUCCESS.getCode());
                    forUpdate.setMessageId(result.getData());
                }else{
                    forUpdate.setSendStatus(SendStatus.FAILED.getCode());
                }
                mQStoreService.update(transactionId, forUpdate);
            }
        };
        this.serverController.getSendMessageExecutor().execute(sendTask);
    }


    @Override
    public RpcCommand rollbackMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId) {
        /*************** 1、检查消息是否需要回滚 ***************/
        Result<RocketMQMessage> getResult = mQStoreService.get(transactionId);
        if(!getResult.isSuccess()) {
            LOGGER.error("事务消息回滚失败, transactionId: " + transactionId+", error: "+ getResult.getErrorDetail());
            return RpcCommandBuilder.buildFail("事务消息回滚失败," + getResult.getErrorDetail());
        }

        RocketMQMessage mqMessage = getResult.getData();
        if(mqMessage == null){
            LOGGER.warn("事务消息回滚失败, 消息不存在, transactionId:" + transactionId);
            return RpcCommandBuilder.buildResponse(ResponseCode.NOT_EXIST, "该事务消息不存在");
        }else if(mqMessage.getTransactionState() == TransactionState.ROLLBACK.getCode()){
            return RpcCommandBuilder.buildSuccess();
        }

        /*************** 2、回滚消息 ***************/
        final RpcCommand response;

        RocketMQMessage forUpdate = new RocketMQMessage();
        forUpdate.setId(transactionId);
        forUpdate.setTransactionState(TransactionState.ROLLBACK.getCode());

        Result<Void> rollbackResult = mQStoreService.update(transactionId, forUpdate);

        if(rollbackResult.isSuccess()){
            response = RpcCommandBuilder.buildSuccess();
        }else{
            LOGGER.error("事务消息回滚失败, transactionId: " + transactionId+", error: "+ rollbackResult.getErrorDetail());
            response = RpcCommandBuilder.buildFail("事务消息回滚失败," + rollbackResult.getErrorDetail());
        }
        return response;
    }

    @Override
    public RpcCommand queryMessage(ChannelHandlerContext ctx, QueryMessageHeader messageHeader) {
        Long id = messageHeader.getTransactionId();
        if(id != null){
            return queryById(id);
        }else if ( StringUtils.isNotEmpty(messageHeader.getMessageKey()) ){
            return queryByMessageKey(messageHeader.getMessageKey(), messageHeader.getLimit());
        }
        return RpcCommandBuilder.buildFail("查询参数不正确");
    }

    private RpcCommand queryById(Long id){
        Result<RocketMQMessage> result = mQStoreService.get(id);
        RpcCommand response;
        if(result.isSuccess()){
            response = RpcCommandBuilder.buildSuccess(result.getData());
        }else{
            response = RpcCommandBuilder.buildFail("查询失败,"+result.getErrorDetail());
        }
        return response;
    }

    private RpcCommand queryByMessageKey(String messageKey, int limit){
        Result<List<RocketMQMessage>> result = mQStoreService.queryByMessageKey(messageKey, limit);
        RpcCommand response;
        if(result.isSuccess()){
            response = RpcCommandBuilder.buildSuccess(result.getData());
        }else{
            response = RpcCommandBuilder.buildFail("查询失败,"+result.getErrorDetail());
        }
        return response;
    }

    private MQProducer getMQProducer(String group) throws MQClientException {
        MQProducer mqProducer = producerTable.get(group);
        if (mqProducer != null) {
            return mqProducer;
        }
        try {
            lock.lock();

            mqProducer = producerTable.get(group);
            if (mqProducer != null) {
                return mqProducer;
            }

            DefaultMQProducer producer = new DefaultMQProducer(group);
            producer.setNamesrvAddr(nameSrvAddr);
            producer.start();

            producerTable.put(group, producer);

            return producer;
        } finally {
            lock.unlock();
        }
    }

}
