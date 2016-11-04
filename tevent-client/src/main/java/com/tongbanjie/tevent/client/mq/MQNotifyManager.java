package com.tongbanjie.tevent.client.mq;

import com.tongbanjie.tevent.client.MessageResult;

/**
 * MQ通知管理者 接口<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public interface MQNotifyManager<T> {

    void init() throws Exception;

    MessageResult sendMessage(final T message);

    MessageResult prepareMessage(final T message);

    MessageResult commitMessage(Long transactionId, final T message);

    MessageResult rollbackMessage(Long transactionId);



}
