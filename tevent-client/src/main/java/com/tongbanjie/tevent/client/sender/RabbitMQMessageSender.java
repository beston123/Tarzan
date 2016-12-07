package com.tongbanjie.tevent.client.sender;

import com.tongbanjie.tevent.client.ClientController;
import com.tongbanjie.tevent.client.transaction.TransactionCheckListener;
import com.tongbanjie.tevent.common.body.RabbitMQBody;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.rpc.protocol.header.CheckTransactionStateHeader;

/**
 * RabbitMQ消息发送者 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public class RabbitMQMessageSender extends AbstractMQMessageSender<RabbitMQBody>  {

    public RabbitMQMessageSender(ClientController clientController, TransactionCheckListener transactionCheckListener) {
        super(clientController, transactionCheckListener);
    }

    @Override
    protected MQType getMQType() {
        return null;
    }

    @Override
    public void checkTransactionState(String serverAddr, RabbitMQBody mqBody, CheckTransactionStateHeader requestHeader) {
        //TODO
    }
}
