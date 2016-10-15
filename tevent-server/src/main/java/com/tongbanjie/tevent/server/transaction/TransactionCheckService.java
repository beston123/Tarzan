package com.tongbanjie.tevent.server.transaction;

import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.common.util.NamedThreadFactory;
import com.tongbanjie.tevent.server.ServerController;
import com.tongbanjie.tevent.store.Result;
import com.tongbanjie.tevent.store.service.RocketMQStoreService;
import com.tongbanjie.tevent.store.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/14
 */
public class TransactionCheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionCheckService.class);

    private final ServerController serverController;

    private final TransactionCheckExecutor transactionCheckExecutor;

    private ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(new NamedThreadFactory("TransactionStateCheckScheduledThread"));


    public TransactionCheckService(final ServerController serverController) {
        this.serverController = serverController;
        this.transactionCheckExecutor  = new DefaultTransactionCheckExecutor(serverController);
    }


    public void start() {
        this.scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionCheckService.this.checkTransactionState();
                }
                catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }, 1000 * 30, 1000 * 600, TimeUnit.MILLISECONDS);
    }

    public void checkTransactionState(){
        StoreService storeService = this.serverController.getStoreManager().getStoreService();
        if(storeService instanceof RocketMQStoreService){
            Result<List<RocketMQMessage>> listResult = storeService.selectTrans();
            List<RocketMQMessage> list = listResult.getData();
            for(RocketMQMessage mqMessage : list){
                this.transactionCheckExecutor.gotoCheck(mqMessage.getProducerGroup(), mqMessage);
            }
        }
    }

    public void shutdown(){
        this.scheduledExecutorService.shutdown();
    }

}
