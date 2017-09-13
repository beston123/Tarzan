package rockermq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Random;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/8/7
 */
public class TestConsumer implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        System.out.println("get message： "+msgs.get(0).getKeys() +", tags:"+ msgs.get(0).getTags());
        if(new Random().nextBoolean()){
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }else{
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
