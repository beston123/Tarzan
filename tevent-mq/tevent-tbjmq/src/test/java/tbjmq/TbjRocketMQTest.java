package tbjmq;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.MessageResult;
import com.tongbanjie.tevent.client.mq.MQNotifyManager;
import com.tongbanjie.tevent.common.Constants;
import com.tongbanjie.tevent.tbjmq.TbjRocketMQNotifyManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class TbjRocketMQTest {

    private MQNotifyManager mqNotifyManager;

    @Before
    public void before(){
        ApplicationContext act = new ClassPathXmlApplicationContext("spring-context.xml");
        mqNotifyManager = act.getBean(TbjRocketMQNotifyManager.class);
    }

    @Test
    public void preparedAndCommit(){
        preparedAndCommit("preparedAndCommit");
    }

    @Test
    public void preparedAndRollback(){
        preparedAndRollback("preparedAndRollback");
    }

    private void preparedAndCommit(String messageKey){
        transactionMessage(messageKey, true);
    }

    private void preparedAndRollback(String messageKey){
        transactionMessage(messageKey, false);
    }

    private void transactionMessage(String messageKey, boolean commit) {

        Message message = new Message();
        message.setTopic(Constants.TEVENT_TEST_TOPIC);
        message.setKeys(messageKey);
        message.setBody(("TbjRocketMQTest " + messageKey).getBytes());

        MessageResult result = mqNotifyManager.prepareMessage(message);
        if (result.isSuccess()) {
            System.out.println(">>>Prepare message '" + message.getKeys() + "' success");
            Long tid = result.getTransactionId();
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {}
            if(commit){
                System.out.println(">>>Commit message '" + message.getKeys() + "' success");
                mqNotifyManager.commitMessage(tid, message);
            }else{
                System.out.println(">>>Rollback message '" + message.getKeys() + "' success");
                mqNotifyManager.rollbackMessage(tid);
            }
        } else {
            System.err.println(">>>Prepare message '" + message.getKeys() + "' failed, " + result.getErrorMsg());
        }
    }

    private void sendMessage(){
        for(int i=0; i< 10; i++) {
            Message message = new Message();
            message.setTopic(Constants.TEVENT_TEST_TOPIC);
            message.setKeys("cluster_msg_" + i);
            message.setBody(("TbjRocketMQTest " + i).getBytes());

            MessageResult result = mqNotifyManager.sendMessage(message);
            if (result.isSuccess()) {
                System.out.println(">>>Send message '" + message.getKeys() + "' to server success. msgId=" + result.getMsgId());
            } else {
                System.err.println(">>>Send message '" + message.getKeys() + "' to server failed, " + result.getErrorMsg());
            }
        }

        try {
            Thread.sleep(5000<<10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
