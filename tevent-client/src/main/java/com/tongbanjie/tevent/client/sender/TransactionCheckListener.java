package com.tongbanjie.tevent.client.sender;

import com.tongbanjie.tevent.common.body.MQBody;

/**
 * 事务检查监听器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public interface TransactionCheckListener<T extends MQBody>{

    LocalTransactionState checkTransactionState(final T mqBody);

}
