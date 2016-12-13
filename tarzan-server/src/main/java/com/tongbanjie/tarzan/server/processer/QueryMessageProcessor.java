package com.tongbanjie.tarzan.server.processer;

import com.tongbanjie.tarzan.server.ServerController;
import com.tongbanjie.tarzan.server.handler.MQMessageHandler;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;
import com.tongbanjie.tarzan.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.QueryMessageHeader;
import com.tongbanjie.tarzan.server.handler.MQMessageHandlerFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈消息查询处理器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/22
 */
public class QueryMessageProcessor implements NettyRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageProcessor.class);

    private final ServerController serverController;

    public QueryMessageProcessor(final ServerController serverController) {
        this.serverController = serverController;
    }

    @Override
    public RpcCommand processRequest(ChannelHandlerContext ctx, RpcCommand request) throws Exception {
        switch (request.getCmdCode()) {
            case RequestCode.QUERY_MESSAGE:
                return this.queryMessage(ctx, request);
            default:
                LOGGER.warn("Invalid request，requestCode："+request.getCmdCode());
                break;
        }
        return RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST,
                "Invalid request，requestCode：" + request.getCmdCode());
    }

    private RpcCommand queryMessage(ChannelHandlerContext ctx, RpcCommand request) throws RpcCommandException {
        //1、解析消息头
        QueryMessageHeader messageHeader = (QueryMessageHeader)request.decodeCustomHeader(QueryMessageHeader.class);
        validateMessage(messageHeader);

        //2、获取事件处理者
        MQMessageHandler producer = getHandler(messageHeader.getMqType());
        if(producer == null){
            return RpcCommandBuilder.buildResponse(ResponseCode.SYSTEM_ERROR,
                    "System error：can not find a producer to handle the message {}" + messageHeader);
        }
        //3、处理事件
        return producer.queryMessage(ctx, messageHeader);
    }

    private void validateMessage(QueryMessageHeader header) throws RpcCommandException{
        if(header == null){
            throw new RpcCommandException("Param error: messageHeader can not be null");
        }
        if(header.getMqType() == null){
            throw new RpcCommandException("Param error: mqType can not be null");
        }
    }

    private MQMessageHandler getHandler(MQType mqType){
        return MQMessageHandlerFactory.getInstance().getAndCreate(mqType, this.serverController);
    }
}
