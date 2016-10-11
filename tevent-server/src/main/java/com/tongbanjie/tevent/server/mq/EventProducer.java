package com.tongbanjie.tevent.server.mq;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface EventProducer {

    RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request) throws RpcCommandException, MQClientException;

    RpcCommand prepareMessage(ChannelHandlerContext ctx, RpcCommand request);

    RpcCommand commitMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId);

    RpcCommand rollbackMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId);

}
