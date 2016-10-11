package com.tongbanjie.tevent.client;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.validator.RocketMQValidators;
import com.tongbanjie.tevent.common.MQType;
import com.tongbanjie.tevent.common.TransactionState;
import com.tongbanjie.tevent.common.config.ServerConfig;
import com.tongbanjie.tevent.registry.ServiceDiscovery;
import com.tongbanjie.tevent.registry.zookeeper.ZkConstants;
import com.tongbanjie.tevent.registry.zookeeper.ZooKeeperServiceDiscovery;
import com.tongbanjie.tevent.registry.zookeeper.ZooKeeperServiceRegistry;
import com.tongbanjie.tevent.rpc.codec.SerializeType;
import com.tongbanjie.tevent.rpc.exception.*;
import com.tongbanjie.tevent.common.body.RocketMQBody;
import com.tongbanjie.tevent.rpc.protocol.header.MQMessageHeader;
import com.tongbanjie.tevent.rpc.protocol.header.RegisterRequestHeader;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.ResponseCode;
import com.tongbanjie.tevent.rpc.netty.NettyClientConfig;
import com.tongbanjie.tevent.rpc.netty.NettyRpcClient;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.header.TransactionMessageHeader;
import com.tongbanjie.tevent.rpc.protocol.heartbeat.HeartbeatData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.*;


