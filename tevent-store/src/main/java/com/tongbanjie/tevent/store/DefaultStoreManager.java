package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.common.redis.RedisComponent;
import com.tongbanjie.tevent.store.service.*;
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
    private ToCheckMessageService toCheckMessageService;

    @Resource
    private ToSendMessageService toSendMessageService;

    @Resource
    private MessageToCheckJob messageToCheckJob;

    @Resource
    private MessageToSendJob messageToSendJob;

    @Resource
    private RedisComponent redisComponent;

    @Override
    public void start() throws Exception {
        mqStoreServiceTable.put(MQType.ROCKET_MQ, rocketMQStoreService);
        
        messageToCheckJob.start();
        messageToSendJob.start();
    }

    @Override
    public void shutdown() {
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
    public ToCheckMessageService getToCheckMessageService() {
        return this.toCheckMessageService;
    }

    @Override
    public ToSendMessageService getToSendMessageService() {
        return this.toSendMessageService;
    }

    @Override
    public RedisComponent getRedisComponent() {
        return this.redisComponent;
    }


}
