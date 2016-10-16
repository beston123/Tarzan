package com.tongbanjie.tevent.client.sender;

import com.tongbanjie.tevent.common.body.RocketMQBody;
import com.tongbanjie.tevent.common.message.TransactionState;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.header.CheckTransactionStateHeader;
import com.tongbanjie.tevent.rpc.protocol.header.TransactionMessageHeader;
import com.tongbanjie.tevent.rpc.util.RpcHelper;
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

    private TransactionCheckListener transactionCheckListener;

    protected BlockingQueue<Runnable> checkRequestQueue;

    protected ExecutorService checkExecutor;

    public RocketMQMessageSender(TransactionCheckListener transactionCheckListener){
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
    public TransactionCheckListener transactionCheckListener() {
        return this.transactionCheckListener;
    }

    @Override
    public void checkTransactionState(final String serverAddr, final RocketMQBody rocketMQBody,
                                      final CheckTransactionStateHeader requestHeader,
                                      final RpcClient rpcClient) {
        final Runnable request = new Runnable() {
            private final String group = rocketMQBody.getProducerGroup();
            private final String messageKey = rocketMQBody.getMessageKey();
            private final String topic = rocketMQBody.getTopic();
            String logMsg = " [message group:"+ group+", messageKey:"+ messageKey +", topic:"+ topic +"]";
            
            @Override
            public void run() {
                TransactionCheckListener transactionCheckListener = RocketMQMessageSender.this.transactionCheckListener();
                LocalTransactionState localTransactionState = LocalTransactionState.UNKNOWN;
                if (transactionCheckListener != null) {
                    Throwable exception = null;
                    try {
                        localTransactionState = transactionCheckListener.checkTransactionState(rocketMQBody);
                    } catch (Throwable e) {
                        LOGGER.error("Server call checkTransactionState, but checkLocalTransactionState exception", e);
                        exception = e;
                    }

                    this.processTransactionState(//
                            localTransactionState, //
                            exception);
                } else {
                    LOGGER.warn("checkTransactionState, pick transactionCheckListener by group[{}] failed", group);
                    this.processTransactionState(//
                            localTransactionState, //
                            new RuntimeException("checkTransactionState, pick transactionCheckListener by group[" + group + "] failed"));
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
                        break;
                    case ROLLBACK:
                        thisHeader.setTransactionState(TransactionState.ROLLBACK);
                        LOGGER.warn("Client rollback this transaction, {}", thisHeader);
                        break;
                    case UNKNOWN:
                        thisHeader.setTransactionState(TransactionState.UNKNOWN);
                        LOGGER.warn("Client do not know this transaction state, {}", thisHeader);
                        break;
                    default:
                        thisHeader.setTransactionState(TransactionState.UNKNOWN);
                        LOGGER.warn("Client do not know this transaction state, {}", thisHeader);
                        break;
                }

                String remark = null;
                if (exception != null) {
                    remark = "checkLocalTransactionState Exception: " + RpcHelper.exceptionToString(exception);
                }

                RpcCommand request = RpcCommandBuilder.buildRequest(RequestCode.TRANSACTION_MESSAGE, thisHeader, remark);
                try {
                    rpcClient.invokeOneway(serverAddr, request, 3000);
                } catch (Exception e) {
                    LOGGER.error("Response checkLocalTransactionState exception", e);
                }
            }
        };

        //
        this.checkExecutor.submit(request);
    }
}
