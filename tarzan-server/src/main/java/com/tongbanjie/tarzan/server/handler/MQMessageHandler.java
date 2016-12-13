package com.tongbanjie.tarzan.server.handler;

import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.message.MQMessage;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.header.QueryMessageHeader;
import io.netty.channel.ChannelHandlerContext;

/**
 * MQ消息处理者接口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
public interface MQMessageHandler {

    RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request);

    Result<String> sendMessage(MQMessage mqMessage);

    RpcCommand prepareMessage(ChannelHandlerContext ctx, RpcCommand request);

    RpcCommand commitMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId);

    RpcCommand rollbackMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId);

    RpcCommand unknownMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId);

    RpcCommand queryMessage(ChannelHandlerContext ctx, QueryMessageHeader queryMessageHeader);
}
