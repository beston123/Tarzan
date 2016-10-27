package com.tongbanjie.tevent.rocketmq;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.ClientController;
import com.tongbanjie.tevent.client.ClientControllerFactory;
import com.tongbanjie.tevent.client.sender.MQMessageSender;
import com.tongbanjie.tevent.client.sender.RocketMQMessageSender;
import com.tongbanjie.tevent.client.sender.TransactionCheckListener;
import com.tongbanjie.tevent.common.body.RocketMQBody;
import com.tongbanjie.tevent.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class RocketMQClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQNotifyManager.class);

    private final RocketMQParam rocketMQParam;

    private final TransactionCheckListener transactionCheckListener;

    protected MQMessageSender mqMessageSender;

    public RocketMQClient(RocketMQParam rocketMQParam,
                          TransactionCheckListener transactionCheckListener) {
        this.rocketMQParam = rocketMQParam ;
        this.transactionCheckListener = transactionCheckListener;

        this.init();
    }

    private void init(){
        ClientController clientController = ClientControllerFactory.getInstance().getAndCreate();
        try {
            clientController.start();
            mqMessageSender = new RocketMQMessageSender(clientController, transactionCheckListener);
            clientController.registerMQMessageSender(rocketMQParam.getGroupId(), mqMessageSender);
        } catch (Exception e) {
            LOGGER.error("RocketMQClient start failed.", e);
        }
    }

    public void sendMessage(Message message) {
        RocketMQBody mqBody = new RocketMQBody();
        mqBody.setProducerGroup(rocketMQParam.getGroupId());
        mqBody.setTopic(rocketMQParam.getTopic());
        mqBody.setTags(rocketMQParam.getTag());

        mqBody.setMessageKey(message.getKeys());
        mqBody.setMessageBody(message.getBody());
        try {
            mqMessageSender.sendMessage(mqBody);
        } catch (RpcException e) {
            LOGGER.error("Send message error, " + mqBody, e);
        }
    }



}
