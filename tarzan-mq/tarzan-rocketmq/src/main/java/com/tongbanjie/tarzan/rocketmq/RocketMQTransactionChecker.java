package com.tongbanjie.tarzan.rocketmq;

import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.body.RocketMQBody;

/**
 * 〈RocketMQ 事务检查者〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/8/21
 */
public interface RocketMQTransactionChecker extends TransactionCheckListener<RocketMQBody> {

}
