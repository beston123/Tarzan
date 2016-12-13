package com.tongbanjie.tarzan.client.sender;

import com.tongbanjie.tarzan.client.ClientController;
import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.body.MQBody;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.common.message.TransactionState;
import com.tongbanjie.tarzan.common.exception.RpcException;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.MessageResultHeader;
import com.tongbanjie.tarzan.rpc.protocol.header.SendMessageHeader;
import com.tongbanjie.tarzan.rpc.protocol.header.TransactionMessageHeader;

import java.util.concurrent.*;

/**
 * MQ消息发送者 抽象类 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public abstract class AbstractMQMessageSender<T extends MQBody> implements MQMessageSender<T> {

    private static int checkThreadPoolCoreSize = 1;

    private static int checkThreadPoolMaxSize = 1;

    private static int checkRequestHoldMax = 1000;

    private int sendMessageTimeOut = 3000;

    private final ClientController clientController;

    private TransactionCheckListener transactionCheckListener;

    protected final ExecutorService checkExecutor;

    public AbstractMQMessageSender(ClientController clientController){
        this(clientController, null);
    }

    public AbstractMQMessageSender(ClientController clientController, TransactionCheckListener transactionCheckListener){
        this.clientController = clientController;
        this.sendMessageTimeOut = this.clientController.getClientConfig().getSendMessageTimeout();
        this.transactionCheckListener = transactionCheckListener;

        this.checkExecutor = new ThreadPoolExecutor(//
                checkThreadPoolCoreSize, //
                checkThreadPoolMaxSize, //
                1000 * 60, //
                TimeUnit.MILLISECONDS, //
                new LinkedBlockingQueue<Runnable>(checkRequestHoldMax));
    }

    @Override
    public MessageResult sendMessage(T mqBody) {
        final SendMessageHeader requestHeader = new SendMessageHeader();
        requestHeader.setMqType(this.getMQType());

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.SEND_MESSAGE, requestHeader, mqBody);

        MessageResult result;
        try {
            RpcCommand response = this.clientController.getClusterClient().invokeSync(sendMessageTimeOut, request);
            if (response.getCmdCode() == ResponseCode.SUCCESS){
                MessageResultHeader responseHeader = (MessageResultHeader)response.decodeCustomHeader(MessageResultHeader.class);
                result = MessageResult.buildSucc(responseHeader.getMsgId());
            }else{
                result = MessageResult.buildFail("系统异常, code:"+response.getCmdCode()
                        +", error:"+ response.getRemark());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result = MessageResult.buildFail("应用被中断", e);
        } catch (RpcException e) {
            result = MessageResult.buildFail("远程调用失败", e);
        }
        return result;
    }

    @Override
    public MessageResult prepareMessage(T mqBody) {
        final TransactionMessageHeader requestHeader = new TransactionMessageHeader();
        requestHeader.setTransactionState(TransactionState.PREPARE);
        requestHeader.setMqType(this.getMQType());

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.TRANSACTION_MESSAGE, requestHeader, mqBody);

        MessageResult result;
        try {
            RpcCommand response = this.clientController.getClusterClient().invokeSync(sendMessageTimeOut, request);
            if (response.getCmdCode() == ResponseCode.SUCCESS){
                MessageResultHeader responseHeader = (MessageResultHeader)response.decodeCustomHeader(MessageResultHeader.class);
                result = MessageResult.buildSucc(responseHeader.getTransactionId());
            }else{
                result = MessageResult.buildFail("系统异常, code:"+response.getCmdCode()
                        +", error:"+ response.getRemark());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result = MessageResult.buildFail("应用被中断", e);
        } catch (RpcException e) {
            result = MessageResult.buildFail("远程调用失败", e);
        }
        return result;
    }

    @Override
    public MessageResult commitMessage(Long transactionId, T mqBody) {
        final TransactionMessageHeader requestHeader = new TransactionMessageHeader();
        requestHeader.setTransactionState(TransactionState.COMMIT);
        requestHeader.setMqType(this.getMQType());
        requestHeader.setTransactionId(transactionId);

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.TRANSACTION_MESSAGE, requestHeader, mqBody);

        MessageResult result;
        try {
            RpcCommand response = this.clientController.getClusterClient().invokeSync(sendMessageTimeOut, request);
            if (response.getCmdCode() == ResponseCode.SUCCESS){
                result = MessageResult.buildSucc(transactionId);
            }else{
                result = MessageResult.buildFail("系统异常, code:"+response.getCmdCode()
                        +", error:"+ response.getRemark());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result = MessageResult.buildFail("应用被中断", e);
        } catch (RpcException e) {
            result = MessageResult.buildFail("远程调用失败", e);
        }
        return result;
    }

    @Override
    public MessageResult rollbackMessage(Long transactionId) {
        final TransactionMessageHeader requestHeader = new TransactionMessageHeader();
        requestHeader.setTransactionState(TransactionState.ROLLBACK);
        requestHeader.setMqType(this.getMQType());
        requestHeader.setTransactionId(transactionId);

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.TRANSACTION_MESSAGE, requestHeader);

        MessageResult result;
        try {
            RpcCommand response = this.clientController.getClusterClient().invokeSync(sendMessageTimeOut, request);
            if (response.getCmdCode() == ResponseCode.SUCCESS){
                result = MessageResult.buildSucc(transactionId);
            }else{
                result = MessageResult.buildFail("系统异常, code:"+response.getCmdCode()
                        +", error:"+ response.getRemark());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result = MessageResult.buildFail("应用被中断", e);
        } catch (RpcException e) {
            result = MessageResult.buildFail("远程调用失败", e);
        }
        return result;
    }


    @Override
    public TransactionCheckListener transactionCheckListener(){
        return this.transactionCheckListener;
    }

    @Override
    public void setTransactionCheckListener(TransactionCheckListener transactionCheckListener){
        this.transactionCheckListener = transactionCheckListener;
    }

    protected abstract MQType getMQType();

    protected ClientController getClientController(){
        return this.clientController;
    }

}
