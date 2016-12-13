package com.tongbanjie.tarzan.store.service;

import com.tongbanjie.tarzan.store.mapper.MessageAggregatePlanMapper;
import com.tongbanjie.tarzan.store.model.AggregateStatus;
import com.tongbanjie.tarzan.store.model.MessageAggregatePlan;
import com.tongbanjie.tarzan.store.query.MessageAggregatePlanQuery;
import com.tongbanjie.tarzan.common.FailResult;
import com.tongbanjie.tarzan.common.PagingParam;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.store.model.AggregateType;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 〈消息汇总计划服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/1
 */
@Service
public class MessageAggregatePlanService {

    private static Logger LOGGER = LoggerFactory.getLogger(MessageAggregatePlanService.class);

    public static final int BATCH_PERIOD_SEC = 600;

    private final PagingParam defaultPagingParam = new PagingParam(5000);

    @Resource
    private MessageAggregatePlanMapper messageAggregatePlanMapper;

    public Result<List<MessageAggregatePlan>> getToDo(MQType mqType, AggregateType aggregateType){
        MessageAggregatePlanQuery query = new MessageAggregatePlanQuery();
        query.setMqType(mqType.getCode());
        if(aggregateType != null){
            query.setAggregateType(aggregateType.getCode());
        }
        query.addStatus(AggregateStatus.INITIAL);
        query.addStatus(AggregateStatus.FAILED);

        if(query.getPagingParam() == null){
            query.setPagingParam(defaultPagingParam);
        }
        Result<List<MessageAggregatePlan>> result;
        try {
            List<MessageAggregatePlan> list = messageAggregatePlanMapper.selectByCondition(query);
            result = Result.buildSucc(list);
        } catch (Exception e) {
            LOGGER.error("getToDo fail.",e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<MessageAggregatePlan> create(Date start, MQType mqType, AggregateType aggregateType){
        Result<MessageAggregatePlan> result;
        try {
            Date now = messageAggregatePlanMapper.getNow();
            Date end = DateUtils.addSeconds(start, BATCH_PERIOD_SEC);
            if(end.after(now) ){
                LOGGER.warn("批次结束时间大于当前时间，等待下一次生成批次");
                return Result.buildSucc(null);
            }
            MessageAggregatePlan messageBatch = new MessageAggregatePlan();
            messageBatch.setTimeStart(start);
            messageBatch.setTimeEnd(end);
            messageBatch.setMqType(mqType.getCode());
            messageBatch.setAggregateType(aggregateType.getCode());
            messageBatch.setStatus(AggregateStatus.INITIAL);
            messageAggregatePlanMapper.insert(messageBatch);
            result = Result.buildSucc(messageBatch);
        } catch (Exception e) {
            LOGGER.error("create fail.",e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<MessageAggregatePlan> getLatest(MQType mqType, AggregateType aggregateType){
        Result<MessageAggregatePlan> result;
        try {
            MessageAggregatePlanQuery query = new MessageAggregatePlanQuery();
            query.setMqType(mqType.getCode());
            query.setAggregateType(aggregateType.getCode());
            result = Result.buildSucc(messageAggregatePlanMapper.getLatest(query));
        } catch (Exception e) {
            LOGGER.error("getLatest fail.",e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Void> updateSuccess(int id, Integer recordCount){
        return updateSuccess(id, recordCount, null);
    }

    public Result<Void> updateSuccess(int id, Integer recordCount, Long cost){
        return updateStatus(id, AggregateStatus.SUCCESS, recordCount, "执行成功", cost);
    }

    public Result<Void> updateFail(int id, Integer recordCount){
        return updateStatus(id, AggregateStatus.FAILED, recordCount, "执行失败", null);
    }

    public Result<Void> updateTimeout(int id, Integer recordCount){
        return updateStatus(id, AggregateStatus.FAILED, recordCount, "执行超时", null);
    }

    private Result<Void> updateStatus(int id, Byte status, Integer recordCount, String remark, Long cost){
        Result<Void> result;
        try {
            MessageAggregatePlan messageBatch = new MessageAggregatePlan();
            messageBatch.setId(id);
            messageBatch.setStatus(status);
            messageBatch.setRecordCount(recordCount);
            messageBatch.setRemark(remark);
            messageBatch.setElapsedTime(cost);
            messageAggregatePlanMapper.updateByPrimaryKeySelective(messageBatch);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            LOGGER.error("updateStatus fail.",e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Date> getNow(){
        Result<Date> result;
        try {
            Date now = messageAggregatePlanMapper.getNow();
            result = Result.buildSucc(now);
        } catch (Exception e) {
            LOGGER.error("get now fail.",e);
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }
}

