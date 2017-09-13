package rockermq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.hook.ConsumeMessageContext;
import com.alibaba.rocketmq.client.hook.ConsumeMessageHook;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.tongbanjie.tarzan.client.ClientConfig;
import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.common.message.MQConsume;
import com.tongbanjie.tarzan.rocketmq.RocketMQConsumeRecorder;
import com.tongbanjie.tarzan.rocketmq.RocketMQMessageNotifier;
import org.junit.Before;
import org.junit.Test;

/**
 * RocketMQ Client Test <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class RocketMQClientTest {

    private ClientConfig clientConfig;

    private RocketMQConsumeRecorder rocketMQConsumeRecorder;

    @Before
    public void before() throws Exception {
        clientConfig = new ClientConfig("zk.tbj.com:2181", "TestApp");
        rocketMQConsumeRecorder = new RocketMQConsumeRecorder(clientConfig);
        rocketMQConsumeRecorder.start();
    }

    @Test
    public void sendMessage() throws Exception {

        RocketMQMessageNotifier mqNotifyManager = create();

        for(int i=0; i< 10; i++) {
            Message message = new Message();
            message.setTopic(Constants.TARZAN_TEST_TOPIC);
            message.setKeys(System.currentTimeMillis()+"_test");
            message.setTags("sendByTarzan");
            message.setBody(("RocketMQTest " + i).getBytes());

            MessageResult result = mqNotifyManager.sendMessage(message);
            if (result.isSuccess()) {
                System.out.println(">>>Send message '" + message.getKeys() + "' to server success.");
            } else {
                System.err.println(">>>Send message '" + message.getKeys() + "' to server failed, " + result.getErrorMsg());
            }
        }

        Thread.sleep(5000 << 10);

    }

    private RocketMQMessageNotifier create() throws Exception {
        TransactionCheckListener checkListener = new TestTransactionCheckListener();
        RocketMQMessageNotifier mqNotifyManager = new RocketMQMessageNotifier(Constants.TARZAN_TEST_P_GROUP, Constants.TARZAN_TEST_TOPIC, checkListener, clientConfig);
        mqNotifyManager.init();
        return mqNotifyManager;
    }

    @Test
    public void consumeMessage() throws MQClientException, InterruptedException {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setConsumerGroup("TEST_C_GROUP");
        mqPushConsumer.setNamesrvAddr("rocketmq.tbj.com:9876");
        mqPushConsumer.subscribe(Constants.TARZAN_TEST_TOPIC, "sendByTarzan");
        mqPushConsumer.setMessageListener(new TestConsumer());
        mqPushConsumer.getDefaultMQPushConsumerImpl().registerConsumeMessageHook(createConsumeHook());

        mqPushConsumer.start();

        Thread.sleep(5000 << 10);
    }

    private ConsumeMessageHook createConsumeHook(){
        return new ConsumeMessageHook() {
            @Override
            public String hookName() {
                return "recordConsumeHook";
            }

            @Override
            public void consumeMessageBefore(ConsumeMessageContext context) {

            }

            @Override
            public void consumeMessageAfter(ConsumeMessageContext context) {
               for(MessageExt messageExt : context.getMsgList()){
                   record(messageExt, context);
               }
            }
        };
    }

    private void record(MessageExt message, ConsumeMessageContext context){
        try {
            if(context.isSuccess()) {
                if( message.getReconsumeTimes() > 0){
                    MQConsume mqConsume = buildMQConsume(message, context);
                    rocketMQConsumeRecorder.consumedSuccess(mqConsume);
                }
            }else{
                MQConsume mqConsume = buildMQConsume(message, context);
                System.out.println("Message has been consumed failed, " + mqConsume);
                rocketMQConsumeRecorder.consumedFail(mqConsume);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private MQConsume buildMQConsume(MessageExt message, ConsumeMessageContext context){
        MQConsume mqConsume = new MQConsume();
        String mqTid = message.getUserProperty(Constants.TARZAN_MQ_TID);
        //tarzan发送事务消息，会带tid
        if(mqTid != null){
            mqConsume.setTid(Long.parseLong(mqTid));
        }
        mqConsume.setMessageId(message.getMsgId());
        mqConsume.setMessageKey(message.getKeys() == null ? "" : message.getKeys());
        mqConsume.setConsumerGroup(context.getConsumerGroup());
        mqConsume.setTopic(message.getTopic());
        mqConsume.setTags(message.getTags());
        mqConsume.setConsumer(clientConfig.getClientId());
        mqConsume.setReconsumeTimes(Short.valueOf(""+message.getReconsumeTimes()));

        return mqConsume;
    }

}
