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
package com.tongbanjie.tarzan.server.client;

import com.tongbanjie.tarzan.server.ServerController;
import com.tongbanjie.tarzan.common.ScheduledService;
import com.tongbanjie.tarzan.common.util.NamedSingleThreadFactory;
import com.tongbanjie.tarzan.rpc.netty.ChannelEventListener;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端Channel 管理服务<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/15
 */
public class ClientChannelManageService implements ScheduledService, ChannelEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientChannelManageService.class);

    private final ServerController serverController;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
            new NamedSingleThreadFactory("ClientChannelManageService"));

    public ClientChannelManageService(final ServerController serverController) {
        this.serverController = serverController;
    }

    @Override
    public void start() {
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    schedule();
                } catch (Exception e) {
                    LOGGER.error("ClientChannelManageService scanExceptionChannel exception ", e);
                }
            }
        }, 1000 * 10, 1000 * 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void schedule() throws Exception {
        this.scanExceptionChannel();
    }

    @Override
    public void shutdown() {
        this.scheduledExecutorService.shutdown();
    }


    private void scanExceptionChannel() {
        this.serverController.getClientManager().scanNotActiveChannel();
    }


    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {

    }


    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        this.serverController.getClientManager().doChannelCloseEvent(remoteAddr, channel);
    }


    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        this.serverController.getClientManager().doChannelCloseEvent(remoteAddr, channel);
    }


    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        this.serverController.getClientManager().doChannelCloseEvent(remoteAddr, channel);
    }
}
