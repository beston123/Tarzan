package rocketmq.benchmark;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.ClientConfig;
import com.tongbanjie.tevent.client.MessageResult;
import com.tongbanjie.tevent.client.mq.MQNotifyManager;
import com.tongbanjie.tevent.client.transaction.TransactionCheckListener;
import com.tongbanjie.tevent.common.Constants;
import com.tongbanjie.tevent.rocketmq.RocketMQNotifyManager;
import com.tongbanjie.tevent.rocketmq.RocketMQParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocketmq.TestTransactionCheckListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务端 BenchMark 测试<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/15
 */
public class ServerBenchMarkTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerBenchMarkTest.class);

    private final int totalMessage = 20000;

    private final int topicsNum = 50;

    private final int threadsNum = topicsNum;

    private final String mqNameSrv = "192.168.1.42:9876";

    private final String registryAddress = "192.168.1.120:2181";

    private ExecutorService executorService;

    private List<RocketMQNotifyManager> mqNotifyManagerList = new ArrayList<RocketMQNotifyManager>();

    @Before
    public void before() throws Exception {
        ClientConfig clientConfig = new ClientConfig(registryAddress);
        executorService = Executors.newFixedThreadPool(threadsNum);
        for(int i=0; i< topicsNum; i++){
            mqNotifyManagerList.add(createMqNotifyManager(i, clientConfig));
        }
    }

    @Test
    public void start() throws Exception {
        CountDownLatch latch = new CountDownLatch(totalMessage);
        long start = System.currentTimeMillis();
        Random random = new Random();

        for(int i=0; i< totalMessage; i++) {
            MQNotifyManager mqNotifyManager = mqNotifyManagerList.get(random.nextInt(topicsNum));
            Message message = buildMessage(i);
            executorService.execute(new Task(message, latch, mqNotifyManager));
        }
        latch.await();

        long costSec = (System.currentTimeMillis() - start) / 1000;
        LOGGER.info("Topics: {}\tPrepare: {}\tCommit: {}\tRollback: {}",
                topicsNum, totalMessage, totalMessage/2, totalMessage/2);
        LOGGER.info("Total costs {}s\tTPS: {}", costSec, totalMessage*2/costSec);
    }

    private class Task implements Runnable{

        private final Random random = new Random();

        private Message message;

        private CountDownLatch countDownLatch;

        private MQNotifyManager mqNotifyManager;

        public Task(Message message, CountDownLatch countDownLatch, MQNotifyManager mqNotifyManager){
            this.message = message;
            this.countDownLatch = countDownLatch;
            this.mqNotifyManager = mqNotifyManager;
        }

        @Override
        public void run() {
            try {
                long start = System.currentTimeMillis();
                MessageResult result = mqNotifyManager.prepareMessage(message);
                LOGGER.info(">>>Prepare message '{}' cost {}ms", message.getKeys(), System.currentTimeMillis() - start);
                start = System.currentTimeMillis();
                if (result.isSuccess()) {
                    long tid = result.getTransactionId();
                    if(random.nextBoolean()){
                        mqNotifyManager.commitMessage(tid, message);
                        LOGGER.info(">>>Commit message '{}' cost {}ms", message.getKeys(), System.currentTimeMillis() - start);
                    }else{
                        mqNotifyManager.rollbackMessage(tid);
                        LOGGER.info(">>>Rollback message '{}' cost {}ms", message.getKeys(), System.currentTimeMillis() - start);
                    }
                } else {
                    LOGGER.error(">>>Prepare message '{}' failed, error: {}", message.getKeys(), result.getErrorMsg());
                }
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    private Message buildMessage(int index){
        Message message = new Message();
        message.setKeys("MSG_" + index);
        message.setBody((this.getClass().getCanonicalName() + ", RocketMQTest " + index).getBytes());
        return message;
    }


    private RocketMQNotifyManager createMqNotifyManager(int index, ClientConfig clientConfig) throws Exception {
        RocketMQParam rocketMQParam = new RocketMQParam();
        rocketMQParam.setGroupId(Constants.TEVENT_TEST_P_GROUP + index)
                .setName("RocketMQTest" + index)
                .setTopic(Constants.TEVENT_TEST_TOPIC + +index)
                .setNamesrvAddr(mqNameSrv);
        TransactionCheckListener checkListener = new TestTransactionCheckListener();
        RocketMQNotifyManager mqNotifyManager = new RocketMQNotifyManager(rocketMQParam, checkListener, clientConfig);
        mqNotifyManager.init();
        return mqNotifyManager;
    }

    @After
    public void after(){
        if(executorService != null){
            executorService.shutdown();
        }
    }
}
