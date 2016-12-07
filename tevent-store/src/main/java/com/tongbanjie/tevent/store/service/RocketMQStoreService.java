package com.tongbanjie.tevent.store.service;

import com.tongbanjie.tevent.common.FailResult;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.common.message.SendStatus;
import com.tongbanjie.tevent.common.PagingParam;
import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.store.dao.RocketMQMessageDAO;
import com.tongbanjie.tevent.store.model.ToCheckMessage;
import com.tongbanjie.tevent.store.model.ToSendMessage;
import com.tongbanjie.tevent.store.query.MQMessageQuery;
import com.tongbanjie.tevent.store.query.ToCheckMessageQuery;
import com.tongbanjie.tevent.store.query.ToSendMessageQuery;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * RocketMQ 存储服务<p>
 * 〈功能详细描述〉

 * @author zixiao
 * @date 16/10/9
 */
@Service
public class RocketMQStoreService implements StoreService<RocketMQMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQStoreService.class);

    @Resource
    private RocketMQMessageDAO rocketMQMessageDAO;

    @Resource
    private ToCheckMessageService toCheckMessageService;

    @Resource
    private ToSendMessageService toSendMessageService;

    @Override
    public Result<Long> put(RocketMQMessage mqMessage) {
        Result<Long> result;
        try {
            Validate.notNull(mqMessage, "mqMessage can not be null");
            Validate.notNull(mqMessage.getProducerGroup(), "producerGroup can not be null");
            Validate.notNull(mqMessage.getTopic(), "topic can not be null");
            Validate.notNull(mqMessage.getTransactionState(), "transactionState can not be null");
            mqMessage.setSendStatus(SendStatus.INITIAL.getCode());
            mqMessage.setHasAggregated(false);
            rocketMQMessageDAO.insert(mqMessage);
            result = Result.buildSucc(mqMessage.getId());
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": put error, message:"+mqMessage, e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<RocketMQMessage> get(Long id) {
        Result<RocketMQMessage> result;
        try {
            Validate.notNull(id, "id can not be null");
            RocketMQMessage mqMessage = rocketMQMessageDAO.selectByPrimaryKey(id);
            result = Result.buildSucc(mqMessage);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": get error, id:"+id, e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Void> update(Long id, RocketMQMessage mqMessage) {
        Result<Void> result;
        try {
            Validate.notNull(mqMessage, "mqMessage can not be null");
            Validate.notNull(id, "id can not be null");
            mqMessage.setId(id);
            rocketMQMessageDAO.updateByPrimaryKeySelective(mqMessage);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": update error, message:"+mqMessage, e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<List<RocketMQMessage>> selectByCondition(MQMessageQuery query, PagingParam pagingParam) {
        Result<List<RocketMQMessage>> result;
        try {
            query.setPagingParam(pagingParam);
            List<RocketMQMessage> messageList = rocketMQMessageDAO.selectByCondition(query);
            result = Result.buildSucc(messageList);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": selectByCondition error, pagingParam = " + pagingParam, e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Integer> countByCondition(MQMessageQuery query) {
        Result<Integer> result;
        try {
            int count = rocketMQMessageDAO.countByCondition(query);
            result = Result.buildSucc(count);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ":  countByCondition error.", e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<List<RocketMQMessage>> queryByMessageKey(String messageKey) {
        Result<List<RocketMQMessage>> result;
        try {
            Validate.notNull(messageKey, "messageKey can not be null");
            MQMessageQuery query = new MQMessageQuery();
            query.setMessageKey(messageKey);
            List<RocketMQMessage> mqMessageList = rocketMQMessageDAO.selectByCondition(query);
            result = Result.buildSucc(mqMessageList);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": get error, messageKey:"+messageKey, e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }


    @Override
    public Result<List<RocketMQMessage>> getToCheck(MQType mqType, PagingParam pagingParam) {
        ToCheckMessageQuery query = new ToCheckMessageQuery();
        query.setMqType(mqType.getCode());
        Result<List<ToCheckMessage>> listResult = toCheckMessageService.query(query, pagingParam);
        if(!listResult.isSuccess()){
            return Result.buildFail(FailResult.STORE, listResult.getExceptionMsg());
        }
        Result<List<RocketMQMessage>> result;
        try {
            List<RocketMQMessage> mqMessages = new ArrayList<RocketMQMessage>(listResult.getData().size());
            for(ToCheckMessage toCheckMessage : listResult.getData()){
                RocketMQMessage mqMessage = rocketMQMessageDAO.selectByPrimaryKey(toCheckMessage.getTid());
                if(mqMessage != null){
                    mqMessages.add(mqMessage);
                }
            }
            result = Result.buildSucc(mqMessages);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ":  getToCheck error.", e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Integer> countToCheck(MQType mqType){
        ToCheckMessageQuery query = new ToCheckMessageQuery();
        query.setMqType(mqType.getCode());
        return toCheckMessageService.count(query);
    }

    @Override
    public Result<List<RocketMQMessage>> getToSend(MQType mqType, PagingParam pagingParam) {
        ToSendMessageQuery query = new ToSendMessageQuery();
        query.setMqType(mqType.getCode());
        Result<List<ToSendMessage>> listResult = toSendMessageService.query(query, pagingParam);
        if(!listResult.isSuccess()){
            return Result.buildFail(FailResult.STORE, listResult.getExceptionMsg());
        }
        Result<List<RocketMQMessage>> result;
        try {
            List<RocketMQMessage> mqMessages = new ArrayList<RocketMQMessage>(listResult.getData().size());
            for(ToSendMessage toSendMessage : listResult.getData()){
                RocketMQMessage mqMessage = rocketMQMessageDAO.selectByPrimaryKey(toSendMessage.getTid());
                if(mqMessage != null){
                    mqMessages.add(mqMessage);
                }
            }
            result = Result.buildSucc(mqMessages);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ":  getToSend error.", e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Integer> countToSend(MQType mqType) {
        ToSendMessageQuery query = new ToSendMessageQuery();
        query.setMqType(mqType.getCode());
        return toSendMessageService.count(query);
    }

    @Override
    public Result<Date> getEarliestCreateTime() {
        Result<Date> result;
        try {
            List<RocketMQMessage> list = rocketMQMessageDAO.getEarliest();
            if(list.size() == 0){
                result = Result.buildSucc(null);
            }else{
                result = Result.buildSucc(list.get(0).getCreateTime());
            }
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": getEarliestCreateTime error.", e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Void> updateAggregated(Long id) {
        RocketMQMessage record = new RocketMQMessage();
        record.setId(id);
        record.setHasAggregated(true);
        return update(id, record);
    }

    @Override
    public Result<Void> updateSendSuccess(Long id) {
        RocketMQMessage record = new RocketMQMessage();
        record.setId(id);
        record.setSendStatus(SendStatus.SUCCESS.getCode());
        return update(id, record);
    }

}
