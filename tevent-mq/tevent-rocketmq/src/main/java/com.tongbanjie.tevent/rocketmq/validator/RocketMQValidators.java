package com.tongbanjie.tevent.rocketmq.validator;

import com.alibaba.rocketmq.client.Validators;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.protocol.ResponseCode;

/**
 * RocketMQ 校验器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RocketMQValidators {

    public static void checkMessage(Message msg) throws MQClientException {
        checkMessage(msg, null);
    }

    public static void checkMessage(Message msg, DefaultMQProducer defaultMQProducer)
            throws MQClientException {
        if (null == msg) {
            throw new MQClientException(ResponseCode.MESSAGE_ILLEGAL, "the message is null");
        }
        // topic
        Validators.checkTopic(msg.getTopic());
        // body
        if (null == msg.getBody()) {
            throw new MQClientException(ResponseCode.MESSAGE_ILLEGAL, "the message body is null");
        }

        if (0 == msg.getBody().length) {
            throw new MQClientException(ResponseCode.MESSAGE_ILLEGAL, "the message body length is zero");
        }

        if (defaultMQProducer != null && msg.getBody().length > defaultMQProducer.getMaxMessageSize()) {
            throw new MQClientException(ResponseCode.MESSAGE_ILLEGAL,
                    "the message body size over max value, MAX: " + defaultMQProducer.getMaxMessageSize());
        }
    }
}
