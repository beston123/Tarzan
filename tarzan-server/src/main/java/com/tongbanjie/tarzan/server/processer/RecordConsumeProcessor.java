package com.tongbanjie.tarzan.server.processer;

import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;
import com.tongbanjie.tarzan.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.RecordConsumeHeader;
import com.tongbanjie.tarzan.rpc.protocol.header.MessageResultHeader;
import com.tongbanjie.tarzan.store.StoreManager;
import com.tongbanjie.tarzan.store.model.MessageConsume;
import com.tongbanjie.tarzan.store.service.MessageConsumeService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 〈记录消息消费结果 处理器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/13
 */
@Component
public class RecordConsumeProcessor implements NettyRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageProcessor.class);

    @Autowired
    private StoreManager storeManager;

    @Override
    public RpcCommand processRequest(ChannelHandlerContext ctx, RpcCommand request) throws Exception {
        switch (request.getCmdCode()) {
            case RequestCode.RECORD_CONSUME:
                return this.recordConsume(ctx, request);
            default:
                LOGGER.warn("Invalid request，requestCode："+request.getCmdCode());
                break;
        }
        return RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST,
                "Invalid request，requestCode：" + request.getCmdCode());
    }

    private RpcCommand recordConsume(ChannelHandlerContext ctx, RpcCommand request) throws RpcCommandException {
        //1、解析消息头
        RecordConsumeHeader messageHeader = (RecordConsumeHeader)request.decodeCustomHeader(RecordConsumeHeader.class);

        //2、消息消费结果保存
        Result<Void> result = null;
        //a、tarzan事务消息
        if(messageHeader.getTid() != null){
            result = recordTarzanConsume(messageHeader);
        //b、普通消息
        }else{
            result = recordMQConsume(messageHeader);
        }
        if(result != null && !result.isSuccess()){
            LOGGER.error("Record message consume result failed, message:{}, error:{}", messageHeader, result.getErrorDetail());
            return RpcCommandBuilder.buildFail("Record message consume result failed:"+result.getErrorMsg());
        }

        MessageResultHeader resultHeader = new MessageResultHeader();
        resultHeader.setMsgId(messageHeader.getMessageId());
        return RpcCommandBuilder.buildSuccess(resultHeader);
    }

    /**
     * tarzan事务消息 消费结果记录
     * @param messageHeader
     * @return
     */
    private Result<Void> recordTarzanConsume(RecordConsumeHeader messageHeader){
        MessageConsumeService messageConsumeService = this.storeManager.getMessageConsumeService();
        Result<Long> exist = messageConsumeService.exist(messageHeader.getTid(), messageHeader.getConsumerGroup());
        Validate.isTrue(exist.isSuccess(), exist.getErrorDetail());
        //a、如果已存在消费记录，则更新消费结果
        if(exist.getData() != null){
            MessageConsume forUpdate = new MessageConsume();
            forUpdate.setId(exist.getData());
            forUpdate.setMessageId(messageHeader.getMessageId());   //最新messageId
            forUpdate.setConsumer(messageHeader.getConsumer());     //最新的consumer
            forUpdate.setConsumeStatus(messageHeader.getConsumeStatus());
            forUpdate.setReconsumeTimes(messageHeader.getReconsumeTimes());
            return messageConsumeService.update(forUpdate);
        //b、不存在消费记录，且消费失败则插入
        }else if(!messageHeader.getConsumeStatus()){
            return messageConsumeService.insert(buildMessageConsume(messageHeader));
        }else{
            return Result.buildSucc(null);
        }
    }

    /**
     * 普通消息 消费结果记录
     * @param messageHeader
     * @return
     */
    private Result<Void> recordMQConsume(RecordConsumeHeader messageHeader){
        //消费失败或非首次消费
        if(!messageHeader.getConsumeStatus() || messageHeader.getReconsumeTimes() > 0){
            MessageConsumeService messageConsumeService = this.storeManager.getMessageConsumeService();
            return messageConsumeService.insert(buildMessageConsume(messageHeader));
        }else{
            return Result.buildSucc(null);
        }
    }

    private MessageConsume buildMessageConsume(RecordConsumeHeader messageHeader){
        MessageConsume messageConsume = new MessageConsume();
        messageConsume.setTid(messageHeader.getTid());
        messageConsume.setMessageId(messageHeader.getMessageId());
        messageConsume.setMessageKey(messageHeader.getMessageKey());
        messageConsume.setTopic(messageHeader.getTopic());
        messageConsume.setTags(messageHeader.getTags());
        messageConsume.setConsumerGroup(messageHeader.getConsumerGroup());
        messageConsume.setMqType(messageHeader.getMqType().getCode());
        messageConsume.setConsumer(messageHeader.getConsumer());
        messageConsume.setConsumeStatus(messageHeader.getConsumeStatus());
        messageConsume.setReconsumeTimes(messageHeader.getReconsumeTimes());
        return messageConsume;
    }

}
