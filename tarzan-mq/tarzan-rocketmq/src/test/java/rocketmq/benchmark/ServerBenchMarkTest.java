package rocketmq.benchmark;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tarzan.client.ClientConfig;
import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.client.mq.MQMessageNotifier;
import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.rocketmq.RocketMQMessageNotifier;
import com.tongbanjie.tarzan.rocketmq.RocketMQParam;
import org.apache.commons.lang3.RandomUtils;
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
 * 模拟数据
 * 1）0.1%的消息，本地事务执行时异常
 * 2）约50%的消息，提交
 * 3）约50%的消息，回滚
 * @author zixiao
 * @date 16/11/15
 */
public class ServerBenchMarkTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerBenchMarkTest.class);

    /**
     * 测试消息总数
     */
    private final int totalPage = 1;

    private final int totalMessage = totalPage * 10000;

    /**
     * 测试Topic总数
     */
    private final int topicsNum = 10;

    private final int threadsNum = topicsNum;

    private final String registryAddress = "192.168.1.120:2181";

    private ExecutorService executorService;

    private List<RocketMQMessageNotifier> mqNotifyManagerList = new ArrayList<RocketMQMessageNotifier>();

    private final Random random = new Random();

    @Before
    public void before() throws Exception {
        ClientConfig clientConfig = new ClientConfig(registryAddress, "BenchMark");
        executorService = Executors.newFixedThreadPool(threadsNum);
        for(int i=0; i< topicsNum; i++){
            mqNotifyManagerList.add(createMqNotifyManager(i, clientConfig));
        }
    }

    @Test
    public void transactionListen() throws InterruptedException {
        for(RocketMQMessageNotifier mqNotifyManager : mqNotifyManagerList){
            mqNotifyManager.setTransactionCheckListener(new TestTransactionCheckListener());
        }
        Thread.sleep(50 * 60 * 1000);
    }

    @Test
    public void start() throws Exception {
        long start = System.currentTimeMillis();
        Random random = new Random();
        int perCount = totalMessage / totalPage;

        for(int t=0; t<totalPage; t++){
            CountDownLatch latch = new CountDownLatch(perCount);
            for(int i=0; i<perCount; i++) {
                MQMessageNotifier mqNotifyManager = mqNotifyManagerList.get(random.nextInt(topicsNum));
                Message message = buildMessage(i);
                executorService.execute(new Task(message, latch, mqNotifyManager));
            }
            latch.await();
        }

        long costSec = (System.currentTimeMillis() - start) / 1000;
        LOGGER.info("Topics: {}\tPrepare: {}\tCommit: {}\tRollback: {}", topicsNum, totalMessage, totalMessage/2, totalMessage/2);
        LOGGER.info("Total costs {}s\tTPS: {}", costSec, totalMessage*2/costSec);
    }

    private class Task implements Runnable{

        private Message message;

        private CountDownLatch countDownLatch;

        private MQMessageNotifier mqNotifyManager;

        public Task(Message message, CountDownLatch countDownLatch, MQMessageNotifier mqNotifyManager){
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
                    //1、序号为1000的倍数的消息，设置为异常
                    if(Integer.parseInt(message.getTags()) % 1000 == 0){
                       throw new RuntimeException("事务状态未知，message '" + message.getKeys() + "'.");
                    }
                    //2、随机提交或回滚
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
        String key = RandomUtils.nextLong(1000000, 5000000) + "_" + index;
        message.setKeys(key);
        message.setBody((this.getClass().getCanonicalName() + ", key:" + key).getBytes());
        message.setTags(String.valueOf(index));
        return message;
    }


    private RocketMQMessageNotifier createMqNotifyManager(int index, ClientConfig clientConfig) throws Exception {
        RocketMQParam rocketMQParam = new RocketMQParam();
        rocketMQParam.setGroupId(Constants.TARZAN_TEST_P_GROUP + index)
                .setTopic(Constants.TARZAN_TEST_TOPIC + index);
        RocketMQMessageNotifier mqNotifyManager = new RocketMQMessageNotifier(rocketMQParam, null, clientConfig);
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