package com.tongbanjie.tarzan.rocketmq;

import com.tongbanjie.tarzan.client.ClientConfig;
import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.client.mq.AbstractMQConsumeRecorder;
import com.tongbanjie.tarzan.common.message.MQConsume;
import com.tongbanjie.tarzan.common.message.MQType;

/**
 * 〈RocketMQ 消费结果记录者〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/12
 */
public class RocketMQConsumeRecorder extends AbstractMQConsumeRecorder<MQConsume>{

    public RocketMQConsumeRecorder(ClientConfig clientConfig) {
        super(clientConfig, MQType.ROCKET_MQ);
    }

    @Override
    public MessageResult consumedFail(MQConsume consume) {
        return consumed(consume, false);
    }

    @Override
    public MessageResult consumedSuccess(MQConsume consume) {
        return consumed(consume, true);
    }
}
