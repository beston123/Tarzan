package com.tongbanjie.tarzan.store.service;

import com.tongbanjie.tarzan.common.FailResult;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.store.mapper.MessageConsumeMapper;
import com.tongbanjie.tarzan.store.model.MessageConsume;
import com.tongbanjie.tarzan.store.query.MessageConsumeQuery;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 〈消息消费结果 服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/16
 */
@Service
public class MessageConsumeService {

    @Resource
    private MessageConsumeMapper messageConsumeMapper;

    /**
     * 是否存在
     * @param tid
     * @param customerGroup
     * @return 存在则返回主键Id，不存在返回null
     */
    public Result<Long> exist(Long tid, String customerGroup){
        Result<Long> result;
        try {
            MessageConsumeQuery messageConsumeQuery = new MessageConsumeQuery();
            messageConsumeQuery.setTid(tid);
            messageConsumeQuery.setConsumerGroup(customerGroup);
            List<MessageConsume> list = messageConsumeMapper.selectByCondition(messageConsumeQuery);
            if(list.size() > 0){
                result = Result.buildSucc(list.get(0).getId());
            }else{
                result = Result.buildSucc(null);
            }
        }catch (Exception e){
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Void> insert(MessageConsume messageConsume) {
        Result<Void> result;
        try {
            messageConsumeMapper.insert(messageConsume);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    /**
     * 更新
     * @param messageConsume 支持3个参数更新:consumer, consumeStatus,reconsumeTimes
     * @return
     */
    public Result<Void> update(MessageConsume messageConsume){
        Result<Void> result;
        try {
            Validate.notNull(messageConsume);
            Validate.notNull(messageConsume.getId());
            messageConsumeMapper.updateByPrimaryKeySelective(messageConsume);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }


    public Result<List<MessageConsume>> query(MessageConsumeQuery messageConsumeQuery) {
        Result<List<MessageConsume>> result;
        try {
            Validate.notNull(messageConsumeQuery, "查询参数不为空");
            List<MessageConsume> list = messageConsumeMapper.selectByCondition(messageConsumeQuery);
            result = Result.buildSucc(list);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Integer> count(MessageConsumeQuery messageConsumeQuery) {
        Result<Integer> result;
        try {
            Validate.notNull(messageConsumeQuery, "查询参数不为空");
            Integer count = messageConsumeMapper.selectCountByCondition(messageConsumeQuery);
            result = Result.buildSucc(count);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Integer> delete(Long id) {
        Result<Integer> result;
        try {
            Validate.notNull(id, "参数Id不为空");
            Integer count = messageConsumeMapper.deleteByPrimaryKey(id);
            result = Result.buildSucc(count);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<MessageConsume> get(Long id) {
        Result<MessageConsume> result;
        try {
            Validate.notNull(id, "参数Id不为空");
            MessageConsume data = messageConsumeMapper.selectByPrimaryKey(id);
            result = Result.buildSucc(data);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }
}
