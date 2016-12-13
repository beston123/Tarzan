package com.tongbanjie.tarzan.server.processer;

import com.tongbanjie.tarzan.server.ServerController;
import com.tongbanjie.tarzan.server.handler.MQMessageHandler;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.common.message.TransactionState;
import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;
import com.tongbanjie.tarzan.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.SendMessageHeader;
import com.tongbanjie.tarzan.rpc.protocol.header.TransactionMessageHeader;
import com.tongbanjie.tarzan.server.handler.MQMessageHandlerFactory;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息发送请求处理者<p>
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
                LOGGER.warn("Invalid request，requestCode："+request.getCmdCode());
                break;
        }
        return RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST,
                "Invalid request，requestCode：" + request.getCmdCode());
    }

    /**
     * 发送普通消息
     * @param ctx
     * @param request
     * @return
     * @throws RpcCommandException
     */
    private RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request) throws RpcCommandException {
        //1、解析并校验 消息头
        SendMessageHeader header = (SendMessageHeader)request.decodeCustomHeader(SendMessageHeader.class);
        validateMessage(header);

        //2、获取事件处理者
        MQMessageHandler producer = getHandler(header.getMqType());
        if(producer == null){
            return RpcCommandBuilder.buildResponse(ResponseCode.SYSTEM_ERROR,
                    "System error：can not find a producer to handle the message {}" + header);
        }
        //3、处理事件
        return producer.sendMessage(ctx, request);
    }

    private void validateMessage(SendMessageHeader header) throws RpcCommandException{
        if(header == null){
            throw new RpcCommandException("Param error: messageHeader can not be null");
        }
        if(header.getMqType() == null){
            throw new RpcCommandException("Param error: mqType can not be null");
        }
    }

    /**
     * 事务消息
     * @param ctx
     * @param request
     * @return
     * @throws RpcCommandException
     */
    private RpcCommand transactionMessage(ChannelHandlerContext ctx, RpcCommand request) throws RpcCommandException {
        //1、解析并校验 消息头
        TransactionMessageHeader header = (TransactionMessageHeader) request.decodeCustomHeader(TransactionMessageHeader.class);
        validateTransactionMessage(header);

        //2、获取事件处理者
        MQMessageHandler producer = getHandler(header.getMqType());
        if(producer == null){
            return RpcCommandBuilder.buildResponse(ResponseCode.SYSTEM_ERROR,
                    "System error：can not find a producer to handle the message {}"+ header);
        }
        //3、处理事件
        switch (header.getTransactionState()){
            case PREPARE:
                return producer.prepareMessage(ctx, request);
            case COMMIT:
                return producer.commitMessage(ctx, request, header.getTransactionId());
            case ROLLBACK:
                return producer.rollbackMessage(ctx, request, header.getTransactionId());
            case UNKNOWN:
                return producer.unknownMessage(ctx, request, header.getTransactionId());
            default:
                break;
        }
        //never goto here
        return RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST,
                "Param error: transactionState can not be null");
    }

    private void validateTransactionMessage(TransactionMessageHeader header) throws RpcCommandException{
        try {
            Validate.notNull(header, "Param error: messageHeader can not be null");
            Validate.notNull(header.getMqType(), "Param error: mqType can not be null");
            if(header.getTransactionState() != null && header.getTransactionState() != TransactionState.PREPARE){
                Validate.notNull(header.getTransactionId(),
                        "Param error: transactionId can not be null when transactionState is " + header.getTransactionState());
            }
        } catch (Exception e) {
            throw new RpcCommandException("Param error: " + e.getMessage());
        }
    }

    private MQMessageHandler getHandler(MQType mqType){
        return MQMessageHandlerFactory.getInstance().getAndCreate(mqType, this.serverController);
    }

}
