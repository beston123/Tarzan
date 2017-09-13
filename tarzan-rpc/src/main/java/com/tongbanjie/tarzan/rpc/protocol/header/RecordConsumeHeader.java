package com.tongbanjie.tarzan.rpc.protocol.header;

import com.tongbanjie.tarzan.common.NotNull;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;

/**
 * 〈记录消息消费结果 请求协议头〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/12
 */
public class RecordConsumeHeader implements CustomHeader {

    private static final long serialVersionUID = -910040077730988320L;

    private Long tid;

    @NotNull
    private String messageId;

    @NotNull
    private String messageKey;

    @NotNull
    private String consumerGroup;

    @NotNull
    private String topic;

    private String tags;

    @NotNull
    private String consumer;

    @NotNull
    private Boolean consumeStatus;

    @NotNull
    private MQType mqType;

    @NotNull
    private Short reconsumeTimes;

    @Override
    public void checkFields() throws RpcCommandException {

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

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public Boolean getConsumeStatus() {
        return consumeStatus;
    }

    public void setConsumeStatus(Boolean consumeStatus) {
        this.consumeStatus = consumeStatus;
    }

    public MQType getMqType() {
        return mqType;
    }

    public void setMqType(MQType mqType) {
        this.mqType = mqType;
    }

    public Short getReconsumeTimes() {
        return reconsumeTimes;
    }

    public void setReconsumeTimes(Short reconsumeTimes) {
        this.reconsumeTimes = reconsumeTimes;
    }

    @Override
    public String toString() {
        return "RecordConsumeHeader{" +
                "tid=" + tid +
                ", messageId='" + messageId + '\'' +
                ", messageKey='" + messageKey + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", topic='" + topic + '\'' +
                ", consumer='" + consumer + '\'' +
                ", consumeStatus=" + consumeStatus +
                ", mqType=" + mqType +
                ", reconsumeTimes=" + reconsumeTimes +
                '}';
    }
}
