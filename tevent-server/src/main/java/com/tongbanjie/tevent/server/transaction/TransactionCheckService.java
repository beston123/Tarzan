package com.tongbanjie.tevent.server.transaction;

import com.tongbanjie.tevent.common.ScheduledService;
import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.common.message.SendStatus;
import com.tongbanjie.tevent.common.message.TransactionState;
import com.tongbanjie.tevent.common.util.NamedThreadFactory;
import com.tongbanjie.tevent.common.util.ResultValidate;
import com.tongbanjie.tevent.common.util.Timeout;
import com.tongbanjie.tevent.server.ServerController;
import com.tongbanjie.tevent.common.PagingParam;
import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.store.StoreManager;
import com.tongbanjie.tevent.store.model.ToSendMessage;
import com.tongbanjie.tevent.store.service.StoreService;
import com.tongbanjie.tevent.store.service.ToCheckMessageService;
import com.tongbanjie.tevent.store.service.ToSendMessageService;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 事务状态检查服务 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/14
 */
public class TransactionCheckService implements ScheduledService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionCheckService.class);

    /**
     * 事务检查超时时间
     */
    private static final int MAX_EXEC_MILLIS = 10 * 60 * 1000;

    /**
     * 每次处理数量
     */
    private static final int PAGE_SIZE = 512;

    /**
     * Job锁超时毫秒数
     */
    private static final int JOB_EXPIRE_MILLIS = 30 * 60 * 1000;

    /**
     * JobKey
     */
    private static final String JOB_KEY = TransactionCheckService.class.getCanonicalName();

    private final ServerController serverController;

    private final TransactionCheckExecutor transactionCheckExecutor;

    private ToCheckMessageService toCheckMessageService;

    private ToSendMessageService toSendMessageService;

    private StoreManager storeManager;

    private ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(new NamedThreadFactory("TransactionCheckService"));

    public TransactionCheckService(final ServerController serverController) {
        this.serverController = serverController;
        this.transactionCheckExecutor  = new DefaultTransactionCheckExecutor(serverController);
    }

    @Override
    public void start() {
        this.scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    schedule();
                } catch (Exception e) {
                    LOGGER.error("TransactionCheckService 执行失败", e);
                }
            }
        }, RandomUtils.nextInt(3 * 60, 7 * 60), 10 * 60, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown(){
        this.scheduledExecutorService.shutdown();
    }

    private void init() {
        if(storeManager == null){
            storeManager = this.serverController.getStoreManager();
        }
        Assert.notNull(storeManager);

        if(toCheckMessageService == null){
            toCheckMessageService = this.storeManager.getToCheckMessageService();
        }
        if(toSendMessageService == null){
            toSendMessageService = this.storeManager.getToSendMessageService();
        }
        Assert.notNull(toCheckMessageService);
        Assert.notNull(toSendMessageService);
    }

    @Override
    public void schedule(){
        init();

        if(!storeManager.getRedisComponent().acquireLock(JOB_KEY, JOB_EXPIRE_MILLIS)){
            LOGGER.warn("Job[{}] 并发执行！", JOB_KEY);
            return;
        }
        LOGGER.info("Job[{}] 开始执行", JOB_KEY);
        try {
            Set<Map.Entry<MQType, StoreService>> mqStoreServiceSet = this.storeManager.mqStoreServiceSet();
            for(Map.Entry<MQType, StoreService> entry : mqStoreServiceSet){
                MQType mqType = entry.getKey();
                checkTransactionState(mqType, entry.getValue());
            }
        }finally {
            storeManager.getRedisComponent().releaseLock(JOB_KEY);
        }
        LOGGER.info("Job[{}] 执行结束", JOB_KEY);
    }

    private void checkTransactionState(MQType mqType, StoreService storeService){
        Result<Integer> countRet = storeService.countToCheck(mqType);
        ResultValidate.isTrue(countRet);
        int total = countRet.getData();
        if(total == 0){
            return;
        }
        PagingParam pagingParam = new PagingParam(PAGE_SIZE, total);
        Timeout timeout = new Timeout(MAX_EXEC_MILLIS);
        while (timeout.validate()){
            Result<List<MQMessage>> listResult = storeService.getToCheck(mqType, pagingParam);
            ResultValidate.isTrue(listResult);

            for(MQMessage mqMessage : listResult.getData()){
                if(TransactionState.PREPARE.getCode() == mqMessage.getTransactionState()){
                    this.transactionCheckExecutor.gotoCheck(mqMessage.getProducerGroup(), mqMessage);
                    toCheckMessageService.incrRetryCount(mqMessage.getId());
                }else{
                    removeFromToCheckList(mqType, mqMessage);
                }
            }

            if(!pagingParam.hasNextPage()){
                break;
            }
            pagingParam.nextPage();
        }
    }

    /**
     * 1、COMMIT状态且没有发送成功的消息，添加到［待发送］列表
     * 2、从［待检查］列表移除
     * @param mqType
     * @param mqMessage
     */
    private void removeFromToCheckList(MQType mqType, MQMessage mqMessage){
        try {
            //1、COMMIT状态且没有发送成功的消息，添加到［待发送］列表
            if(TransactionState.COMMIT.getCode() == mqMessage.getTransactionState()
                    && SendStatus.SUCCESS.getCode() != mqMessage.getSendStatus()){
                ToSendMessage toSendMessage = new ToSendMessage();
                toSendMessage.setTid(mqMessage.getId());
                toSendMessage.setMqType(mqType.getCode());
                toSendMessage.setRetryCount((short) 0);
                toSendMessage.setSourceTime(mqMessage.getCreateTime());
                Result<Void> result = this.toSendMessageService.insert(toSendMessage);
                ResultValidate.isTrue(result);
            }
            //2、从［待检查］列表移除
            this.toCheckMessageService.delete(mqMessage.getId());
        } catch (Exception e) {
            LOGGER.error("removeFromToCheckList failed, tid="+mqMessage.getId(), e);
        }
    }

}
