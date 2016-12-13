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
package com.tongbanjie.tarzan.rpc;

import com.tongbanjie.tarzan.rpc.exception.RpcSendRequestException;
import com.tongbanjie.tarzan.rpc.exception.RpcTimeoutException;
import com.tongbanjie.tarzan.rpc.exception.RpcTooMuchRequestException;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.netty.NettyRequestProcessor;
import io.netty.channel.Channel;
import org.javatuples.Pair;

import java.util.concurrent.ExecutorService;

public interface RpcServer extends RpcService {

    /****************************** invoke 方法 *****************************/

    RpcCommand invokeSync(final Channel channel, final RpcCommand request, final long timeoutMillis)
            throws InterruptedException, RpcTooMuchRequestException, RpcSendRequestException, RpcTimeoutException;


    void invokeAsync(final Channel channel, final RpcCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback)
            throws InterruptedException, RpcTimeoutException, RpcTooMuchRequestException, RpcSendRequestException;


    void invokeOneWay(final Channel channel, final RpcCommand request, final long timeoutMillis)
            throws InterruptedException, RpcTimeoutException, RpcTooMuchRequestException, RpcSendRequestException;

    /****************************** 处理器注册 *****************************/

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                           final ExecutorService executor);

    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);

    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);

    /****************************** 本地监听端口 *****************************/

    int localListenPort();

}
