package com.tongbanjie.tevent.common.body;

/**
 * RocketMQ 协议体 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public class RocketMQBody implements CustomBody {

    private String topic;

    private String tags;

    private String producerGroup;

    private String messageKey;

    private byte[] messageBody;

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

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public byte[] getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(byte[] messageBody) {
        this.messageBody = messageBody;
    }


}
