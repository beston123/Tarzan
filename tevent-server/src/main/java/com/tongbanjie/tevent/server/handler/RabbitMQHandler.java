package com.tongbanjie.tevent.server.handler;

import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.header.QueryMessageHeader;
import com.tongbanjie.tevent.server.ServerController;
import io.netty.channel.ChannelHandlerContext;

/**
 * RabbitMQ消息处理者 <p>
 * 〈功能详细描述〉
 * TODO
 *
 * @author zixiao
 * @date 16/9/30
 */
public class RabbitMQHandler implements MQMessageHandler {

    private final ServerController serverController;

    public RabbitMQHandler(ServerController serverController) {
        this.serverController = serverController;
    }

    @Override
    public RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request){
        return null;
    }

    @Override
    public Result<String> sendMessage(MQMessage mqMessage) {
        return null;
    }

    @Override
    public RpcCommand prepareMessage(ChannelHandlerContext ctx, RpcCommand request) {
        return null;
    }

    @Override
    public RpcCommand commitMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId) {
        return null;
    }

    @Override
    public RpcCommand rollbackMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId) {
        return null;
    }

    @Override
    public RpcCommand unknownMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId) {
        return null;
    }

    @Override
    public RpcCommand queryMessage(ChannelHandlerContext ctx, QueryMessageHeader queryMessageHeader) {
        return null;
    }
}
