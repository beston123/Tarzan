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
        RecordConsumeHeader messageHeader = null;
        try {
            //1、解析消息头
            messageHeader = (RecordConsumeHeader)request.decodeCustomHeader(RecordConsumeHeader.class);

            //2、消息消费结果保存
            if(!messageHeader.getConsumeStatus() || messageHeader.getReconsumeTimes() > 0){
                recordConsume(messageHeader);
            }else {
                //第一次消费且消费成功的消息，暂不记录
            }
            MessageResultHeader resultHeader = new MessageResultHeader();
            resultHeader.setMsgId(messageHeader.getMessageId());
            return RpcCommandBuilder.buildSuccess(resultHeader);
        } catch (RpcCommandException e) {
            throw e;
        } catch (Exception e){
            return RpcCommandBuilder.buildFail("记录消息消费结果失败, message:"+messageHeader +", error:" + e.getMessage());
        }

    }

    private void recordConsume(RecordConsumeHeader messageHeader){
        MessageConsumeService messageConsumeService = this.storeManager.getMessageConsumeService();
        if(messageHeader.getTid() != null){
            Result<Long> exist = messageConsumeService.exist(messageHeader.getTid(), messageHeader.getConsumerGroup());
            Validate.isTrue(exist.isSuccess(), exist.getErrorDetail());
            if(exist.getData() != null){
                MessageConsume forUpdate = new MessageConsume();
                forUpdate.setId(exist.getData());
                forUpdate.setMessageId(messageHeader.getMessageId());   //最新messageId
                forUpdate.setConsumer(messageHeader.getConsumer());     //最新的consumer
                forUpdate.setConsumeStatus(messageHeader.getConsumeStatus());
                forUpdate.setReconsumeTimes(messageHeader.getReconsumeTimes());
                Result<Void> result = messageConsumeService.update(forUpdate);
                Validate.isTrue(result.isSuccess(), result.getErrorDetail());
            }
        }
        Result<Void> result = messageConsumeService.insert(buildMessageConsume(messageHeader));
        Validate.isTrue(result.isSuccess(), result.getErrorDetail());
    }

    private MessageConsume buildMessageConsume(RecordConsumeHeader messageHeader){
        MessageConsume messageConsume = new MessageConsume();
        messageConsume.setTid(messageHeader.getTid());
        messageConsume.setMessageId(messageHeader.getMessageId());
        messageConsume.setMessageKey(messageHeader.getMessageKey());
        messageConsume.setTopic(messageHeader.getTopic());
        messageConsume.setConsumerGroup(messageHeader.getConsumerGroup());
        messageConsume.setMqType(messageHeader.getMqType().getCode());
        messageConsume.setConsumer(messageHeader.getConsumer());
        messageConsume.setConsumeStatus(messageHeader.getConsumeStatus());
        messageConsume.setReconsumeTimes(messageHeader.getReconsumeTimes());
        return messageConsume;
    }

}
