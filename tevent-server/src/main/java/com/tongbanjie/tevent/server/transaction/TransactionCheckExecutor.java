package com.tongbanjie.tevent.server.transaction;

import com.tongbanjie.tevent.common.message.MQMessage;

/**
 * 向消息生产者回查事务状态 接口<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public interface TransactionCheckExecutor {

    void gotoCheck(String producerGroup, MQMessage mqMessage);
}
