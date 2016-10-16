package com.tongbanjie.tevent.client.example;

import com.tongbanjie.tevent.client.sender.LocalTransactionState;
import com.tongbanjie.tevent.client.sender.TransactionCheckListener;
import com.tongbanjie.tevent.common.body.RocketMQBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by swy on 2016/10/15.
 */
public class TransactionCheckListenerExample implements TransactionCheckListener<RocketMQBody> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionCheckListenerExample.class);

    private Random random = new Random();
    @Override
    public LocalTransactionState checkTransactionState(RocketMQBody mqBody) {
        LOGGER.info("Check local transaction state, msgKey:{}, group:{}", mqBody.getMessageKey(), mqBody.getProducerGroup());
        int state = random.nextInt(4);
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
