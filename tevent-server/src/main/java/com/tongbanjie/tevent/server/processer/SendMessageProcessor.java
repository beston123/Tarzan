package com.tongbanjie.tevent.server.processer;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;
import com.tongbanjie.tevent.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.ResponseCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.header.SendMessageHeader;
import com.tongbanjie.tevent.rpc.protocol.header.TransactionMessageHeader;
import com.tongbanjie.tevent.server.ServerController;
import com.tongbanjie.tevent.server.mq.EventProducer;
import com.tongbanjie.tevent.server.mq.EventProducerFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
public class SendMessageProcessor implements NettyRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageProcessor.class);

    private final ServerController serverController;

    public SendMessageProcessor(final ServerController serverController) {
        this.serverController = serverController;
    }

    @Override
    public RpcCommand processRequest(ChannelHandlerContext ctx, RpcCommand request) throws Exception {
        switch (request.getCmdCode()) {
            case RequestCode.SEND_MESSAGE:
                return this.sendMessage(ctx, request);
            case RequestCode.TRANSACTION_MESSAGE:
                return this.transactionMessage(ctx, request);
            default:
                LOGGER.warn("Unknown request code "+request.getCmdCode());
                break;
        }
        return RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST,
                "未知请求，requestCode："+request.getCmdCode());
    }

    private RpcCommand transactionMessage(ChannelHandlerContext ctx, RpcCommand request) throws RpcCommandException {
        TransactionMessageHeader header = (TransactionMessageHeader) request.decodeCustomHeader(TransactionMessageHeader.class);
        EventProducer producer = getProducer(header.getMqType());
        //TODO 检查producer是否存在
        switch (header.getTransactionState()){
            case PREPARE:
                return producer.prepareMessage(ctx, request);
            case COMMIT:
                return producer.commitMessage(ctx, request, header.getTransactionId());
            case ROLLBACK:
                return producer.rollbackMessage(ctx, request, header.getTransactionId());
            case UNKNOWN:
                break;
            default:
                break;
        }
        return RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST,
                "未知事务状态, transactionState: "+header.getTransactionState());
    }

    private RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request) throws MQClientException, RpcCommandException {
        SendMessageHeader header = (SendMessageHeader)request.decodeCustomHeader(SendMessageHeader.class);
        EventProducer producer = getProducer(header.getMqType());
        //TODO 检查producer是否存在
        if(producer == null){

        }
        return producer.sendMessage(ctx, request);
    }

    private EventProducer getProducer(MQType mqType){
        return EventProducerFactory.getInstance().getAndCreate(mqType, this.serverController);
    }



}
