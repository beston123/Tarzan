package com.tongbanjie.tarzan.common.body;

import com.tongbanjie.tarzan.common.message.RocketMQMessage;

import java.util.Arrays;

/**
 * RocketMQ 协议体 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public class RocketMQBody implements MQBody {

    private static final long serialVersionUID = -8300918145242520246L;

    private String topic;

    private String tags;

    private String producerGroup;

    private String messageKey;

    private byte[] messageBody;

    public static RocketMQBody build(RocketMQMessage mqMessage){
        final RocketMQBody mqBody = new RocketMQBody();
        mqBody.setTopic(mqMessage.getTopic());
        mqBody.setProducerGroup(mqMessage.getProducerGroup());
        mqBody.setMessageBody(mqMessage.getMessageBody());
        mqBody.setMessageKey(mqMessage.getMessageKey());
        mqBody.setTags(mqMessage.getTags());
        return mqBody;
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

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public byte[] getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(byte[] messageBody) {
        this.messageBody = messageBody;
    }

    @Override
    public String toString() {
        return "RocketMQBody{" +
                "topic='" + topic + '\'' +
                ", tags='" + tags + '\'' +
                ", producerGroup='" + producerGroup + '\'' +
                ", messageKey='" + messageKey + '\'' +
                ", messageBody=" + Arrays.toString(messageBody) +
                '}';
    }
}
