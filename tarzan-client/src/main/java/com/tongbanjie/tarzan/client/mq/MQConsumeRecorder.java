package com.tongbanjie.tarzan.client.mq;

import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.common.message.MQConsume;

/**
 * 〈MQ消费状态记录 接口〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/12
 */
public interface MQConsumeRecorder<T extends MQConsume> {

    MessageResult consumedFail(final T consume);

    MessageResult consumedSuccess(final T consume);
}