/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ExampleClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleClient.class);

    private NettyRpcClient remotingClient;

    private ServiceDiscovery serviceDiscovery;

    private ServerConfig serverConfig = new ServerConfig();

    private String producerGroup = "ExampleClientGroup";

    private String clientId = "123444";

    private NettyClientConfig nettyClientConfig = new NettyClientConfig();

    private final ConcurrentHashMap<String/* Server Name */, HashMap<Long/* brokerId */, String/* address */>> serverAddrTable =
            new ConcurrentHashMap<String, HashMap<Long, String>>();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "MQClientFactoryScheduledThread");
        }
    });

    public void init(){
        serviceDiscovery = new ZooKeeperServiceDiscovery(serverConfig.getRegistryAddress());
        serviceDiscovery.start();

        remotingClient = new NettyRpcClient(nettyClientConfig, null);
        remotingClient.start();
        try {
            sendHeartbeat();
        } catch (RpcException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.startScheduledTask();
    }

    private void startScheduledTask(){
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    ExampleClient.this.cleanOfflineBroker();
                    ExampleClient.this.sendHeartbeatToAllServerWithLock();
                } catch (Exception e) {
                    LOGGER.error("ScheduledTask sendHeartbeatToAllServer exception", e);
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private void cleanOfflineBroker() {

    }

    private void sendHeartbeatToAllServerWithLock() throws RpcException, InterruptedException {
        sendHeartbeat();
    }


    public void sendHeartbeat() throws RpcException, InterruptedException {
        HeartbeatData heartbeatData = new HeartbeatData();
        heartbeatData.setClientId(clientId);
        heartbeatData.setGroup(producerGroup);
        sendHeartbeat(heartbeatData, 1000);
    }

    public void sendHeartbeat(final HeartbeatData heartbeatData,//
                              final long timeoutMillis//
    ) throws RpcException, InterruptedException {

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.HEART_BEAT, null);
        request.setBody(heartbeatData);

        String serverAddr = serviceDiscovery.discover(ZkConstants.ZK_SERVERS_PATH);

        RpcCommand response = this.remotingClient.invokeSync(serverAddr, request, timeoutMillis);
        assert response != null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS: {
                LOGGER.info(">>>Send heartbeat '{}' to server {} success!",
                        heartbeatData.getGroup(), serverAddr);
                return;
            }
            default:
                break;
        }

        //throw new MQBrokerException(response.getCode(), response.getRemark());
    }

    public void unregister() throws RpcException, InterruptedException {
        String serverAddr = serviceDiscovery.discover(ZkConstants.ZK_SERVERS_PATH);
        unregister(serverAddr, clientId, producerGroup, 6 << 10);
    }

    public void unregister(//
                             final String addr,//
                             final String clientID,//
                             final String producerGroup,//
                             final long timeoutMillis//
    ) throws RpcException, InterruptedException {
        final RegisterRequestHeader requestHeader = new RegisterRequestHeader();
        requestHeader.setClientId(clientID);
        requestHeader.setGroup(producerGroup);
        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.UNREGISTER_CLIENT, requestHeader);
        request.setSerializeType(SerializeType.PROTOSTUFF);

        RpcCommand response = this.remotingClient.invokeSync(addr, request, timeoutMillis);
        assert response != null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS: {
                LOGGER.info(">>>Unregister '{}' from server {} success!",
                        producerGroup, addr);
                return;
            }
            default:
                break;
        }

        //throw new MQBrokerException(response.getCode(), response.getRemark());
    }

    public void sendMessage(Message message, String group, boolean trans) throws RpcException, InterruptedException {
        String serverAddr = serviceDiscovery.discover(ZkConstants.ZK_SERVERS_PATH);
        if(trans){
            transMessage(message, serverAddr, group, 6 << 10);
        }else{
            sendMessage(message, serverAddr, group, 6 << 10);
        }
    }

    public void sendMessage(Message message,
                            final String addr,//
                            final String producerGroup,//
                            final long timeoutMillis
    ) throws InterruptedException, RpcTimeoutException, RpcConnectException, RpcSendRequestException {
        try {
            RocketMQValidators.checkMessage(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }

        final MQMessageHeader requestHeader = new MQMessageHeader();
        requestHeader.setMqType(MQType.ROCKET_MQ);

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.SEND_MSG, requestHeader);
        request.setSerializeType(SerializeType.PROTOSTUFF);

        RocketMQBody mqBody = new RocketMQBody();
        mqBody.setTopic(message.getTopic());
        mqBody.setProducerGroup(producerGroup);
        mqBody.setMessageBody(message.getBody());
        mqBody.setMessageKey(message.getKeys());

        request.setBody(mqBody);

        RpcCommand response = this.remotingClient.invokeSync(addr, request, timeoutMillis);
        assert response != null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS: {
                LOGGER.info(">>>Send message '{}' to server {} success!",
                        message.getKeys(), addr);
                return;
            }
            default:
                break;
        }
    }


    public void transMessage(Message message,
                            final String addr,//
                            final String producerGroup,//
                            final long timeoutMillis
    ) throws InterruptedException, RpcTimeoutException, RpcConnectException, RpcSendRequestException {
        try {
            RocketMQValidators.checkMessage(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }

        final TransactionMessageHeader requestHeader = new TransactionMessageHeader();
        requestHeader.setMqType(MQType.ROCKET_MQ);
        requestHeader.setTransactionState(TransactionState.PREPARE);

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.TRANSACTION_MSG, requestHeader);
        request.setSerializeType(SerializeType.PROTOSTUFF);

        RocketMQBody mqBody = new RocketMQBody();
        mqBody.setTopic(message.getTopic());
        mqBody.setProducerGroup(producerGroup);
        mqBody.setMessageBody(message.getBody());
        mqBody.setMessageKey(message.getKeys());

        request.setBody(mqBody);

        RpcCommand response = this.remotingClient.invokeSync(addr, request, timeoutMillis);
        assert response != null;
        Long transactionId = null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS:
                TransactionMessageHeader responseHeader = null;
                try {
                    responseHeader = (TransactionMessageHeader) response.decodeCustomHeader(TransactionMessageHeader.class);
                } catch (RpcCommandException e) {
                    e.printStackTrace();
                }
                transactionId = responseHeader.getTransactionId();
                break;
            default:
                break;
        }
        if(transactionId != null){
            LOGGER.info(">>>Prepare message '{}' to server {}, transactionId={}",
                    message.getKeys(), addr, transactionId);
            Thread.sleep(3000L);
            if(new Random().nextBoolean()){
                commitMessage(message, transactionId + new Random().nextInt(2),addr,  timeoutMillis);
            }else{
                roolbackMessage(message, transactionId + new Random().nextInt(2),addr,  timeoutMillis);
            }
        }
    }

    public void commitMessage(Message message, Long transactionId,
                                     final String addr,//
                                     final long timeoutMillis
    ) throws InterruptedException, RpcTimeoutException, RpcConnectException, RpcSendRequestException {
        try {
            RocketMQValidators.checkMessage(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }

        final TransactionMessageHeader requestHeader = new TransactionMessageHeader();
        requestHeader.setMqType(MQType.ROCKET_MQ);
        requestHeader.setTransactionState(TransactionState.COMMIT);
        requestHeader.setTransactionId(transactionId);

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.TRANSACTION_MSG, requestHeader);
        request.setSerializeType(SerializeType.PROTOSTUFF);

        RocketMQBody mqBody = new RocketMQBody();
        mqBody.setTopic(message.getTopic());
        mqBody.setProducerGroup(producerGroup);
        mqBody.setMessageBody(message.getBody());
        mqBody.setMessageKey(message.getKeys());

        request.setBody(mqBody);

        RpcCommand response = this.remotingClient.invokeSync(addr, request, timeoutMillis);
        assert response != null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS:
                LOGGER.info(">>>Commit message '{}' to server {} success, transactionId={}",
                        message.getKeys(), addr, transactionId);
                break;
            case ResponseCode.NOT_EXIST:
                LOGGER.error(">>>Commit message '" + message.getKeys()
                        + "' fail, 消息未找到：" + response.getRemark() + ", transactionId=" + transactionId);
                break;
            case ResponseCode.SYSTEM_ERROR:
                LOGGER.error(">>>Commit message '" + message.getKeys()
                        + "' fail, 系统异常："+response.getRemark()+", transactionId="+transactionId);
                break;
            default:
                LOGGER.error(">>>Commit message '" + message.getKeys()
                        + "' fail, 未知原因："+response.getRemark()+", transactionId="+transactionId);
                break;
        }
    }

    public void roolbackMessage(Message message, Long transactionId,
                              final String addr,//
                              final long timeoutMillis
    ) throws InterruptedException, RpcTimeoutException, RpcConnectException, RpcSendRequestException {
        try {
            RocketMQValidators.checkMessage(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }

        final TransactionMessageHeader requestHeader = new TransactionMessageHeader();
        requestHeader.setMqType(MQType.ROCKET_MQ);
        requestHeader.setTransactionState(TransactionState.ROLLBACK);
        requestHeader.setTransactionId(transactionId);

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.TRANSACTION_MSG, requestHeader);
        request.setSerializeType(SerializeType.PROTOSTUFF);

        RpcCommand response = this.remotingClient.invokeSync(addr, request, timeoutMillis);
        assert response != null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS:
                LOGGER.info(">>>Rollback message '{}' to server {} success, transactionId={}",
                        message.getKeys(), addr, transactionId);
                break;
            case ResponseCode.NOT_EXIST:
                LOGGER.error(">>>Rollback message '" + message.getKeys()
                        + "' fail, 消息未找到：" + response.getRemark() + ", transactionId=" + transactionId);
                break;
            case ResponseCode.SYSTEM_ERROR:
                LOGGER.error(">>>Rollback message '" + message.getKeys()
                        + "' fail, 系统异常："+response.getRemark()+", transactionId="+transactionId);
                break;
            default:
                LOGGER.error(">>>Rollback message '" + message.getKeys()
                        + "' fail, 未知原因："+response.getRemark()+", transactionId="+transactionId);
                break;
        }
    }
}
