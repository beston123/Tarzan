package com.tongbanjie.tarzan.store.service;

import com.tongbanjie.tarzan.store.mapper.ToSendMessageMapper;
import com.tongbanjie.tarzan.store.model.ToSendMessage;
import com.tongbanjie.tarzan.store.query.ToSendMessageQuery;
import com.tongbanjie.tarzan.common.FailResult;
import com.tongbanjie.tarzan.common.PagingParam;
import com.tongbanjie.tarzan.common.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 〈待发送消息 服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/30
 */
@Service
public class ToSendMessageService {

    @Resource
    private ToSendMessageMapper toSendMessageDAO;

    public Result<Boolean> exists(Long tid){
        ToSendMessage toSendMessage = toSendMessageDAO.selectByPrimaryKey(tid);
        return Result.buildSucc(toSendMessage != null);
    }

    public Result<Void> insert(ToSendMessage toSendMessage) {
        Result<Void> result;
        try {
            toSendMessageDAO.insert(toSendMessage);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            Result<Boolean> ret = exists(toSendMessage.getTid());
            //如果记录已存在 则返回成功
            if(ret.isSuccess() && ret.getData()){
                result = Result.buildSucc(null);
            }else{
                result = Result.buildFail(FailResult.STORE, e.getMessage());
            }
        }
        return result;
    }

    public Result<List<ToSendMessage>> query(ToSendMessageQuery query, PagingParam pagingParam) {
        Result<List<ToSendMessage>> result;
        try {
            query.setPagingParam(pagingParam);
            List<ToSendMessage> list = toSendMessageDAO.selectByCondition(query);
            result = Result.buildSucc(list);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Integer> count(ToSendMessageQuery query) {
        Result<Integer> result;
        try {
            Integer count = toSendMessageDAO.selectCountByCondition(query);
            result = Result.buildSucc(count);
        }catch (Exception e){
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Void> delete(Long tid) {
        Result<Void> result;
        try {
            toSendMessageDAO.deleteByPrimaryKey(tid);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Void> update(ToSendMessage toCheckMessage){
        Result<Void> result;
        try {
            toSendMessageDAO.updateByPrimaryKeySelective(toCheckMessage);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Void> incrRetryCount(Long tid){
        Result<Void> result;
        try {
            toSendMessageDAO.incrRetryCount(tid);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }
}
