package com.tongbanjie.tarzan.rocketmq;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tarzan.client.ClientConfig;
import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.client.mq.AbstractMQMessageNotifier;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.NotNull;
import com.tongbanjie.tarzan.common.body.RocketMQBody;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.rocketmq.validator.RocketMQValidators;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RocketMQ 消息通知者<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class RocketMQMessageNotifier extends AbstractMQMessageNotifier<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQMessageNotifier.class);

    /**
     * 生产者Group
     */
    @NotNull
    protected String groupId;

    /**
     * 消息Topic
     */
    @NotNull
    protected String topic;

    public RocketMQMessageNotifier() {
        super(MQType.ROCKET_MQ);
    }

    public RocketMQMessageNotifier(String groupId, String topic,
                                   TransactionCheckListener transactionCheckListener,
                                   ClientConfig clientConfig) {
        this();
        this.groupId = groupId;
        this.topic = topic;
        this.setTransactionCheckListener(transactionCheckListener);
        this.setClientConfig(clientConfig);
    }

    public void init() throws Exception{
        try {
            Validate.notBlank(this.groupId, "The 'groupId' can not be blank");
            Validate.notBlank(this.topic, "The 'topic' can not be blank");
        }catch (IllegalArgumentException e){
            LOGGER.error("Init rocketMQ client failed. Param error: "+ e);
            throw e;
        }
        super.start(getGroupId());
    }

    @Override
    public MessageResult sendMessage(Message message) {
        /*************** 消息校验 ***************/
        try {
            checkMessage(message);
        } catch (MQClientException e) {
            LOGGER.error("消息格式错误", e);
            return MessageResult.buildFail("消息格式错误,"+ e.getErrorMessage());
        }

        /*************** 消息发送 ***************/
        RocketMQBody mqBody = buildMQBody(message);

        return this.getMqMessageSender().sendMessage(mqBody);
    }

    @Override
    public MessageResult prepareMessage(Message message) {
        /*************** 消息校验 ***************/
        try {
            checkMessage(message);
        } catch (MQClientException e) {
            LOGGER.error("消息格式错误", e);
            return MessageResult.buildFail("消息格式错误,"+ e.getErrorMessage());
        }

        /*************** 消息发送 ***************/
        RocketMQBody mqBody = buildMQBody(message);

        return this.getMqMessageSender().prepareMessage(mqBody);

    }

    @Override
    public MessageResult commitMessage(Long transactionId, Message message) {
        /*************** 消息校验 ***************/
        try {
            checkMessage(message);
        } catch (MQClientException e) {
            LOGGER.error("消息格式错误", e);
            return MessageResult.buildFail("消息格式错误,"+ e.getErrorMessage());
        }

        /*************** 消息发送 ***************/
        RocketMQBody mqBody = buildMQBody(message);

        return this.getMqMessageSender().commitMessage(transactionId, mqBody);
    }

    private void checkMessage(Message message) throws MQClientException {
        if(message.getTopic() == null){
            message.setTopic(getTopic());
        }
        RocketMQValidators.checkMessage(message);
    }

    private RocketMQBody buildMQBody(Message message){
        RocketMQBody mqBody = new RocketMQBody();
        mqBody.setProducerGroup(getGroupId());
        mqBody.setTopic(getTopic());
        mqBody.setTags(message.getTags());

        mqBody.setMessageKey(message.getKeys());
        mqBody.setMessageBody(message.getBody());
        return mqBody;
    }

    @Override
    public MessageResult rollbackMessage(Long transactionId) {
        /*************** 消息发送 ***************/
        return this.getMqMessageSender().rollbackMessage(transactionId);
    }

    public String getTopic() {
        return this.topic;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
