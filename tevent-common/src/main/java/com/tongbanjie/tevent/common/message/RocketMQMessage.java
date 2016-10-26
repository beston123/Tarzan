package com.tongbanjie.tevent.common.message;

import com.tongbanjie.tevent.common.body.RocketMQBody;

import java.util.Arrays;
import java.util.Date;

/**
 * RocketMQ 消息存储Model <p>
 * 对应表 rocketmq_message
 *
 * @author zixiao
 * @date 16/10/9
 */
public class RocketMQMessage implements MQMessage{

    private Long id;

    private String messageKey;

    private String producerGroup;

    private String topic;

    private String tags;

    private byte[] messageBody;

    private Byte transactionState;

    private Integer sendStatus;

    private Integer retryTimes;

    private Date createTime;

    private String producerAddress;

    private Date modifyTime;

    private String messageId;

    public static RocketMQMessage build(RocketMQBody mqBody){
        return build(mqBody, null);
    }

    public static RocketMQMessage build(RocketMQBody mqBody, TransactionState transactionState){
        return build(mqBody, transactionState, null);
    }

    public static RocketMQMessage build(RocketMQBody mqBody, TransactionState transactionState, String producerAddress){
        RocketMQMessage mqMessage = new RocketMQMessage();
        mqMessage.setMessageBody(mqBody.getMessageBody());
        mqMessage.setMessageKey(mqBody.getMessageKey());
        mqMessage.setProducerGroup(mqBody.getProducerGroup());
        mqMessage.setTopic(mqBody.getTopic());
        if(transactionState != null){
            mqMessage.setTransactionState(transactionState.getCode());
        }
        mqMessage.setProducerAddress(producerAddress);
        return mqMessage;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }


    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
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

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getProducerAddress() {
        return producerAddress;
    }

    public void setProducerAddress(String producerAddress) {
        this.producerAddress = producerAddress;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
