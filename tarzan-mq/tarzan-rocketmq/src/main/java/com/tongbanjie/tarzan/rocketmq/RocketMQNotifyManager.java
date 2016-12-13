package com.tongbanjie.tarzan.rocketmq;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tarzan.client.ClientConfig;
import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.client.mq.AbstractMQNotifyManager;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.body.RocketMQBody;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.rocketmq.validator.RocketMQValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RocketMQ 通知管理者<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class RocketMQNotifyManager extends AbstractMQNotifyManager<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQNotifyManager.class);

    private final MQType mqType = MQType.ROCKET_MQ;

    private RocketMQParam rocketMQParam;

    public RocketMQNotifyManager(String groupId, String name, String topic, String tag, String namesrvAddr,
                                 TransactionCheckListener transactionCheckListener,
                                 ClientConfig clientConfig) {
        this(new RocketMQParam(groupId, name, topic, tag, namesrvAddr), transactionCheckListener, clientConfig);
    }

    public RocketMQNotifyManager(RocketMQParam rocketMQParam,
                                 TransactionCheckListener transactionCheckListener,
                                 ClientConfig clientConfig) {
        super(clientConfig);
        this.rocketMQParam = rocketMQParam;
        this.setTransactionCheckListener(transactionCheckListener);
    }

    public void init() throws Exception{
        try {
            this.rocketMQParam.validate();
        }catch (Exception e){
            LOGGER.error("Init rocketMQ client failed. Param error: " + rocketMQParam, e);
            throw e;
        }
        super.start(mqType, this.getGroupId());
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

        return mqMessageSender.sendMessage(mqBody);
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

        return mqMessageSender.prepareMessage(mqBody);

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

        return mqMessageSender.commitMessage(transactionId, mqBody);
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
        mqBody.setTags(rocketMQParam.getTag());

        mqBody.setMessageKey(message.getKeys());
        mqBody.setMessageBody(message.getBody());
        return mqBody;
    }

    @Override
    public MessageResult rollbackMessage(Long transactionId) {
        /*************** 消息发送 ***************/
        return mqMessageSender.rollbackMessage(transactionId);
    }

    public String getTopic() {
        return rocketMQParam.getTopic();
    }

    public String getGroupId() {
        return rocketMQParam.getGroupId();
    }

}
