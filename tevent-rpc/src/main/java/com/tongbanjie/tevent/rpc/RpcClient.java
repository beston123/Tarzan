package com.tongbanjie.tevent.rpc;

import com.tongbanjie.tevent.rpc.exception.RpcConnectException;
import com.tongbanjie.tevent.rpc.exception.RpcSendRequestException;
import com.tongbanjie.tevent.rpc.exception.RpcTimeoutException;
import com.tongbanjie.tevent.rpc.exception.RpcTooMuchRequestException;
import com.tongbanjie.tevent.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;

import java.util.concurrent.ExecutorService;


public interface RpcClient extends RpcService {

    /****************************** invoke 方法 *****************************/

    RpcCommand invokeSync(String addr, final RpcCommand request,
                                      long timeoutMillis)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcSendRequestException, RpcTimeoutException;


    void invokeAsync(final String addr, final RpcCommand request, final long timeoutMillis,
                            final InvokeCallback invokeCallback)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException;


    void invokeOneWay(final String addr, final RpcCommand request, final long timeoutMillis)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException;


    /****************************** 处理器注册 *****************************/

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                                  final ExecutorService executor);

    /****************************** 更新服务器地址 *****************************/

    boolean isChannelWritable(final String addr);
}
