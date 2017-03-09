package com.tongbanjie.tarzan.client.mq;

import com.tongbanjie.tarzan.client.ClientConfig;
import com.tongbanjie.tarzan.client.ClientController;
import com.tongbanjie.tarzan.client.ClientControllerFactory;
import com.tongbanjie.tarzan.client.ClientException;
import com.tongbanjie.tarzan.client.sender.MQMessageSender;
import com.tongbanjie.tarzan.client.sender.MQMessageSenderFactory;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.message.MQType;

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

    private TransactionCheckListener transactionCheckListener;

    protected MQMessageSender mqMessageSender;

    private final ClientConfig clientConfig;

    private final MQType mqType;

    public AbstractMQMessageNotifier(ClientConfig clientConfig, MQType mqType){
        this.clientConfig = clientConfig;
        this.mqType = mqType;
    }

    public void start(String producerGroup) throws ClientException {
        if(isStart.compareAndSet(false, true)){
            this.validate();
            ClientController clientController = ClientControllerFactory.getInstance().getAndCreate(clientConfig);
            clientController.start();
            mqMessageSender = MQMessageSenderFactory.create(mqType, clientController, transactionCheckListener);
            clientController.registerMQMessageSender(producerGroup, mqMessageSender);
        }
    }

    private void validate(){
        if(mqType == null){
            throw new ClientException("Init MQ client failed, mqType can not be null.");
        }
    }
    
    public void setTransactionCheckListener(TransactionCheckListener transactionCheckListener) {
        this.transactionCheckListener = transactionCheckListener;
        if(mqMessageSender != null && mqMessageSender.transactionCheckListener() == null){
            mqMessageSender.setTransactionCheckListener(this.transactionCheckListener);
        }
    }

}
