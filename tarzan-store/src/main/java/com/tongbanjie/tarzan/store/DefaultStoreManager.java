package com.tongbanjie.tarzan.store;

import com.tongbanjie.tarzan.store.service.*;
import com.tongbanjie.tarzan.common.message.MQType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认存储管理器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
@Component
public class DefaultStoreManager implements StoreManager {

    /**
     * MQ存储服务表
     */
    private Map<MQType, StoreService> mqStoreServiceTable = new ConcurrentHashMap<MQType, StoreService>();

    @Resource
    private RocketMQStoreService rocketMQStoreService;

    @Resource
    private MessageToCheckJob messageToCheckJob;

    @Resource
    private MessageToSendJob messageToSendJob;

    @Resource
    private MessageConsumeService messageConsumeService;

    @Override
    public void start() throws Exception {
        mqStoreServiceTable.put(MQType.ROCKET_MQ, rocketMQStoreService);
        
        messageToCheckJob.start();
        messageToSendJob.start();
    }

    @Override
    public void shutdown() {
        messageToCheckJob.shutdown();
        messageToSendJob.shutdown();

        mqStoreServiceTable.clear();
    }

    @Override
    public StoreService getMQStoreService(MQType mqType) {
        return mqStoreServiceTable.get(mqType);
    }

    @Override
    public Set<Map.Entry<MQType, StoreService>> mqStoreServiceSet(){
        return this.mqStoreServiceTable.entrySet();
    }

    @Override
    public MessageConsumeService getMessageConsumeService() {
        return messageConsumeService;
    }
}
