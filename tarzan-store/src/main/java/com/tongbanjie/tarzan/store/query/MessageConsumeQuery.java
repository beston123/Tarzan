package com.tongbanjie.tarzan.store.query;

import com.tongbanjie.tarzan.common.PagingQuery;

/**
 * 〈消息消费结果 查询参数〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/16
 */
public class MessageConsumeQuery extends PagingQuery {

    /**
     * 消息Tid
     */
    private Long tid;

    /**
     * 消息Id
     */
    private String messageId;

    /**
     * 消息key
     */
    private String messageKey;

    /**
     * 消费者group
     */
    private String consumerGroup;

    /**
     * Topic
     */
    private String topic;

    /**
     * MQ类型
     */
    private Integer mqType;

    /**
     * 消费状态
     */
    private Boolean consumeStatus;

    public MessageConsumeQuery() {
        setOrderByClause(ORDER_BY_ID_ASC);
    }

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

    public Integer getMqType() {
        return mqType;
    }

    public void setMqType(Integer mqType) {
        this.mqType = mqType;
    }

    public Boolean getConsumeStatus() {
        return consumeStatus;
    }

    public void setConsumeStatus(Boolean consumeStatus) {
        this.consumeStatus = consumeStatus;
    }

    @Override
    public String toString() {
        return "MessageConsumeQuery{" +
                "tid=" + tid +
                ", messageId='" + messageId + '\'' +
                ", messageKey='" + messageKey + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", topic='" + topic + '\'' +
                ", mqType=" + mqType +
                ", consumeStatus=" + consumeStatus +
                '}';
    }
}
