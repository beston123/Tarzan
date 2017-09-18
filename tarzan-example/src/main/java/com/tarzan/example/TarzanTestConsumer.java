package com.tarzan.example;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.tongbanjie.tarzan.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 〈消费者〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/9/18
 */
@Component
public class TarzanTestConsumer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TarzanTestConsumer.class);

    @Value("${rocket.mq.namesrvAddr}")
    private String rocketMQNameSrv;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setConsumerGroup("TARZAN_TEST_C_EXAMPLE");
        mqPushConsumer.setNamesrvAddr(rocketMQNameSrv);
        mqPushConsumer.subscribe(Constants.TARZAN_TEST_TOPIC, TestConstants.MESSAGE_TAG);
        mqPushConsumer.setMessageListener(new TarzanTestListener());
        mqPushConsumer.start();
    }

    private class TarzanTestListener implements MessageListenerConcurrently {

        private Random random = new Random();

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            LOGGER.info("get message： " + msgs.get(0).getKeys() + ", tags:" + msgs.get(0).getTags());
            if(random.nextInt(10000)%2 == 0){
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }else{
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
    }

}
