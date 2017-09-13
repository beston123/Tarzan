package com.tongbanjie.tarzan.common.message;

/**
 * 〈MQ消息结果〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/12
 */
public class MQConsume {

    private Long tid;

    private String messageId;

    private String messageKey;

    private String consumerGroup;

    private String topic;

    private String tags;

    private String consumer;

    private Short reconsumeTimes;

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
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

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public Short getReconsumeTimes() {
        return reconsumeTimes;
    }

    public void setReconsumeTimes(Short reconsumeTimes) {
        this.reconsumeTimes = reconsumeTimes;
    }

    @Override
    public String toString() {
        return "MQConsume{" +
                "tid=" + tid +
                ", messageId='" + messageId + '\'' +
                ", messageKey='" + messageKey + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", topic='" + topic + '\'' +
                ", tags='" + tags + '\'' +
                ", consumer='" + consumer + '\'' +
                ", reconsumeTimes=" + reconsumeTimes +
                '}';
    }
}
