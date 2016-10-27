package com.tongbanjie.tevent.client.sender;

import com.tongbanjie.tevent.client.ClientController;
import com.tongbanjie.tevent.common.body.RocketMQBody;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.common.message.TransactionState;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.exception.*;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.header.CheckTransactionStateHeader;
import com.tongbanjie.tevent.rpc.protocol.header.SendMessageHeader;
import com.tongbanjie.tevent.rpc.protocol.header.TransactionMessageHeader;
import com.tongbanjie.tevent.rpc.util.RpcHelper;
import org.apache.zookeeper.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public class RocketMQMessageSender implements MQMessageSender<RocketMQBody> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQMessageSender.class);

    private final TransactionCheckListener transactionCheckListener;

    protected final BlockingQueue<Runnable> checkRequestQueue;

    protected final ExecutorService checkExecutor;

    private final ClientController clientController;

    public RocketMQMessageSender(ClientController clientController, TransactionCheckListener transactionCheckListener){
        this.clientController = clientController;
        this.transactionCheckListener = transactionCheckListener;

        this.checkRequestQueue = new LinkedBlockingQueue<Runnable>(checkRequestHoldMax);
        this.checkExecutor = new ThreadPoolExecutor(//
                checkThreadPoolCoreSize, //
                checkThreadPoolMaxSize, //
                1000 * 60, //
                TimeUnit.MILLISECONDS, //
                this.checkRequestQueue);
    }

    @Override
    public void sendMessage(RocketMQBody mqBody) throws RpcException {
        final SendMessageHeader requestHeader = new SendMessageHeader();
        requestHeader.setMqType(MQType.ROCKET_MQ);

        RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.SEND_MESSAGE, requestHeader, mqBody);

        try {
            this.clientController.getClusterClient().invokeOneway(sendMessageTimeOut, request);
        } catch (InterruptedException e) {
            LOGGER.error("SystemError", e);
        } catch (RpcException e) {
            throw e;
        }
    }

    @Override
    public void prepareMessage(RocketMQBody mqBody) throws RpcException {

    }

    @Override
    public void commitMessage(Long transactionId, RocketMQBody mqBody) throws RpcException {

    }

    @Override
    public void rollbackMessage(Long transactionId) throws RpcException {

    }

    @Override
    public TransactionCheckListener transactionCheckListener() {
        return this.transactionCheckListener;
    }

    @Override
    public void checkTransactionState(final String serverAddr, final RocketMQBody rocketMQBody,
                                      final CheckTransactionStateHeader requestHeader) {
        final Runnable request = new Runnable() {
            private final String group = rocketMQBody.getProducerGroup();
            private final String messageKey = rocketMQBody.getMessageKey();
            private final String topic = rocketMQBody.getTopic();

            @Override
            public void run() {
                LOGGER.debug("Start to check localTransactionState, messageKey:{}, topic:{}, transactionId:{}",
                        messageKey, topic, requestHeader.getTransactionId());

                TransactionCheckListener transactionCheckListener = RocketMQMessageSender.this.transactionCheckListener();
                LocalTransactionState localTransactionState = LocalTransactionState.UNKNOWN;
                if (transactionCheckListener != null) {
                    Throwable exception = null;
                    try {
                        localTransactionState = transactionCheckListener.checkTransactionState(rocketMQBody);
                    } catch (Throwable e) {
                        LOGGER.error("Server call checkTransactionState, but checkLocalTransactionState exception.", e);
                        exception = e;
                    }

                    this.processTransactionState(//
                            localTransactionState, //
                            exception);
                } else {
                    LOGGER.error("CheckTransactionState failed: MQMessageSender of group '{}', has not been set a transactionCheckListener.", group);
                    this.processTransactionState(//
                            localTransactionState, //
                            new RuntimeException("CheckTransactionState failed: MQMessageSender of group '" + group
                                    + "', has not been set a transactionCheckListener."));
                }
            }

            private void processTransactionState(//
                                                 final LocalTransactionState localTransactionState, //
                                                 final Throwable exception) {
                final TransactionMessageHeader thisHeader = new TransactionMessageHeader();
                thisHeader.setMqType(requestHeader.getMqType());
                thisHeader.setTransactionId(requestHeader.getTransactionId());
                
                switch (localTransactionState) {
                    case COMMIT:
                        thisHeader.setTransactionState(TransactionState.COMMIT);
                        LOGGER.info("Client commit this transaction, {}, messageKey:{}, topic:{}",
                                thisHeader, messageKey, topic);
                        break;
                    case ROLLBACK:
                        thisHeader.setTransactionState(TransactionState.ROLLBACK);
                        LOGGER.info("Client rollback this transaction, {}, messageKey:{}, topic:{}",
                                thisHeader, messageKey, topic);
                        break;
                    case UNKNOWN:
                        thisHeader.setTransactionState(TransactionState.UNKNOWN);
                        LOGGER.warn("Client do not know this transaction state, {}, messageKey:{}, topic:{}",
                                thisHeader, messageKey, topic);
                        break;
                    default:
                        thisHeader.setTransactionState(TransactionState.UNKNOWN);
                        LOGGER.warn("Client do not know this transaction state, {}, messageKey:{}, topic:{}",
                                thisHeader, messageKey, topic);
                        break;
                }

                String remark = null;
                if (exception != null) {
                    remark = "checkLocalTransactionState Exception: " + RpcHelper.exceptionToString(exception);
                }

                RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.TRANSACTION_MESSAGE, thisHeader, remark);
                try {
                    clientController.getRpcClient().invokeOneway(serverAddr, request, 3000);
                } catch (Exception e) {
                    LOGGER.error("Response checkLocalTransactionState exception. " + thisHeader, e);
                }
            }
        };

        //
        this.checkExecutor.submit(request);
    }

}
