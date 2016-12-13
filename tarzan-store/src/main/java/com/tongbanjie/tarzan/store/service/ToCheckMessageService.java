package com.tongbanjie.tarzan.store.service;

import com.tongbanjie.tarzan.common.FailResult;
import com.tongbanjie.tarzan.common.PagingParam;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.store.mapper.ToCheckMessageMapper;
import com.tongbanjie.tarzan.store.model.ToCheckMessage;
import com.tongbanjie.tarzan.store.query.ToCheckMessageQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 〈待检查事务状态 服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/30
 */
@Service
public class ToCheckMessageService {

    @Resource
    private ToCheckMessageMapper toCheckMessageDAO;

    public Result<Boolean> exists(Long tid){
        ToCheckMessage toCheckMessage = toCheckMessageDAO.selectByPrimaryKey(tid);
        return Result.buildSucc(toCheckMessage != null);
    }

    public Result<Void> insert(ToCheckMessage toCheckMessage) {
        Result<Void> result;
        try {
            toCheckMessageDAO.insert(toCheckMessage);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            Result<Boolean> ret = exists(toCheckMessage.getTid());
            //如果记录已存在 则返回成功
            if(ret.isSuccess() && ret.getData()){
                result = Result.buildSucc(null);
            }else{
                result = Result.buildFail(FailResult.STORE, e.getMessage());
            }
        }
        return result;
    }

    public Result<List<ToCheckMessage>> query(ToCheckMessageQuery query, PagingParam pagingParam) {
        Result<List<ToCheckMessage>> result;
        try {
            query.setPagingParam(pagingParam);
            List<ToCheckMessage> list = toCheckMessageDAO.selectByCondition(query);
            result =  Result.buildSucc(list);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Integer> count(ToCheckMessageQuery query) {
        Result<Integer> result;
        try {
            Integer count = toCheckMessageDAO.selectCountByCondition(query);
            result = Result.buildSucc(count);
        }catch (Exception e){
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Void> delete(Long tid) {
        Result<Void> result;
        try {
            toCheckMessageDAO.deleteByPrimaryKey(tid);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Void> update(ToCheckMessage toCheckMessage){
        Result<Void> result;
        try {
            toCheckMessageDAO.updateByPrimaryKeySelective(toCheckMessage);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }

    public Result<Void> incrRetryCount(Long tid){
        Result<Void> result;
        try {
            toCheckMessageDAO.incrRetryCount(tid);
            result = Result.buildSucc(null);
        } catch (Exception e) {
            result = Result.buildFail(FailResult.STORE, e.getMessage());
        }
        return result;
    }
}
