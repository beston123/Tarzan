package rocketmq.client;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.ClientController;
import com.tongbanjie.tevent.client.cluster.ClusterClient;
import com.tongbanjie.tevent.client.transaction.LocalTransactionState;
import com.tongbanjie.tevent.common.body.HeartbeatData;
import com.tongbanjie.tevent.common.body.RocketMQBody;
import com.tongbanjie.tevent.common.exception.RpcException;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.common.message.TransactionState;
import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.rocketmq.validator.RocketMQValidators;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.exception.*;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.ResponseCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.header.RegisterRequestHeader;
import com.tongbanjie.tevent.rpc.protocol.header.SendMessageHeader;
import com.tongbanjie.tevent.rpc.protocol.header.TransactionMessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Client样例 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 */
public class ExampleClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleClient.class);

    private final String producerGroup;

    private final String clientId = "123456";

    private final RpcClient rpcClient;

    private final ClusterClient clusterClient;

    public ExampleClient(ClientController clientController, String producerGroup){
        this.rpcClient = clientController.getRpcClient();
        this.producerGroup = producerGroup;
        this.clusterClient = clientController.getClusterClient();
    }

    public void sendHeartbeat() throws RpcException, InterruptedException {
        HeartbeatData heartbeatData = new HeartbeatData();
        heartbeatData.setGroup(producerGroup);
        heartbeatData.setClientId(clientId);

        Address addrress = clusterClient.selectOne();
        if(addrress == null){
            return;
        }
        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.HEART_BEAT, null);
        request.setBody(heartbeatData);

        RpcCommand response = this.rpcClient.invokeSync(addrress.getAddress(), request, 3000);
        assert response != null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS: {
                LOGGER.info(">>>Send heartbeat '{}' to server {} success!",
                        heartbeatData.getGroup(), addrress.getAddress());
                return;
            }
            default:
                break;
        }
    }

    public void unregister(  final String addr,//
                             final long timeoutMillis//
    ) throws RpcException, InterruptedException {
        final RegisterRequestHeader requestHeader = new RegisterRequestHeader();
        requestHeader.setClientId(this.clientId);
        requestHeader.setGroup(producerGroup);
        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.UNREGISTER_CLIENT, requestHeader);

        RpcCommand response = this.rpcClient.invokeSync(addr, request, timeoutMillis);
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
    }

    public void sendClusterMessage(final Message message,
                                   final int tryTimes,//
                                   final long timeoutMillis ) throws RpcException, InterruptedException {
        try {
            RocketMQValidators.checkMessage(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }
        RpcCommand request = buildRequest(message);

        RpcCommand response = this.clusterClient.invokeSync(timeoutMillis, tryTimes, request);
        assert response != null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS: {
                LOGGER.info(">>>Send message '{}' to server success!",
                        message.getKeys());
                return;
            }
            default:
                LOGGER.info(">>>Send message '{}' to server failed, error:{}, {}",
                        message.getKeys(), response.getCmdCode(), response.getRemark());
                break;
        }
    }

    private RpcCommand buildRequest(final Message message){
        final SendMessageHeader requestHeader = new SendMessageHeader();
        requestHeader.setMqType(MQType.ROCKET_MQ);

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.SEND_MESSAGE, requestHeader);

        RocketMQBody mqBody = new RocketMQBody();
        mqBody.setTopic(message.getTopic());
        mqBody.setProducerGroup(producerGroup);
        mqBody.setMessageBody(message.getBody());
        mqBody.setMessageKey(message.getKeys());

        request.setBody(mqBody);

        return request;
    }

    public void sendMessage(final Message message,
                            final String addr,//
                            final long timeoutMillis
    ) throws InterruptedException, RpcTimeoutException, RpcConnectException, RpcSendRequestException, RpcTooMuchRequestException {
        try {
            RocketMQValidators.checkMessage(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }
        RpcCommand request = buildRequest(message);

        RpcCommand response = this.rpcClient.invokeSync(addr, request, timeoutMillis);
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


    public void transMessage(final Message message,
                             final String addr,
                             final long timeoutMillis
    ) throws InterruptedException, RpcTimeoutException, RpcConnectException, RpcSendRequestException, RpcTooMuchRequestException {
        try {
            RocketMQValidators.checkMessage(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }

        //1、组装消息
        RpcCommand request = buildTransactionRequest(TransactionState.PREPARE, null, message);

        //2、发送消息
        RpcCommand response = this.rpcClient.invokeSync(addr, request, timeoutMillis);
        assert response != null;
        Long transactionId = null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS:
                TransactionMessageHeader responseHeader = null;
                try {
                    responseHeader = (TransactionMessageHeader) response.decodeCustomHeader(TransactionMessageHeader.class);
                    transactionId = responseHeader.getTransactionId();
                    LOGGER.info(">>>Prepared message '{}' to server {} success, transactionId={} .",
                            message.getKeys(), addr, transactionId);
                } catch (RpcCommandException e) {
                    e.printStackTrace();
                }
                break;
            default:
                LOGGER.error(">>>Prepared message '{}' to server {} failed, errorCode:{}, error:{}",
                        message.getKeys(), addr, response.getCmdCode(), response.getRemark());
                break;
        }

        //3、执行本地事务
        if(transactionId != null){
            try {
                LocalTransactionState state = localTransaction(message.getKeys());
                LOGGER.info("本地事务执行结果: {},  message '{}'", state, message.getKeys());
                switch (state){
                    case COMMIT:
                        commitMessage(message, transactionId, addr, timeoutMillis);
                        break;
                    case ROLLBACK:
                        rollbackMessage(transactionId, addr, timeoutMillis);
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                LOGGER.error("执行本地事务异常, message '"+message.getKeys()+"' ", e);
                        rollbackMessage(transactionId, addr, timeoutMillis);
            } catch (Throwable throwable) {
                //
                LOGGER.error("未知数据结果, message '"+message.getKeys()+"' ", throwable);
            }
        }

    }

    /**
     * 模拟本地事务
     * @return
     */
    private LocalTransactionState localTransaction(String msgKey) throws Throwable {
        //模拟查询事务状态
        Thread.sleep(300L);
        int state = Integer.valueOf(msgKey.split("_")[1]);

        switch (state){
            case 0: //事务处理异常
                throw new IOException("Check local transaction exception, db is down.");
            case 1: //事务需要提交
                return LocalTransactionState.COMMIT;
            case 2: //事务需要回滚
                return LocalTransactionState.ROLLBACK;
            default: // state>=3 应用挂掉了,没有事务结果
                break;
        }
        //state>=3
        throw new Throwable("应用挂掉了,没有事务结果");
    }

    private void commitMessage(final Message message,
                               final Long transactionId,
                               final String addr,
                               final long timeoutMillis
    ) throws InterruptedException, RpcTimeoutException, RpcConnectException, RpcSendRequestException, RpcTooMuchRequestException {
        try {
            RocketMQValidators.checkMessage(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }
        RpcCommand request = buildTransactionRequest(TransactionState.COMMIT, transactionId, message);

        RpcCommand response = this.rpcClient.invokeSync(addr, request, timeoutMillis);
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

    private void rollbackMessage(final Long transactionId,
                                 final String addr,
                                 final long timeoutMillis
    ) throws InterruptedException, RpcTimeoutException, RpcConnectException, RpcSendRequestException, RpcTooMuchRequestException {

        RpcCommand request = buildTransactionRequest(TransactionState.ROLLBACK, transactionId, null);

        RpcCommand response = this.clusterClient.invokeSync(timeoutMillis, request);
        assert response != null;
        switch (response.getCmdCode()) {
            case ResponseCode.SUCCESS:
                LOGGER.info(">>>Rollback message to server {} success, transactionId={}",
                        addr, transactionId);
                break;
            case ResponseCode.NOT_EXIST:
                LOGGER.error(">>>Rollback message fail, 消息未找到：" + response.getRemark() + ", transactionId=" + transactionId);
                break;
            case ResponseCode.SYSTEM_ERROR:
                LOGGER.error(">>>Rollback message fail, 系统异常："+response.getRemark()+", transactionId="+transactionId);
                break;
            default:
                LOGGER.error(">>>Rollback message fail, 未知原因："+response.getRemark()+", transactionId="+transactionId);
                break;
        }
    }


    private RpcCommand buildTransactionRequest(TransactionState transactionState, Long transactionId, Message message){
        final TransactionMessageHeader requestHeader = new TransactionMessageHeader();
        requestHeader.setMqType(MQType.ROCKET_MQ);
        requestHeader.setTransactionState(transactionState);
        requestHeader.setTransactionId(transactionId);

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.TRANSACTION_MESSAGE, requestHeader);

        if(message != null){
            RocketMQBody mqBody = new RocketMQBody();
            mqBody.setTopic(message.getTopic());
            mqBody.setProducerGroup(producerGroup);
            mqBody.setMessageBody(message.getBody());
            mqBody.setMessageKey(message.getKeys());

            request.setBody(mqBody);
        }
        return request;
    }


}
