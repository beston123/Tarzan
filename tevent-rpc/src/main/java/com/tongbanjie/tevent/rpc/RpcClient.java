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
package com.tongbanjie.tevent.rpc;

import com.tongbanjie.tevent.rpc.exception.RpcConnectException;
import com.tongbanjie.tevent.rpc.exception.RpcSendRequestException;
import com.tongbanjie.tevent.rpc.exception.RpcTimeoutException;
import com.tongbanjie.tevent.rpc.exception.RpcTooMuchRequestException;
import com.tongbanjie.tevent.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;

import java.util.List;
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


    void invokeOneway(final String addr, final RpcCommand request, final long timeoutMillis)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException;


    /****************************** 处理器注册 *****************************/

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                                  final ExecutorService executor);

    /****************************** 更新服务器地址 *****************************/

    boolean isChannelWritable(final String addr);
}
