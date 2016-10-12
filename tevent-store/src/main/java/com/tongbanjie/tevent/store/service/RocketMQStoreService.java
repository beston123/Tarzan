package com.tongbanjie.tevent.store.service;

import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.store.EventStore;
import com.tongbanjie.tevent.store.Result;
import com.tongbanjie.tevent.store.config.EventStoreConfig;
import com.tongbanjie.tevent.store.util.DistributedIdGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RocketMQ 存储服务<p>
 * 〈功能详细描述〉

 * @author zixiao
 * @date 16/10/9
 */
public class RocketMQStoreService implements MQStoreService<RocketMQMessage> {

    //模拟数据库
    private static Map<Long /*storeId*/, MQMessage> storage = new ConcurrentHashMap<Long, MQMessage>();

    private EventStoreConfig eventStoreConfig;

    public RocketMQStoreService(EventStoreConfig eventStoreConfig){
        this.eventStoreConfig = eventStoreConfig;
    }

    @Override
    public Result<Long> put(RocketMQMessage mqMessage) {
        Result<Long> result;
        Long storeId;
        try {
            storeId = DistributedIdGenerator.generateId();
            mqMessage.setId(storeId);
            storage.put(storeId, mqMessage);
            result = Result.buildSucc(storeId);
        } catch (Exception e) {
            e.printStackTrace();
            result = Result.buildFail("", "", e.getMessage());
        }
        return result;
    }

    @Override
    public Result<RocketMQMessage> get(Long storeId) {
        Result<RocketMQMessage> result;
        try {
            RocketMQMessage mqMessage = (RocketMQMessage)storage.get(storeId);
            result = Result.buildSucc(mqMessage);
        } catch (Exception e) {
            e.printStackTrace();
            result = Result.buildFail("", "", e.getMessage());
        }
        return result;
    }

    @Override
    public Result update(Long storeId, RocketMQMessage mqMessage) {
        Result<RocketMQMessage> result;
        try {
            RocketMQMessage newMqMessage = (RocketMQMessage)storage.put(storeId, mqMessage);
            result = Result.buildSucc(newMqMessage);
        } catch (Exception e) {
            e.printStackTrace();
            result = Result.buildFail("", "", e.getMessage());
        }
        return result;
    }

}
