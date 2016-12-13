package com.tongbanjie.tarzan.rpc.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * RPC 帮助类 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/27
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RpcHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcHelper.class);

    public static final String RpcLogName = "TEventRpc";

    public static final String OS_NAME = System.getProperty("os.name");

    private RpcHelper(){}

    public static String exceptionToString(final Throwable e) {
        StringBuffer sb = new StringBuffer();
        if (e != null) {
            sb.append(e.toString());

            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                StackTraceElement elment = stackTrace[0];
                sb.append(", ");
                sb.append(elment.toString());
            }
        }

        return sb.toString();
    }

    /**
     * 获取Channel的远程地址
     * @param channel
     * @return
     */
    public static String parseChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        final SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }

            return addr;
        }

        return "";
    }

    public static String parseSocketAddressAddr(SocketAddress socketAddress) {
        if (socketAddress != null) {
            final String addr = socketAddress.toString();

            if (addr.length() > 0) {
                return addr.substring(1);
            }
        }
        return "";
    }

    public static SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
        return isa;
    }

    public static void closeChannel(Channel channel) {
        final String addrRemote = RpcHelper.parseChannelRemoteAddr(channel);
        channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                LOGGER.info("closeChannel: close the connection to remote address[{}] result: {}", addrRemote,
                        future.isSuccess());
            }
        });
    }


    public static boolean isLinuxPlatform() {
        if (OS_NAME != null && OS_NAME.toLowerCase().indexOf("linux") >= 0) {
            return true;
        }
        return false;
    }
}
