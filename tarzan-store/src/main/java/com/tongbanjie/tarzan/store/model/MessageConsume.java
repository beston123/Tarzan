package com.tongbanjie.tarzan.store.model;

import com.tongbanjie.tarzan.common.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * 〈消息消费结果〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/16
 */
public class MessageConsume implements Serializable{

    private static final long serialVersionUID = 8508606761749886932L;

    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * tid
     */
    private Long tid;

    /**
     * 消息Id
     */
    @NotNull
    private String messageId;

    /**
     * 消息key
     */
    @NotNull
    private String messageKey;

    /**
     * 消费者group
     */
    @NotNull
    private String consumerGroup;

    /**
     * Topic
     */
    @NotNull
    private String topic;

    /**
     * 消费者［ ip/appName］
     */
    @NotNull
    private String consumer;

    /**
     * MQ类型
     */
    @NotNull
    private Byte mqType;

    /**
     * 消费状态
     */
    @NotNull
    private Boolean consumeStatus;

    /**
     * 重新消费次数
     */
    @NotNull
    private Short reconsumeTimes;

    /**
     * 创建时间
     */
    @NotNull
    private Date createTime;

    /**
     * 修改时间
     */
    @NotNull
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.messageId = messageId == null ? null : messageId.trim();
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey == null ? null : messageKey.trim();
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup == null ? null : consumerGroup.trim();
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic == null ? null : topic.trim();
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer == null ? null : consumer.trim();
    }

    public Byte getMqType() {
        return mqType;
    }

    public void setMqType(Byte mqType) {
        this.mqType = mqType;
    }

    public Boolean getConsumeStatus() {
        return consumeStatus;
    }

    public void setConsumeStatus(Boolean consumeStatus) {
        this.consumeStatus = consumeStatus;
    }

    public Short getReconsumeTimes() {
        return reconsumeTimes;
    }

    public void setReconsumeTimes(Short reconsumeTimes) {
        this.reconsumeTimes = reconsumeTimes;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "MessageConsume{" +
                "id=" + id +
                ", tid=" + tid +
                ", messageId='" + messageId + '\'' +
                ", messageKey='" + messageKey + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", topic='" + topic + '\'' +
                ", consumer='" + consumer + '\'' +
                ", mqType=" + mqType +
                ", consumeStatus=" + consumeStatus +
                ", reconsumeTimes=" + reconsumeTimes +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
