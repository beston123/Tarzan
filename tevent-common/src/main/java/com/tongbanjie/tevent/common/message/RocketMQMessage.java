package com.tongbanjie.tevent.common.message;

import com.sun.istack.internal.NotNull;
import com.tongbanjie.tevent.common.body.RocketMQBody;

import java.util.Arrays;

/**
 * RocketMQ 消息存储Model <p>
 * 对应表 rocketmq_message
 *
 * @author zixiao
 * @date 16/10/9
 */
public class RocketMQMessage extends MQMessage{

    private static final long serialVersionUID = -5887448864636050120L;

    /**
     * 消息topic
     */
    @NotNull
    private String topic;

    /**
     * 消息tags
     */
    private String tags;

    public static RocketMQMessage build(RocketMQBody mqBody){
        return build(mqBody, null);
    }

    public static RocketMQMessage build(RocketMQBody mqBody, TransactionState transactionState){
        RocketMQMessage mqMessage = new RocketMQMessage();
        mqMessage.setMessageBody(mqBody.getMessageBody());
        mqMessage.setMessageKey(mqBody.getMessageKey());
        mqMessage.setProducerGroup(mqBody.getProducerGroup());
        mqMessage.setTopic(mqBody.getTopic());
        if(transactionState != null){
            mqMessage.setTransactionState(transactionState.getCode());
        }
        return mqMessage;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }


    @Override
    public String toString() {
        return "RocketMQMessage{" +
                super.toString() + ',' +
                ", topic='" + topic + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

}
