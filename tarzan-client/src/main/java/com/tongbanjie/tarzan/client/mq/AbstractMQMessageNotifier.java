package com.tongbanjie.tarzan.client.mq;

import com.tongbanjie.tarzan.client.ClientConfig;
import com.tongbanjie.tarzan.client.ClientController;
import com.tongbanjie.tarzan.client.ClientControllerFactory;
import com.tongbanjie.tarzan.client.ClientException;
import com.tongbanjie.tarzan.client.sender.MQMessageSender;
import com.tongbanjie.tarzan.client.sender.MQMessageSenderFactory;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.NotNull;
import com.tongbanjie.tarzan.common.message.MQType;
import org.apache.commons.lang3.Validate;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MQ消息通知者 抽象类 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public abstract class AbstractMQMessageNotifier<T> implements MQMessageNotifier<T> {

    private final AtomicBoolean isStart = new AtomicBoolean(false);

    private MQMessageSender mqMessageSender;

    private ClientController clientController;

    @NotNull
    private MQType mqType;

    /**
     * 事务检查器
     */
    @NotNull
    protected TransactionCheckListener transactionCheckListener;

    /**
     * Client配置信息
     */
    @NotNull
    protected ClientConfig clientConfig;

    public AbstractMQMessageNotifier(MQType mqType){
        this.mqType = mqType;
    }

    protected void start(String producerGroup) throws ClientException {
        if(isStart.compareAndSet(false, true)){
            this.validate();
            clientController = ClientControllerFactory.getInstance().getAndCreate(clientConfig);
            clientController.start();
            mqMessageSender = MQMessageSenderFactory.create(mqType, clientController, transactionCheckListener);
            clientController.registerMQMessageSender(producerGroup, mqMessageSender);
        }
    }

    private void validate(){
        try {
            Validate.notNull(mqType, "mqType can not be null");
            Validate.notNull(transactionCheckListener, "transactionCheckListener can not be null");
            Validate.notNull(clientConfig, "clientConfig can not be null");
        }catch (IllegalArgumentException e){
            throw new ClientException("Init MQ client failed,"+e.getMessage());
        }
    }

    @Override
    public void destroy(){
        clientController.shutdown();
    }

    public MQMessageSender getMqMessageSender() {
        return mqMessageSender;
    }

    public TransactionCheckListener getTransactionCheckListener() {
        return transactionCheckListener;
    }

    public void setTransactionCheckListener(TransactionCheckListener transactionCheckListener) {
        this.transactionCheckListener = transactionCheckListener;
        if(mqMessageSender != null && mqMessageSender.transactionCheckListener() == null){
            mqMessageSender.setTransactionCheckListener(this.transactionCheckListener);
        }
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
