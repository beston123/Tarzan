/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tongbanjie.tevent.rpc.netty;


import com.tongbanjie.tevent.rpc.InvokeCallback;
import com.tongbanjie.tevent.rpc.ResponseFuture;
import com.tongbanjie.tevent.rpc.RpcHook;
import com.tongbanjie.tevent.rpc.protocol.ResponseCode;
import com.tongbanjie.tevent.rpc.exception.RpcSendRequestException;
import com.tongbanjie.tevent.rpc.exception.RpcTimeoutException;
import com.tongbanjie.tevent.rpc.exception.RpcTooMuchRequestException;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.util.RpcHelper;
import com.tongbanjie.tevent.common.util.OnceSemaphore;
import com.tongbanjie.tevent.common.util.ServiceThread;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.*;


public abstract class NettyRpcAbstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRpcAbstract.class);

    protected final Semaphore semaphoreOneway;

    protected final Semaphore semaphoreAsync;

    protected final ConcurrentHashMap<Integer /* opaque */, ResponseFuture> responseTable =
            new ConcurrentHashMap<Integer, ResponseFuture>(256);

    protected final HashMap<Integer/* request code */, Pair<NettyRequestProcessor, ExecutorService>> processorTable =
            new HashMap<Integer, Pair<NettyRequestProcessor, ExecutorService>>(64);

    protected final NettyEventExecutor nettyEventExecutor = new NettyEventExecutor();

    protected Pair<NettyRequestProcessor, ExecutorService> defaultRequestProcessor;


    public NettyRpcAbstract(final int permitsOneway, final int permitsAsync) {
        this.semaphoreOneway = new Semaphore(permitsOneway, true);
        this.semaphoreAsync = new Semaphore(permitsAsync, true);
    }

    public abstract ChannelEventListener getChannelEventListener();

    public abstract RpcHook getRpcHook();

    public abstract ExecutorService getCallbackExecutor();

    public void putNettyEvent(final NettyEvent event) {
        this.nettyEventExecutor.putNettyEvent(event);
    }

    public void processMessageReceived(ChannelHandlerContext ctx, RpcCommand msg) throws Exception {
        final RpcCommand cmd = msg;
        if (cmd != null) {
            switch (cmd.getCmdType()) {
                case RpcCommand.REQUEST_COMMAND:
                    processRequestCommand(ctx, cmd);
                    break;
                case RpcCommand.RESPONSE_COMMAND:
                    processResponseCommand(ctx, cmd);
                    break;
                default:
                    break;
            }
        }
    }

    public void processRequestCommand(final ChannelHandlerContext ctx, final RpcCommand cmd) {
        final Pair<NettyRequestProcessor, ExecutorService> matched = this.processorTable.get(cmd.getCmdCode());
        final Pair<NettyRequestProcessor, ExecutorService> pair = null == matched ? this.defaultRequestProcessor : matched;
        final int opaque = cmd.getOpaque();

        if (pair != null) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        RpcHook rpcHook = NettyRpcAbstract.this.getRpcHook();
                        if (rpcHook != null) {
                            rpcHook.doBeforeRequest(RpcHelper.parseChannelRemoteAddr(ctx.channel()), cmd);
                        }

                        final RpcCommand response = pair.getValue0().processRequest(ctx, cmd);

                        if (rpcHook != null) {
                            rpcHook.doAfterResponse(RpcHelper.parseChannelRemoteAddr(ctx.channel()), cmd, response);
                        }

                        if (!cmd.isOneWayRpc()) {
                            if (response != null) {
                                response.setOpaque(opaque);
                                response.setCmdType(RpcCommand.RESPONSE_COMMAND);
                                try {
                                    ctx.writeAndFlush(response);
                                } catch (Throwable e) {
                                    LOGGER.error("process request over, but response failed", e);
                                    LOGGER.error(cmd.toString());
                                    LOGGER.error(response.toString());
                                }
                            } else {

                            }
                        }
                    } catch (Throwable e) {
                        LOGGER.error("process request exception", e);
                        LOGGER.error(cmd.toString());

                        if (!cmd.isOneWayRpc()) {
                            final RpcCommand response = RpcCommandBuilder.buildResponse(ResponseCode.SYSTEM_ERROR, //
                                    RpcHelper.exceptionToString(e));
                            response.setOpaque(opaque);
                            ctx.writeAndFlush(response);
                        }
                    }
                }
            };

            try {
                pair.getValue1().submit(run);
            } catch (RejectedExecutionException e) {
                if ((System.currentTimeMillis() % 10000) == 0) {
                    LOGGER.warn(RpcHelper.parseChannelRemoteAddr(ctx.channel()) //
                            + ", too many requests and system thread pool busy, RejectedExecutionException " //
                            + pair.getValue1().toString() //
                            + " request code: " + cmd.getCmdCode());
                }

                if (!cmd.isOneWayRpc()) {
                    final RpcCommand response = RpcCommandBuilder.buildResponse(ResponseCode.SYSTEM_BUSY,
                            "[OVERLOAD]system busy, start flow control for a while");
                    response.setOpaque(opaque);
                    ctx.writeAndFlush(response);
                }
            }
        } else {
            String error = " request type " + cmd.getCmdCode() + " not supported";
            final RpcCommand response =
                    RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST, error);
            response.setOpaque(opaque);
            ctx.writeAndFlush(response);
            LOGGER.error(RpcHelper.parseChannelRemoteAddr(ctx.channel()) + error);
        }
    }

    public void processResponseCommand(ChannelHandlerContext ctx, RpcCommand cmd) {
        final int opaque = cmd.getOpaque();
        final ResponseFuture responseFuture = responseTable.get(opaque);
        if (responseFuture != null) {
            responseFuture.setResponseCommand(cmd);

            responseFuture.release();

            responseTable.remove(opaque);

            if (responseFuture.getInvokeCallback() != null) {
                boolean runInThisThread = false;
                ExecutorService executor = this.getCallbackExecutor();
                if (executor != null) {
                    try {
                        executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    responseFuture.executeInvokeCallback();
                                } catch (Throwable e) {
                                    LOGGER.warn("execute callback in executor exception, and callback throw", e);
                                }
                            }
                        });
                    } catch (Exception e) {
                        runInThisThread = true;
                        LOGGER.warn("execute callback in executor exception, maybe executor busy", e);
                    }
                } else {
                    runInThisThread = true;
                }

                if (runInThisThread) {
                    try {
                        responseFuture.executeInvokeCallback();
                    } catch (Throwable e) {
                        LOGGER.warn("executeInvokeCallback Exception", e);
                    }
                }
            } else {
                responseFuture.putResponse(cmd);
            }
        } else {
            LOGGER.warn("receive response, but not matched any request, " + RpcHelper.parseChannelRemoteAddr(ctx.channel()));
            LOGGER.warn(cmd.toString());
        }
    }

    public void scanResponseTable() {
        final List<ResponseFuture> rfList = new LinkedList<ResponseFuture>();
        Iterator<Entry<Integer, ResponseFuture>> it = this.responseTable.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, ResponseFuture> next = it.next();
            ResponseFuture rep = next.getValue();

            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + 1000) <= System.currentTimeMillis()) {
                rep.release();
                it.remove();
                rfList.add(rep);
                LOGGER.warn("remove timeout request, " + rep);
            }
        }

        for (ResponseFuture rf : rfList) {
            try {
                rf.executeInvokeCallback();
            } catch (Throwable e) {
                LOGGER.warn("scanResponseTable, operationComplete Exception", e);
            }
        }
    }

    public RpcCommand invokeSyncImpl(final Channel channel, final RpcCommand request, final long timeoutMillis)
            throws InterruptedException, RpcSendRequestException, RpcTimeoutException {
        final int opaque = request.getOpaque();

        try {
            final ResponseFuture responseFuture = new ResponseFuture(opaque, timeoutMillis, null, null);
            this.responseTable.put(opaque, responseFuture);
            final SocketAddress addr = channel.remoteAddress();
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    if (f.isSuccess()) {
                        responseFuture.setSendRequestOK(true);
                        return;
                    } else {
                        responseFuture.setSendRequestOK(false);
                    }

                    responseTable.remove(opaque);
                    responseFuture.setCause(f.cause());
                    responseFuture.putResponse(null);
                    LOGGER.warn("send a request command to channel <" + addr + "> failed.");
                }
            });

            RpcCommand responseCommand = responseFuture.waitResponse(timeoutMillis);
            if (null == responseCommand) {
                if (responseFuture.isSendRequestOK()) {
                    throw new RpcTimeoutException(RpcHelper.parseSocketAddressAddr(addr), timeoutMillis,
                            responseFuture.getCause());
                } else {
                    throw new RpcSendRequestException(RpcHelper.parseSocketAddressAddr(addr), responseFuture.getCause());
                }
            }

            return responseCommand;
        } finally {
            this.responseTable.remove(opaque);
        }
    }

    public void invokeAsyncImpl(final Channel channel, final RpcCommand request, final long timeoutMillis,
                                final InvokeCallback invokeCallback)
            throws InterruptedException, RpcTooMuchRequestException, RpcTimeoutException, RpcSendRequestException {
        final int opaque = request.getOpaque();
        boolean acquired = this.semaphoreAsync.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        if (acquired) {
            final OnceSemaphore once = new OnceSemaphore(this.semaphoreAsync);

            final ResponseFuture responseFuture = new ResponseFuture(opaque, timeoutMillis, invokeCallback, once);
            this.responseTable.put(opaque, responseFuture);
            try {
                channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture f) throws Exception {
                        if (f.isSuccess()) {
                            responseFuture.setSendRequestOK(true);
                            return;
                        } else {
                            responseFuture.setSendRequestOK(false);
                        }

                        responseFuture.putResponse(null);
                        responseTable.remove(opaque);
                        try {
                            responseFuture.executeInvokeCallback();
                        } catch (Throwable e) {
                            LOGGER.warn("execute callback in writeAndFlush addListener, and callback throw", e);
                        } finally {
                            responseFuture.release();
                        }

                        LOGGER.warn("send a request command to channel <{}> failed.", RpcHelper.parseChannelRemoteAddr(channel));
                    }
                });
            } catch (Exception e) {
                responseFuture.release();
                LOGGER.warn("send a request command to channel <" + RpcHelper.parseChannelRemoteAddr(channel) + "> Exception", e);
                throw new RpcSendRequestException(RpcHelper.parseChannelRemoteAddr(channel), e);
            }
        } else {
            String info =
                    String.format("invokeAsyncImpl tryAcquire semaphore timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d", //
                            timeoutMillis, //
                            this.semaphoreAsync.getQueueLength(), //
                            this.semaphoreAsync.availablePermits()//
                    );
            LOGGER.warn(info);
            throw new RpcTooMuchRequestException(info);
        }
    }

    public void invokeOnewayImpl(final Channel channel, final RpcCommand request, final long timeoutMillis)
            throws InterruptedException, RpcTooMuchRequestException, RpcTimeoutException, RpcSendRequestException {
        request.setOneWayRpc(true);
        boolean acquired = this.semaphoreOneway.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        if (acquired) {
            final OnceSemaphore once = new OnceSemaphore(this.semaphoreOneway);
            try {
                channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture f) throws Exception {
                        once.release();
                        if (!f.isSuccess()) {
                            LOGGER.warn("send a request command to channel <" + channel.remoteAddress() + "> failed.");
                        }
                    }
                });
            } catch (Exception e) {
                once.release();
                LOGGER.warn("write send a request command to channel <" + channel.remoteAddress() + "> failed.");
                throw new RpcSendRequestException(RpcHelper.parseChannelRemoteAddr(channel), e);
            }
        } else {
            if (timeoutMillis <= 0) {
                throw new RpcTooMuchRequestException("invokeOnewayImpl invoke too fast");
            } else {
                String info = String.format(
                        "invokeOnewayImpl tryAcquire semaphore timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d", //
                        timeoutMillis, //
                        this.semaphoreAsync.getQueueLength(), //
                        this.semaphoreAsync.availablePermits()//
                );
                LOGGER.warn(info);
                throw new RpcTimeoutException(info);
            }
        }
    }

    class NettyEventExecutor extends ServiceThread {
        private final LinkedBlockingQueue<NettyEvent> eventQueue = new LinkedBlockingQueue<NettyEvent>();
        private final int maxSize = 10000;


        public void putNettyEvent(final NettyEvent event) {
            if (this.eventQueue.size() <= maxSize) {
                this.eventQueue.add(event);
            } else {
                LOGGER.warn("event queue size[{}] enough, so drop this event {}", this.eventQueue.size(), event.toString());
            }
        }

        @Override
        public void run() {
            LOGGER.info(this.getServiceName() + " service started");

            final ChannelEventListener listener = NettyRpcAbstract.this.getChannelEventListener();

            while (!this.isStopped()) {
                try {
                    NettyEvent event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (event != null && listener != null) {
                        switch (event.getType()) {
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddr(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddr(), event.getChannel());
                                break;
                            default:
                                break;

                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn(this.getServiceName() + " service has exception. ", e);
                }
            }

            LOGGER.info(this.getServiceName() + " service end");
        }


        @Override
        public String getServiceName() {
            return NettyEventExecutor.class.getSimpleName();
        }
    }


}
