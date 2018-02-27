package com.tongbanjie.tarzan.client.mq;

import com.tongbanjie.tarzan.client.MessageResult;

/**
 * MQ消息通知者 接口<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public interface MQMessageNotifier<T> {

    void init() throws Exception;

    MessageResult sendMessage(final T message);

    MessageResult prepareMessage(final T message);

    MessageResult commitMessage(Long transactionId, final T message);

    MessageResult rollbackMessage(Long transactionId);

    void destroy();

}
