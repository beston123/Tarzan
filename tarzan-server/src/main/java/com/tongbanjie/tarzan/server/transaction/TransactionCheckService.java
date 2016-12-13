package com.tongbanjie.tarzan.server.transaction;

import com.tongbanjie.tarzan.server.ServerController;
import com.tongbanjie.tarzan.common.ScheduledService;
import com.tongbanjie.tarzan.common.message.MQMessage;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.common.message.SendStatus;
import com.tongbanjie.tarzan.common.message.TransactionState;
import com.tongbanjie.tarzan.common.util.NamedThreadFactory;
import com.tongbanjie.tarzan.common.util.ResultValidate;
import com.tongbanjie.tarzan.common.util.Timeout;
import com.tongbanjie.tarzan.common.PagingParam;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.store.StoreManager;
import com.tongbanjie.tarzan.store.model.ToSendMessage;
import com.tongbanjie.tarzan.store.query.ToCheckMessageQuery;
import com.tongbanjie.tarzan.store.service.StoreService;
import com.tongbanjie.tarzan.store.service.ToCheckMessageService;
import com.tongbanjie.tarzan.store.service.ToSendMessageService;
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
     * 执行超时时间
     */
    private static final int MAX_EXEC_MILLIS = 25 * 60 * 1000;

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
        }, RandomUtils.nextInt(8 * 60, 10 * 60), 10 * 60, TimeUnit.SECONDS);
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
            LOGGER.warn("Job [TransactionCheckService] 并发执行");
            return;
        }
        LOGGER.info("Job [TransactionCheckService] 开始执行");
        try {
            Set<Map.Entry<MQType, StoreService>> mqStoreServiceSet = this.storeManager.mqStoreServiceSet();
            for(Map.Entry<MQType, StoreService> entry : mqStoreServiceSet){
                MQType mqType = entry.getKey();
                checkTransactionState(mqType, entry.getValue());
            }
        }finally {
            storeManager.getRedisComponent().releaseLock(JOB_KEY);
        }
        LOGGER.info("Job [TransactionCheckService] 执行结束");
    }

    private void checkTransactionState(MQType mqType, StoreService storeService){
        Timeout timeout = new Timeout(MAX_EXEC_MILLIS);
        Result<Integer> countRet = storeService.countToCheck(mqType);
        ResultValidate.isTrue(countRet);
        int total = countRet.getData();
        if(total == 0){
            return;
        }
        PagingParam pagingParam = new PagingParam(PAGE_SIZE, total);
        LOGGER.info("本次需要检查事务状态的消息数:{}条, 页数:{}页.", total, pagingParam.getTotalPage());
        ToCheckMessageQuery query = new ToCheckMessageQuery();
        query.setMqType(mqType.getCode());
        while (timeout.validate()){
            Result<List<MQMessage>> listResult = storeService.getToCheck(query, pagingParam);
            ResultValidate.isTrue(listResult);
            for(MQMessage mqMessage : listResult.getData()){
                if(TransactionState.PREPARE.getCode() == mqMessage.getTransactionState()){
                    this.transactionCheckExecutor.gotoCheck(mqMessage.getProducerGroup(), mqMessage);
                    toCheckMessageService.incrRetryCount(mqMessage.getId());
                }else{
                    removeFromToCheckList(mqType, mqMessage);
                }
            }
            if(listResult.getData().size() < PAGE_SIZE){
                break;
            }
            query.setTidFromExclude(getLastTid(listResult.getData()));
        }
    }

    private Long getLastTid(List<MQMessage> list){
        return list.get(list.size()-1).getId();
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
