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
package com.tongbanjie.tevent.rpc.netty;


import com.tongbanjie.tevent.rpc.InvokeCallback;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.RpcHook;
import com.tongbanjie.tevent.rpc.protocol.NettyDecoder;
import com.tongbanjie.tevent.rpc.protocol.NettyEncoder;
import com.tongbanjie.tevent.rpc.exception.RpcConnectException;
import com.tongbanjie.tevent.rpc.exception.RpcSendRequestException;
import com.tongbanjie.tevent.rpc.exception.RpcTimeoutException;
import com.tongbanjie.tevent.rpc.exception.RpcTooMuchRequestException;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.util.RpcHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NettyRpcClient extends NettyRpcAbstract implements RpcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRpcClient.class);

    //锁超时时间
    private static final long LockTimeoutMillis = 3000;

    /******************************** Netty *******************************/
    private final NettyClientConfig nettyClientConfig;
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroupWorker;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private final ConcurrentHashMap<String /* addr */, ChannelWrapper> channelTables = new ConcurrentHashMap<String, ChannelWrapper>();
    private final Lock lockChannelTables = new ReentrantLock();

    /******************************** 线程池 *******************************/
    private final Timer timer = new Timer("ClientHouseKeepingService", true);
    //共用线程池
    private final ExecutorService publicExecutor;

    /******************************** 监听器 *******************************/
    private final ChannelEventListener channelEventListener;

    /******************************** 其他 *******************************/
    private RpcHook rpcHook;

    public NettyRpcClient(final NettyClientConfig nettyClientConfig) {
        this(nettyClientConfig, null);
    }

    public NettyRpcClient(final NettyClientConfig nettyClientConfig, //
                          final ChannelEventListener channelEventListener) {
        super(nettyClientConfig.getClientOnewaySemaphoreValue(), nettyClientConfig.getClientAsyncSemaphoreValue());
        this.nettyClientConfig = nettyClientConfig;
        this.channelEventListener = channelEventListener;

        int publicThreadNums = nettyClientConfig.getClientCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }

        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);


            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyClientPublicExecutor_" + this.threadIndex.incrementAndGet());
            }
        });

        this.eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);


            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyClientSelector_%d", this.threadIndex.incrementAndGet()));
            }
        });
    }

    @Override
    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(//
                nettyClientConfig.getClientWorkerThreads(), //
                new ThreadFactory() {

                    private AtomicInteger threadIndex = new AtomicInteger(0);


                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyClientWorkerThread_" + this.threadIndex.incrementAndGet());
                    }
                });

        this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)//
                //
                .option(ChannelOption.TCP_NODELAY, true)
                //
                .option(ChannelOption.SO_KEEPALIVE, false)
                //
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
                //
                .option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
                //
                .option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
                //
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(//
                                defaultEventExecutorGroup, //
                                new NettyEncoder(), //
                                new NettyDecoder(), //
                                new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds()), //
                                new NettyConnectManageHandler(), //
                                new NettyClientHandler());
                    }
                });

        this.timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    NettyRpcClient.this.scanResponseTable();
                } catch (Exception e) {
                    LOGGER.error("scanResponseTable exception", e);
                }
            }
        }, 1000 * 3, 1000);

        if (this.channelEventListener != null) {
            this.nettyEventExecutor.start();
        }
    }

    @Override
    public void shutdown() {
        try {
            this.timer.cancel();

            for (ChannelWrapper cw : this.channelTables.values()) {
                this.closeChannel(null, cw.getChannel());
            }

            this.channelTables.clear();

            this.eventLoopGroupWorker.shutdownGracefully();

            if (this.nettyEventExecutor != null) {
                this.nettyEventExecutor.shutdown();
            }

            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            LOGGER.error("NettyRpcClient shutdown exception, ", e);
        }

        if (this.publicExecutor != null) {
            try {
                this.publicExecutor.shutdown();
            } catch (Exception e) {
                LOGGER.error("NettyRpcClient shutdown exception, ", e);
            }
        }
    }

    public void closeChannel(final String addr, final Channel channel) {
        if (null == channel)
            return;

        final String addrRemote = null == addr ? RpcHelper.parseChannelRemoteAddr(channel) : addr;

        try {
            if (this.lockChannelTables.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    final ChannelWrapper prevCW = this.channelTables.get(addrRemote);

                    LOGGER.info("closeChannel: begin close the channel[{}] Found: {}", addrRemote, prevCW != null);

                    if (null == prevCW) {
                        LOGGER.info("closeChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    } else if (prevCW.getChannel() != channel) {
                        LOGGER.info("closeChannel: the channel[{}] has been closed before, and has been created again, nothing to do.",
                                addrRemote);
                        removeItemFromTable = false;
                    }

                    if (removeItemFromTable) {
                        this.channelTables.remove(addrRemote);
                        LOGGER.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                    }

                    RpcHelper.closeChannel(channel);
                } catch (Exception e) {
                    LOGGER.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                LOGGER.warn("closeChannel: try to lock channel table, but timeout, {}ms", LockTimeoutMillis);
            }
        } catch (InterruptedException e) {
            LOGGER.error("closeChannel exception", e);
        }
    }

    public void closeChannel(final Channel channel) {
        if (null == channel)
            return;

        try {
            if (this.lockChannelTables.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    ChannelWrapper prevCW = null;
                    String addrRemote = null;
                    for (Map.Entry<String, ChannelWrapper> entry : channelTables.entrySet()) {
                        String key = entry.getKey();
                        ChannelWrapper prev = entry.getValue();
                        if (prev.getChannel() != null) {
                            if (prev.getChannel() == channel) {
                                prevCW = prev;
                                addrRemote = key;
                                break;
                            }
                        }
                    }

                    if (null == prevCW) {
                        LOGGER.info("eventCloseChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    }

                    if (removeItemFromTable) {
                        this.channelTables.remove(addrRemote);
                        LOGGER.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                        RpcHelper.closeChannel(channel);
                    }
                } catch (Exception e) {
                    LOGGER.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                LOGGER.warn("closeChannel: try to lock channel table, but timeout, {}ms", LockTimeoutMillis);
            }
        } catch (InterruptedException e) {
            LOGGER.error("closeChannel exception", e);
        }
    }


    @Override
    public RpcCommand invokeSync(String addr, final RpcCommand request, long timeoutMillis)
            throws InterruptedException, RpcConnectException, RpcSendRequestException, RpcTimeoutException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                if (this.rpcHook != null) {
                    this.rpcHook.doBeforeRequest(addr, request);
                }
                RpcCommand response = this.invokeSyncImpl(channel, request, timeoutMillis);
                if (this.rpcHook != null) {
                    this.rpcHook.doAfterResponse(RpcHelper.parseChannelRemoteAddr(channel), request, response);
                }
                return response;
            } catch (RpcSendRequestException e) {
                LOGGER.warn("invokeSync: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr, channel);
                throw e;
            } catch (RpcTimeoutException e) {
                if (nettyClientConfig.isClientCloseSocketIfTimeout()) {
                    this.closeChannel(addr, channel);
                    LOGGER.warn("invokeSync: close socket because of timeout, {}ms, {}", timeoutMillis, addr);
                }
                LOGGER.warn("invokeSync: wait response timeout exception, the channel[{}]", addr);
                throw e;
            }
        } else {
            this.closeChannel(addr, channel);
            throw new RpcConnectException(addr);
        }
    }

    private Channel getAndCreateChannel(final String addr) throws InterruptedException {
        if (null == addr) {
            //TODO 是否需要重新获取服务端一次？
            return null;
        }
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }

        return this.createChannel(addr);
    }

    private Channel createChannel(final String addr) throws InterruptedException {
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }

        if (this.lockChannelTables.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
            try {
                boolean createNewConnection = false;
                cw = this.channelTables.get(addr);
                if (cw != null) {

                    if (cw.isOK()) {
                        return cw.getChannel();
                    }

                    else if (!cw.getChannelFuture().isDone()) {
                        createNewConnection = false;
                    }

                    else {
                        this.channelTables.remove(addr);
                        createNewConnection = true;
                    }
                }

                else {
                    createNewConnection = true;
                }

                if (createNewConnection) {
                    ChannelFuture channelFuture = this.bootstrap.connect(RpcHelper.string2SocketAddress(addr));
                    LOGGER.info("createChannel: begin to connect remote host[{}] asynchronously", addr);
                    cw = new ChannelWrapper(channelFuture);
                    this.channelTables.put(addr, cw);
                }
            } catch (Exception e) {
                LOGGER.error("createChannel: create channel exception", e);
            } finally {
                this.lockChannelTables.unlock();
            }
        } else {
            LOGGER.warn("createChannel: try to lock channel table, but timeout, {}ms", LockTimeoutMillis);
        }

        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            if (channelFuture.awaitUninterruptibly(this.nettyClientConfig.getConnectTimeoutMillis())) {
                if (cw.isOK()) {
                    LOGGER.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                    return cw.getChannel();
                } else {
                    LOGGER.warn("createChannel: connect remote host[" + addr + "] failed, " + channelFuture.toString(), channelFuture.cause());
                }
            } else {
                LOGGER.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, this.nettyClientConfig.getConnectTimeoutMillis(),
                        channelFuture.toString());
            }
        }

        return null;
    }

    @Override
    public void invokeAsync(String addr, RpcCommand request, long timeoutMillis, InvokeCallback invokeCallback)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException, RpcTimeoutException,
            RpcSendRequestException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                if (this.rpcHook != null) {
                    this.rpcHook.doBeforeRequest(addr, request);
                }
                this.invokeAsyncImpl(channel, request, timeoutMillis, invokeCallback);
            } catch (RpcSendRequestException e) {
                LOGGER.warn("invokeAsync: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr, channel);
                throw e;
            }
        } else {
            this.closeChannel(addr, channel);
            throw new RpcConnectException(addr);
        }
    }

    @Override
    public void invokeOneway(String addr, RpcCommand request, long timeoutMillis) throws InterruptedException,
            RpcConnectException, RpcTooMuchRequestException, RpcTimeoutException, RpcSendRequestException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                if (this.rpcHook != null) {
                    this.rpcHook.doBeforeRequest(addr, request);
                }
                this.invokeOnewayImpl(channel, request, timeoutMillis);
            } catch (RpcSendRequestException e) {
                LOGGER.warn("invokeOneway: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr, channel);
                throw e;
            }
        } else {
            this.closeChannel(addr, channel);
            throw new RpcConnectException(addr);
        }
    }

    @Override
    public void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executor) {
        ExecutorService executorThis = executor;
        if (null == executor) {
            executorThis = this.publicExecutor;
        }

        Pair<NettyRequestProcessor, ExecutorService> pair = new Pair<NettyRequestProcessor, ExecutorService>(processor, executorThis);
        this.processorTable.put(requestCode, pair);
    }

    @Override
    public boolean isChannelWritable(String addr) {
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.isWritable();
        }
        return true;
    }

    @Override
    public ChannelEventListener getChannelEventListener() {
        return channelEventListener;
    }


    @Override
    public void registerRpcHook(RpcHook rpcHook) {
        this.rpcHook = rpcHook;
    }

    @Override
    public RpcHook getRpcHook() {
        return this.rpcHook;
    }

    @Override
    public ExecutorService getCallbackExecutor() {
        return this.publicExecutor;
    }


    static class ChannelWrapper {
        private final ChannelFuture channelFuture;


        public ChannelWrapper(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }


        public boolean isOK() {
            return this.channelFuture.channel() != null && this.channelFuture.channel().isActive();
        }


        public boolean isWritable() {
            return this.channelFuture.channel().isWritable();
        }


        private Channel getChannel() {
            return this.channelFuture.channel();
        }


        public ChannelFuture getChannelFuture() {
            return channelFuture;
        }
    }

    class NettyClientHandler extends SimpleChannelInboundHandler<RpcCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RpcCommand msg) throws Exception {
            processMessageReceived(ctx, msg);

        }
    }


    class NettyConnectManageHandler extends ChannelDuplexHandler {
        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
                throws Exception {
            final String local = localAddress == null ? "UNKNOW" : localAddress.toString();
            final String remote = remoteAddress == null ? "UNKNOW" : remoteAddress.toString();
            LOGGER.info("NETTY CLIENT PIPELINE: CONNECT  {} => {}", local, remote);
            super.connect(ctx, remoteAddress, localAddress, promise);

            if (NettyRpcClient.this.channelEventListener != null) {
                NettyRpcClient.this.putNettyEvent(new NettyEvent(NettyEventType.CONNECT, remoteAddress.toString(), ctx.channel()));
            }
        }


        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            final String remoteAddress = RpcHelper.parseChannelRemoteAddr(ctx.channel());
            LOGGER.info("NETTY CLIENT PIPELINE: DISCONNECT {}", remoteAddress);
            closeChannel(ctx.channel());
            super.disconnect(ctx, promise);

            if (NettyRpcClient.this.channelEventListener != null) {
                NettyRpcClient.this.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress.toString(), ctx.channel()));
            }
        }


        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            final String remoteAddress = RpcHelper.parseChannelRemoteAddr(ctx.channel());
            LOGGER.info("NETTY CLIENT PIPELINE: CLOSE {}", remoteAddress);
            closeChannel(ctx.channel());
            super.close(ctx, promise);

            if (NettyRpcClient.this.channelEventListener != null) {
                NettyRpcClient.this.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress.toString(), ctx.channel()));
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent evnet = (IdleStateEvent) evt;
                if (evnet.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = RpcHelper.parseChannelRemoteAddr(ctx.channel());
                    LOGGER.warn("NETTY CLIENT PIPELINE: IDLE exception [{}]", remoteAddress);
                    closeChannel(ctx.channel());
                    if (NettyRpcClient.this.channelEventListener != null) {
                        NettyRpcClient.this
                                .putNettyEvent(new NettyEvent(NettyEventType.IDLE, remoteAddress.toString(), ctx.channel()));
                    }
                }
            }

            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = RpcHelper.parseChannelRemoteAddr(ctx.channel());
            LOGGER.warn("NETTY CLIENT PIPELINE: exceptionCaught {}", remoteAddress);
            LOGGER.warn("NETTY CLIENT PIPELINE: exceptionCaught exception.", cause);
            closeChannel(ctx.channel());
            if (NettyRpcClient.this.channelEventListener != null) {
                NettyRpcClient.this.putNettyEvent(new NettyEvent(NettyEventType.EXCEPTION, remoteAddress.toString(), ctx.channel()));
            }
        }
    }
}
