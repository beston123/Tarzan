package com.tongbanjie.tevent.client.sender;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public interface TransactionCheckListener{

    LocalTransactionState checkTransactionState(final String messageKey);

}
