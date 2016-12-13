/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.tongbanjie.tarzan.rpc.protocol;

import com.tongbanjie.tarzan.rpc.util.RpcHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Netty编码器<p>
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
