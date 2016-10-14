package com.tongbanjie.tevent.store.service;

import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.common.message.TransactionState;
import com.tongbanjie.tevent.store.Result;
import com.tongbanjie.tevent.store.config.StoreConfig;
import com.tongbanjie.tevent.store.util.DistributedIdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RocketMQ 存储服务<p>
 * 〈功能详细描述〉

 * @author zixiao
 * @date 16/10/9
 */
public class RocketMQStoreService implements StoreService<RocketMQMessage> {

    //模拟数据库
    private static Map<Long /*storeId*/, MQMessage> storage = new ConcurrentHashMap<Long, MQMessage>();

    private StoreConfig storeConfig;

    public RocketMQStoreService(StoreConfig storeConfig){
        this.storeConfig = storeConfig;
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
    public Result<RocketMQMessage> update(Long storeId, RocketMQMessage mqMessage) {
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

    @Override
    public Result<List<RocketMQMessage>> selectTrans() {
        Result<List<RocketMQMessage>> result = new Result<List<RocketMQMessage>>();
        try {
            List<RocketMQMessage> mqMessages = new ArrayList<RocketMQMessage>();
            Set<Map.Entry<Long, MQMessage>> set = storage.entrySet();
            for(Map.Entry<Long, MQMessage> entry : set){
                RocketMQMessage message = (RocketMQMessage) entry.getValue();
                if(message.getTransactionState() == TransactionState.PREPARE.getCode()){
                    mqMessages.add(message);
                }
            }
            result = Result.buildSucc(mqMessages);
        } catch (Exception e) {
            e.printStackTrace();
            result = Result.buildFail("", "", e.getMessage());
        }
        return result;
    }

}
