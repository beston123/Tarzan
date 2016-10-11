package com.tongbanjie.tevent.rpc.codec;

import com.tongbanjie.tevent.rpc.util.RpcHelper;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/27
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class NettyEncoder extends MessageToByteEncoder<RpcCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyEncoder.class);

    @Override
    public void encode(ChannelHandlerContext ctx, RpcCommand rpcCommand, ByteBuf out)
            throws Exception {
        try {
            ByteBuffer header = rpcCommand.encodeHeader();
            out.writeBytes(header);
            byte[] body = rpcCommand.getBody();
            if (body != null) {
                out.writeBytes(body);
            }
        } catch (Exception e) {
            LOGGER.error("encode exception, " + RpcHelper.parseChannelRemoteAddr(ctx.channel()), e);
            if (rpcCommand != null) {
                LOGGER.error(rpcCommand.toString());
            }
            RpcHelper.closeChannel(ctx.channel());
        }
    }
}
