package test.tevent.rocketmq;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.sender.TransactionCheckListener;
import com.tongbanjie.tevent.common.Constants;
import com.tongbanjie.tevent.rocketmq.RocketMQNotifyManager;
import com.tongbanjie.tevent.rocketmq.RocketMQParam;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class RocketMQTest {

    public static void main(String[] args) {
        RocketMQParam rocketMQParam = new RocketMQParam();
        rocketMQParam.setGroupId(Constants.TEVENT_TEST_P_GROUP)
                .setName("RocketMQTest")
                .setTopic(Constants.TEVENT_TEST_TOPIC)
                .setNamesrvAddr("192.168.1.42:9876");
        TransactionCheckListener checkListener = new TestTransactionCheckListener();
        RocketMQNotifyManager mqNotifyManager = new RocketMQNotifyManager(rocketMQParam, checkListener);
        mqNotifyManager.init();

        for(int i=0; i< 10; i++){
            Message message = new Message();
            message.setTopic(Constants.TEVENT_TEST_TOPIC);
            message.setKeys("cluster_msg_" + i);
            message.setBody(("Hello TEvent " + i).getBytes());
            try {
                mqNotifyManager.sendMessage(message);
                System.out.println(">>>Send message '" + message.getKeys() + "' to server success.");
                //Thread.sleep(10<<10);
            } catch (Exception e) {
                System.err.println(">>>Send message '" + message.getKeys() + "' to server failed, " + e);
            }
        }

        try {
            Thread.sleep(5000<<10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
