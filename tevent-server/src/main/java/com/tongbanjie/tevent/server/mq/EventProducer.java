package com.tongbanjie.tevent.server.mq;

import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * 发送者接口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
public interface EventProducer {

    RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request);

    RpcCommand prepareMessage(ChannelHandlerContext ctx, RpcCommand request);

    RpcCommand commitMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId);

    RpcCommand rollbackMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId);

    RpcCommand unknownMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId);
}
