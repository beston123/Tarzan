package com.tongbanjie.tevent.admin.component;

import com.tongbanjie.tevent.client.ClientException;
import com.tongbanjie.tevent.common.Service;
import com.tongbanjie.tevent.common.ServiceState;
import com.tongbanjie.tevent.common.util.NamedSingleThreadFactory;
import com.tongbanjie.tevent.common.util.NamedThreadFactory;
import com.tongbanjie.tevent.registry.ServerAddress;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.exception.RpcConnectException;
import com.tongbanjie.tevent.common.exception.RpcException;
import com.tongbanjie.tevent.rpc.netty.NettyClientConfig;
import com.tongbanjie.tevent.rpc.netty.NettyRpcClient;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * Admin 控制器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
@Component
public class AdminServerComponent implements Service, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServerComponent.class);

    /********************** 配置 ***********************/
    @Value("${tevent.registry.address}")
    private String zkAddress;

    private final NettyClientConfig nettyClientConfig;

    /********************** 服务 ***********************/
    @Autowired
    private AdminDiscovery adminDiscovery;

    //远程通信层对象
    private RpcClient rpcClient;

    /********************** 线程池 ***********************/
    // 处理发送消息线程池
    private ExecutorService sendMessageExecutor;

    // 对消息写入进行流控
    private final BlockingQueue<Runnable> sendThreadPoolQueue;

    //客户端定时线程
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
            new NamedSingleThreadFactory("ClientScheduledThread"));

    /********************** 服务状态 ***********************/
    private ServiceState serviceState = ServiceState.NOT_START;

    public AdminServerComponent() {
        this.nettyClientConfig = new NettyClientConfig();

        this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(1000);

        this.rpcClient = new NettyRpcClient(this.nettyClientConfig);
    }

    private void registerProcessor() {
        this.sendMessageExecutor = new ThreadPoolExecutor(//
                4,//
                4,//
                1000 * 60,//
                TimeUnit.MILLISECONDS,//
                this.sendThreadPoolQueue,//
                new NamedThreadFactory("SendMessageThread_"));

    }

    public void start() throws ClientException {

        synchronized (this) {
            switch (this.serviceState) {
                case NOT_START:
                    serviceState = ServiceState.IN_STARTING;
                    try {
                        doStart();
                        serviceState = ServiceState.RUNNING;
                        LOGGER.info("Client start successfully. ");
                    } catch (Exception e) {
                        serviceState = ServiceState.FAILED;
                        if(e instanceof ClientException){
                            throw (ClientException)e;
                        }else{
                            throw new ClientException("Client start exception,", e);
                        }
                    }
                    break;
                case IN_STARTING:
                case RUNNING:
                case FAILED:
                default:
                    LOGGER.warn("Client start concurrently. Current service state: " + this.serviceState);
                    break;
            }
        }

    }

    private void doStart() throws ClientException {
        //rpcClient
        if (this.rpcClient != null) {
            this.rpcClient.start();
        }

        //this.registerProcessor();

        //start Registry
        try {
            adminDiscovery.setZkAddress(zkAddress);
            adminDiscovery.start();
        } catch (Exception e) {
            throw new ClientException("The registry connect failed, address: " + zkAddress, e);
        }

    }


    public void shutdown() {
        if (this.rpcClient != null) {
            this.rpcClient.shutdown();
        }

        if (this.sendMessageExecutor != null) {
            this.sendMessageExecutor.shutdown();
        }

        if(this.adminDiscovery != null){
            this.adminDiscovery.shutdown();
        }

        if(this.scheduledExecutorService != null){
            this.scheduledExecutorService.shutdown();
        }
    }

    public NettyClientConfig getNettyClientConfig() {
        return nettyClientConfig;
    }

    public ServiceState getServiceState() {
        return serviceState;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    /**
     * 同步调用
     * @param request
     * @return
     * @throws RpcException
     */
    public RpcCommand invokeSync(RpcCommand request, long timeOut) throws RpcException {
        ServerAddress address = adminDiscovery.getOneServer();
        if(address == null){
            throw new RpcConnectException("Have no available tevent servers.");
        }
        try {
            return this.rpcClient.invokeSync(address.getAddress(), request, timeOut);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
