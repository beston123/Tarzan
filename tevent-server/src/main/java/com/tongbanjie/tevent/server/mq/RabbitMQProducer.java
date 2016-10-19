package com.tongbanjie.tevent.server.mq;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.server.ServerController;
import io.netty.channel.ChannelHandlerContext;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
public class RabbitMQProducer implements EventProducer {

    private final ServerController serverController;

    public RabbitMQProducer(ServerController serverController) {
        this.serverController = serverController;
    }

    @Override
    public RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request) throws RpcCommandException, MQClientException {
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
}
