package com.tongbanjie.tevent.common.message;

import com.tongbanjie.tevent.common.TransactionState;
import com.tongbanjie.tevent.common.body.RocketMQBody;

import java.util.Date;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public class RocketMQMessage implements MQMessage{

    private Long id;

    private String messageKey;

    private String topic;

    private String tags;

    private String producerGroup;

    private byte[] messageBody;

    private Byte transactionState;

    private Short sendStatus;

    private Short retryTimes;

    private Date createTime;

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

    public Short getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Short sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Short getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Short retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
