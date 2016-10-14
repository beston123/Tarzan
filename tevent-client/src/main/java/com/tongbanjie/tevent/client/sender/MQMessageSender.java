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
public interface MQMessageSender {


    TransactionCheckListener checkListener();

    void checkTransactionState(final String addr, final String msgKey,
                               final CheckTransactionStateHeader checkTransactionStateHeader);


}
