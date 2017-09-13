package com.tongbanjie.tarzan.store.query;

import com.tongbanjie.tarzan.common.PagingQuery;

import java.util.Date;

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
     * Tags
     */
    private String tags;

    /**
     * MQ类型
     */
    private Integer mqType;

    /**
     * 消费状态
     */
    private Boolean consumeStatus;

    /**
     * 创建时间From
     */
    private Date createTimeFrom;

    /**
     * 创建时间To
     */
    private Date createTimeTo;

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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public Date getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(Date createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public Date getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(Date createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    @Override
    public String toString() {
        return "MessageConsumeQuery{" +
                "tid=" + tid +
                ", messageId='" + messageId + '\'' +
                ", messageKey='" + messageKey + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", topic='" + topic + '\'' +
                ", tags='" + tags + '\'' +
                ", mqType=" + mqType +
                ", consumeStatus=" + consumeStatus +
                ", createTimeFrom=" + createTimeFrom +
                ", createTimeTo=" + createTimeTo +
                '}';
    }
}
