package com.tongbanjie.tevent.client.mq;

import com.tongbanjie.tevent.client.ClientConfig;
import com.tongbanjie.tevent.client.ClientController;
import com.tongbanjie.tevent.client.ClientControllerFactory;
import com.tongbanjie.tevent.client.ClientException;
import com.tongbanjie.tevent.client.sender.MQMessageSender;
import com.tongbanjie.tevent.client.sender.MQMessageSenderFactory;
import com.tongbanjie.tevent.client.transaction.TransactionCheckListener;
import com.tongbanjie.tevent.common.message.MQType;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MQ通知管理者 抽象类 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public abstract class AbstractMQNotifyManager<T> implements MQNotifyManager<T> {

    private final AtomicBoolean isStart = new AtomicBoolean(false);

    private TransactionCheckListener transactionCheckListener;

    protected MQMessageSender mqMessageSender;

    private final ClientConfig clientConfig;

    public AbstractMQNotifyManager(ClientConfig clientConfig){
        this.clientConfig = clientConfig;
    }

    public void start(MQType mqType, String mqGroup) throws ClientException {
        if(isStart.compareAndSet(false, true)){
            if(mqType == null){
                throw new ClientException("Init rocketMQ client failed. MQType can not be null");
            }
            ClientController clientController = ClientControllerFactory.getInstance().getAndCreate(clientConfig);
            clientController.start();
            mqMessageSender = MQMessageSenderFactory.create(mqType, clientController, transactionCheckListener);
            clientController.registerMQMessageSender(mqGroup, mqMessageSender);
        }
    }
    
    public void setTransactionCheckListener(TransactionCheckListener transactionCheckListener) {
        this.transactionCheckListener = transactionCheckListener;
    }

}
