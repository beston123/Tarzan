package com.tongbanjie.tevent.store.query;

import java.util.Date;

/**
 * RocketMQ 消息查询参数<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
public class RocketMQMessageQuery {

    private String messageKey;

    private String producerGroup;

    private Byte transactionState;

    private Integer sendStatus;

    private Integer retryTimesFrom;

    private Integer retryTimesTo;

    private Date createTimeTo;

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

    public Integer getRetryTimesFrom() {
        return retryTimesFrom;
    }

    public void setRetryTimesFrom(Integer retryTimesFrom) {
        this.retryTimesFrom = retryTimesFrom;
    }

    public Integer getRetryTimesTo() {
        return retryTimesTo;
    }

    public void setRetryTimesTo(Integer retryTimesTo) {
        this.retryTimesTo = retryTimesTo;
    }

    public Date getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(Date createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    @Override
    public String toString() {
        return "RocketMQMessageQuery{" +
                "messageKey='" + messageKey + '\'' +
                ", producerGroup='" + producerGroup + '\'' +
                ", transactionState=" + transactionState +
                ", sendStatus=" + sendStatus +
                ", retryTimesFrom=" + retryTimesFrom +
                ", retryTimesTo=" + retryTimesTo +
                ", createTimeTo=" + createTimeTo +
                '}';
    }

}
