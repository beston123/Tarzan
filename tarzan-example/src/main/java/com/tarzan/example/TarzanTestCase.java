package com.tarzan.example;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tarzan.client.MessageResult;
import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.rocketmq.RocketMQMessageNotifier;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Random;

/**
 * 〈生产者测试〉<p>
 * 〈使用 Tarzan 处理分布式事务的案例〉<p>
 * 1、执行本地事务前，向 Tarzan 发送［prepare］消息（业务主键作为messageKey）
 * 2、发送［prepare］消息成功，继续执行本地事务；否则执行失败，结束
 * 3、根据第2步中本地事务的执行结果，成功发送［commit］；失败发送［rollback］
 * 4、Tarzan 服务端定时扫描，处于［prepare］状态的消息，向应用系统反查该消息对应的事务状态
 * 应用系统收到反查消息后，按messageKey等关键字查询事务状态，按事务的状态，成功发送［commit］；失败发送［rollback］
 *
 * @author zixiao
 * @date 17/9/18
 */
public class TarzanTestCase extends BaseTestCase{

    private static final Logger LOGGER = LoggerFactory.getLogger(TarzanTestCase.class);

    @Resource(name="testMessageNotifier")
    private RocketMQMessageNotifier testMessageNotifier;

    @Test
    public void sendTarzanMessage() throws InterruptedException {
        transactionMessage();

        Thread.sleep(30 * 1000L);
    }

    /**
     * 事务消息发送步骤
     *
     * 1、执行本地事务前，向 Tarzan 发送［prepare］消息（业务主键作为messageKey）
     * 2、发送［prepare］消息成功，继续执行本地事务；否则执行失败，结束
     * 3、根据第2步中本地事务的执行结果，成功发送［commit］；失败发送［rollback］
     */
    private void transactionMessage() {
        //构造消息，与RocketMQ一致
        String messageKey = "2017"+String.valueOf(System.currentTimeMillis());

        Message message = new Message();
        message.setTopic(Constants.TARZAN_TEST_TOPIC);
        message.setTags(TestConstants.MESSAGE_TAG);
        message.setKeys(messageKey);
        message.setBody(messageKey.getBytes());

        /*************** 1、发送Prepare消息到 Tarzan ***************/
        MessageResult result = testMessageNotifier.prepareMessage(message);
        //发送失败，报错
        if (!result.isSuccess()) {
            LOGGER.error("准备消息 '{}' 失败, {}", message.getKeys(), result.getErrorMsg());
            return;
        }

        //发送成功，执行本地事务
        Long tid = result.getTransactionId();
        LOGGER.info("准备消息 '" + message.getKeys() + "' 成功, 事务Id=" + tid);

        /*************** 2、执行本地事务 ***************/
        boolean flag = doTransaction(messageKey);

        /*************** 3、根据执行本地事务的结果，发送Commit或Rollback消息 ***************/
        if(flag){
            LOGGER.info("本地事务处理成功，提交消息'" + message.getKeys() + "'.");
            testMessageNotifier.commitMessage(tid, message);
        }else {
            LOGGER.info("本地事务处理失败，回滚消息'" + message.getKeys() + "'.");
            testMessageNotifier.rollbackMessage(tid);
        }
    }

    /**
     * 模拟本地事务执行结果
     *
     * @param messageKey
     * @return
     * @throws Exception
     */
    private boolean doTransaction(String messageKey){
        boolean success = false;
        try {
            Thread.sleep(30L);
            //偶数成功，奇数失败
            if(new Random().nextInt(10000)%2 == 0){
                success = true;
            }else{
                success = false;
            }
            return success;
        } catch (Exception e) {
            LOGGER.error("本地事务处理异常'" + messageKey + "'.", e);
        }
        return success;
    }

}
