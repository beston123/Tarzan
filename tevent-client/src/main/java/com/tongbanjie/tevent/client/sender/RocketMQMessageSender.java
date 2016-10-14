package com.tongbanjie.tevent.client.sender;

import com.tongbanjie.tevent.client.TransactionCheckListener;
import com.tongbanjie.tevent.rpc.protocol.header.CheckTransactionStateHeader;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public class RocketMQMessageSender implements MQMessageSender {

    private TransactionCheckListener transactionCheckListener;

    @Override
    public TransactionCheckListener checkListener() {
        return this.transactionCheckListener;
    }

    @Override
    public void checkTransactionState(String addr, String msgKey, CheckTransactionStateHeader checkTransactionStateHeader) {
        transactionCheckListener.checkTransactionState(msgKey);
    }
}
