package com.tongbanjie.tarzan.common.message;

import com.tongbanjie.tarzan.common.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * 〈MQMessage共用基类〉<p>
 *  MQMessage共用的表字段
 *
 * @author zixiao
 * @date 16/11/22
 */
public abstract class MQMessage implements Serializable{

    private static final long serialVersionUID = -317517452132863039L;

    /**
     * 主键id
     */
    @NotNull
    protected Long id;

    /**
     * 消息key
     */
    @NotNull
    protected String messageKey;

    /**
     * 消息topic
     */
    @NotNull
    private String topic;

    /**
     * 生产者group
     */
    @NotNull
    protected String producerGroup;

    /**
     * 消息内容［二进制格式］
     */
    protected byte[] messageBody;

    /**
     * 事务状态
     * @see TransactionState
     */
    @NotNull
    protected Byte transactionState;

    /**
     * 发送状态
     * @see SendStatus
     */
    @NotNull
    protected Byte sendStatus;

    /**
     * 是否被汇总
     */
    @NotNull
    protected Boolean hasAggregated;

    /**
     * mq消息Id
     */
    protected String messageId;

    /**
     * 创建时间
     */
    @NotNull
    protected Date createTime;

    /**
     * 修改时间
     */
    @NotNull
    protected Date modifyTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public byte[] getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(byte[] messageBody) {
        this.messageBody = messageBody;
    }

    public Byte getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(Byte transactionState) {
        this.transactionState = transactionState;
    }

    public Byte getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Byte sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Boolean getHasAggregated() {
        return hasAggregated;
    }

    public void setHasAggregated(Boolean hasAggregated) {
        this.hasAggregated = hasAggregated;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", messageKey='" + messageKey + '\'' +
                ", topic='" + topic + '\'' +
                ", producerGroup='" + producerGroup + '\'' +
                ", messageBody=" + Arrays.toString(messageBody) +
                ", transactionState=" + transactionState +
                ", sendStatus=" + sendStatus +
                ", hasAggregated=" + hasAggregated +
                ", messageId='" + messageId + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime;
    }
}
