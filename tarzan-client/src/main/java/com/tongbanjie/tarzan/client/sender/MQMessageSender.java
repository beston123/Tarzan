package com.tongbanjie.tarzan.client.sender;

import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.body.MQBody;
import com.tongbanjie.tarzan.rpc.protocol.header.CheckTransactionStateHeader;

/**
 * MQ消息发送者 接口<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public interface MQMessageSender<T extends MQBody> {

    MessageResult sendMessage(final T mqBody);

    MessageResult prepareMessage(final T mqBody);

    MessageResult commitMessage(Long transactionId, final T mqBody);

    MessageResult rollbackMessage(Long transactionId);

    TransactionCheckListener transactionCheckListener();

    void setTransactionCheckListener(TransactionCheckListener transactionCheckListener);

    void checkTransactionState(final String serverAddr, final T mqBody,
                               final CheckTransactionStateHeader requestHeader);


}
