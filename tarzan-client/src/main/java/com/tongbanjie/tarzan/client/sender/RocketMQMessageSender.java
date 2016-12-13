package com.tongbanjie.tarzan.client.sender;

import com.tongbanjie.tarzan.client.ClientController;
import com.tongbanjie.tarzan.client.transaction.LocalTransactionState;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.body.RocketMQBody;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.common.message.TransactionState;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.CheckTransactionStateHeader;
import com.tongbanjie.tarzan.rpc.protocol.header.TransactionMessageHeader;
import com.tongbanjie.tarzan.rpc.util.RpcHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RocketMQ消息发送者 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public class RocketMQMessageSender extends AbstractMQMessageSender<RocketMQBody> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQMessageSender.class);

    public RocketMQMessageSender(ClientController clientController){
        super(clientController);
    }

    public RocketMQMessageSender(ClientController clientController, TransactionCheckListener transactionCheckListener){
        super(clientController, transactionCheckListener);
    }

    @Override
    protected MQType getMQType() {
        return MQType.ROCKET_MQ;
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
                    getClientController().getRpcClient().invokeOneWay(serverAddr, request, 3000);
                } catch (Exception e) {
                    LOGGER.error("Response checkLocalTransactionState exception. " + thisHeader, e);
                }
            }
        };

        //提交任务
        super.checkExecutor.submit(request);
    }

}
