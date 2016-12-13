package rocketmq;

import com.tongbanjie.tarzan.client.transaction.LocalTransactionState;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.body.RocketMQBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;


public class TestTransactionCheckListener implements TransactionCheckListener<RocketMQBody> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestTransactionCheckListener.class);

    private Random random = new Random();
    @Override
    public LocalTransactionState checkTransactionState(RocketMQBody mqBody) {
        LOGGER.info("Check local transaction state, msgKey:{}, group:{}", mqBody.getMessageKey(), mqBody.getProducerGroup());

        //模拟查询事务状态
        try {
            Thread.sleep(100+random.nextInt(300));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int state = random.nextInt(3);
        switch (state){
            case 0:
                return LocalTransactionState.UNKNOWN;
            case 1:
                return LocalTransactionState.COMMIT;
            case 2:
                return LocalTransactionState.ROLLBACK;
            default:
                break;
        }
        throw new RuntimeException("Check local transaction exception, db is down.");
    }


}
