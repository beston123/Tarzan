package com.tongbanjie.tarzan.common.message;

import com.tongbanjie.tarzan.common.body.RocketMQBody;

/**
 * RocketMQ 消息存储Model <p>
 * 对应表 rocketmq_message
 *
 * @author zixiao
 * @date 16/10/9
 */
public class RocketMQMessage extends MQMessage{

    private static final long serialVersionUID = -5887448864636050120L;

    /**
     * 消息tags
     */
    private String tags;

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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }


    @Override
    public String toString() {
        return "RocketMQMessage{" +
                super.toString() + ',' +
                ", tags='" + tags + '\'' +
                '}';
    }

}
