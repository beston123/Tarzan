package com.tongbanjie.tarzan.tbjmq;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.mq.message.BytesMessage;
import com.tongbanjie.mq.message.DapperMessage;
import com.tongbanjie.mq.message.StringMessage;
import com.tongbanjie.mq.util.ObjectUtil;
import com.tongbanjie.tarzan.client.ClientConfig;
import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.rocketmq.RocketMQNotifyManager;
import com.tongbanjie.tarzan.rocketmq.RocketMQParam;

import java.util.HashMap;
import java.util.Map;

/**
 * TBJ RocketMQ 通知管理者 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public class TbjRocketMQNotifyManager extends RocketMQNotifyManager {

    public TbjRocketMQNotifyManager(String groupId, String name, String topic, String tag, String namesrvAddr,
                                    TransactionCheckListener transactionCheckListener,
                                    ClientConfig clientConfig) {
        super(groupId, name, topic, tag, namesrvAddr, transactionCheckListener, clientConfig);
    }

    public TbjRocketMQNotifyManager(RocketMQParam rocketMQParam,
                                    TransactionCheckListener transactionCheckListener,
                                    ClientConfig clientConfig) {
        super(rocketMQParam, transactionCheckListener, clientConfig);
    }

    public MessageResult sendMessage(com.tongbanjie.mq.message.Message message) {
        try{
            Message rocketMsg = toRocketMQMessage(message);
            return super.sendMessage(rocketMsg);
        }catch (Exception e){
            return MessageResult.buildFail("消息转换异常", e);
        }
    }

    public MessageResult prepareMessage(com.tongbanjie.mq.message.Message message){
        try{
            Message rocketMsg = toRocketMQMessage(message);
            return super.prepareMessage(rocketMsg);
        }catch (Exception e){
            return MessageResult.buildFail("消息转换异常", e);
        }
    }

    public MessageResult commitMessage(Long transactionId, com.tongbanjie.mq.message.Message message){
        try{
            Message rocketMsg = toRocketMQMessage(message);
            return super.commitMessage(transactionId, rocketMsg);
        }catch (Exception e){
            return MessageResult.buildFail("消息转换异常", e);
        }
    }

    private Message toRocketMQMessage(com.tongbanjie.mq.message.Message message) throws Exception {
        byte[] body = null;
        if (message instanceof BytesMessage) {
            body = ((BytesMessage) message).getBody();
        }else if(message instanceof StringMessage){
            body = ((StringMessage) message).getBody().getBytes();
        }else if(message instanceof DapperMessage){
            Map map = new HashMap();
            map.put("body", ((DapperMessage) message).getBody());
            map.put("attachment", ((DapperMessage) message).getAttachment());
            body = ObjectUtil.changeObject2byte(map);
        }else {
            throw new IllegalArgumentException("message:" + message.getClass() + " not support");
        }
        return new Message(
                this.getTopic(),
                message.getTags(),
                message.getKeys(),
                body);
    }

}
