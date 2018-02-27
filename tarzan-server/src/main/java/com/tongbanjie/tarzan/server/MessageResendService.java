package com.tongbanjie.tarzan.server;

import com.tongbanjie.tarzan.server.handler.MQMessageHandler;
import com.tongbanjie.tarzan.common.PagingParam;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.ScheduledService;
import com.tongbanjie.tarzan.common.message.MQMessage;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.common.message.SendStatus;
import com.tongbanjie.tarzan.common.util.NamedThreadFactory;
import com.tongbanjie.tarzan.common.util.ResultValidate;
import com.tongbanjie.tarzan.common.util.Timeout;
import com.tongbanjie.tarzan.server.handler.MQMessageHandlerFactory;
import com.tongbanjie.tarzan.store.StoreManager;
import com.tongbanjie.tarzan.store.query.ToSendMessageQuery;
import com.tongbanjie.tarzan.store.redis.RedisComponent;
import com.tongbanjie.tarzan.store.service.StoreService;
import com.tongbanjie.tarzan.store.service.ToSendMessageService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消息重发服务 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/4
 */
@Component
public class MessageResendService implements ScheduledService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageResendService.class);

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
    private static final String JOB_KEY = MessageResendService.class.getCanonicalName();

    @Autowired
    private ServerController serverController;

    @Autowired
    private StoreManager storeManager;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private ToSendMessageService toSendMessageService;

    @Autowired
    private ServerConfig serverConfig;

    private ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(new NamedThreadFactory("MessageResendService"));

    public MessageResendService() {
    }

    @Override
    public void start() {
        this.scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    schedule();
                } catch (Exception e) {
                    LOGGER.error("MessageResendJob 执行失败", e);
                }
            }
        }, 3 * 60, 10 * 60, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown(){
        this.scheduledExecutorService.shutdown();
    }

    private void init() {

    }

    @Override
    public void schedule(){
        if(!serverController.getServerRegistry().isMaster()){
            return;
        }

        init();

        if(!redisComponent.acquireLock(JOB_KEY, JOB_EXPIRE_MILLIS)){
            LOGGER.warn("Job [MessageResendService] 并发执行");
            return;
        }
        LOGGER.info("Job [MessageResendService] 开始执行");
        try {
            Set<Map.Entry<MQType, StoreService>> mqStoreServiceSet = this.storeManager.mqStoreServiceSet();
            for(Map.Entry<MQType, StoreService> entry : mqStoreServiceSet){
                MQType mqType = entry.getKey();
                queryAndResendMessage(mqType,entry.getValue());
            }
        }finally {
            redisComponent.releaseLock(JOB_KEY);
        }
        LOGGER.info("Job [MessageResendService] 执行结束");
    }

    /**
     * 查询并重发消息
     * @param mqType
     * @param storeService
     */
    private void queryAndResendMessage(MQType mqType, StoreService storeService){
        Timeout timeout = new Timeout(MAX_EXEC_MILLIS);
        Result<Integer> countRet = storeService.countToSend(mqType);
        ResultValidate.isTrue(countRet);
        int total = countRet.getData();
        if(total == 0){
            return;
        }
        PagingParam pagingParam = new PagingParam(PAGE_SIZE, total);
        LOGGER.info("本次需要重发的消息数:{}条, 页数:{}页.", total, pagingParam.getTotalPage());
        ToSendMessageQuery query = new ToSendMessageQuery();
        query.setMqType(mqType.getCode());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1*serverConfig.getMessageMaxCheckDays());
        query.setSourceTimeFrom(calendar.getTime());

        while (timeout.validate()){
            Result<List<MQMessage>> listResult = storeService.getToSend(query, pagingParam);
            ResultValidate.isTrue(listResult);

            for(MQMessage mqMessage : listResult.getData()){
                //发送状态为［FAILED］或者［INITIAL］
                if(SendStatus.FAILED.getCode() == mqMessage.getSendStatus()
                        || SendStatus.INITIAL.getCode() == mqMessage.getSendStatus()){
                    this.send(mqType, mqMessage, storeService);
                //发送成功的消息，从［待发送］列表移除
                }else if(SendStatus.SUCCESS.getCode() == mqMessage.getSendStatus()){
                    this.toSendMessageService.delete(mqMessage.getId());
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
     * 1、获取消息处理者
     * 2、发送消息
     * 3.1、成功：更新消息发送状态，并从待发送列表移除
     * 3.2、失败：重试次数＋1
     * @param mqType
     * @param mqMessage
     * @param storeService
     */
    private void send(MQType mqType, MQMessage mqMessage, StoreService storeService) {
        try {
            //1、获取消息处理者
            MQMessageHandler producer = getHandler(mqType);
            Validate.notNull(producer, "找不到消息发送器, mqType: " + mqType);

            //2、发送消息
            Result<String> sendResult = producer.sendMessage(mqMessage);
            if(sendResult.isSuccess()){
                //3.1、成功：更新消息发送状态，并从待发送列表移除
                onSendSuccess(mqMessage, storeService, sendResult.getData());
            }else{
                //3.2、失败：重试次数＋1
                toSendMessageService.incrRetryCount(mqMessage.getId());
            }
        } catch (Exception e) {
            LOGGER.error("Send message failed, id="+mqMessage.getId(), e);
        }
    }

    /**
     * 获取消息处理者
     * @param mqType
     * @return
     */
    private MQMessageHandler getHandler(MQType mqType){
        return MQMessageHandlerFactory.getInstance().getAndCreate(mqType, this.serverController);
    }

    /**
     * 更新消息发送状态为成功，并从待发送列表移除
     * @param mqMessage
     * @param storeService
     */
    private void onSendSuccess(MQMessage mqMessage, StoreService storeService, String msgId){
        try {
            //1、更新消息发送状态
            Result<Void> updateResult = storeService.updateSendSuccess(mqMessage.getId(), msgId);
            ResultValidate.isTrue(updateResult);
            //2、从［待发送］列表删除
            this.toSendMessageService.delete(mqMessage.getId());
        } catch (Exception e) {
            LOGGER.error("updateSendSuccess failed, tid="+mqMessage.getId(), e);
        }
    }

}
