package com.tongbanjie.tevent.store.service;

import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.common.message.SendStatus;
import com.tongbanjie.tevent.common.message.TransactionState;
import com.tongbanjie.tevent.store.PagingParam;
import com.tongbanjie.tevent.store.Result;
import com.tongbanjie.tevent.store.dao.RocketMQMessageDAO;
import com.tongbanjie.tevent.store.query.RocketMQMessageQuery;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * RocketMQ 存储服务<p>
 * 〈功能详细描述〉

 * @author zixiao
 * @date 16/10/9
 */
@Service
public class RocketMQStoreService implements StoreService<RocketMQMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQStoreService.class);

    /**
     * 最大重试次数
     */
    private final int maxRetryTimes = 10;

    @Resource
    private RocketMQMessageDAO rocketMQMessageDAO;

    @Override
    public Result<Long> put(RocketMQMessage mqMessage) {
        Result<Long> result;
        try {
            mqMessage.setSendStatus(SendStatus.INITIAL.ordinal());
            mqMessage.setRetryTimes(0);
            rocketMQMessageDAO.insert(mqMessage);
            result = Result.buildSucc(mqMessage.getId());
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": put error, message:"+mqMessage, e);
            result = Result.buildFail(StoreService.errorCode, StoreService.errorMsg, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<RocketMQMessage> get(Long id) {
        Result<RocketMQMessage> result;
        try {
            RocketMQMessage mqMessage = rocketMQMessageDAO.selectById(id);
            result = Result.buildSucc(mqMessage);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": get error, id:"+id, e);
            result = Result.buildFail(StoreService.errorCode, StoreService.errorMsg, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<RocketMQMessage> update(Long storeId, RocketMQMessage mqMessage) {
        Result<RocketMQMessage> result;
        try {
            mqMessage.setId(storeId);
            rocketMQMessageDAO.updateById(mqMessage);
            result = Result.buildSucc(mqMessage);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": update error, message:"+mqMessage, e);
            result = Result.buildFail(StoreService.errorCode, StoreService.errorMsg, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<List<RocketMQMessage>> getPreparedAndTimeOut(int timeOutSec, PagingParam pagingParam) {
        Result<List<RocketMQMessage>> result;
        /******1、构造参选参数******/
        RocketMQMessageQuery query = new RocketMQMessageQuery();
        query.setTransactionState(TransactionState.PREPARE.getCode());
        //消息没有超时
        if(timeOutSec > 0){
            query.setCreateTimeTo(getTimeLine(timeOutSec));
        }
        //没有超过最大重试次数
        query.setRetryTimesTo(maxRetryTimes - 1);

        try {

            /******2、首次查询获取总记录数******/
            if(pagingParam.getTotalCount() == 0){
                int total = rocketMQMessageDAO.countByCondition(query);
                pagingParam.setTotalCount(total);
            }
            List<RocketMQMessage> messageList = null;
            if(pagingParam.getTotalCount() == 0){
                messageList =  new ArrayList<RocketMQMessage>(0);
            }else{
                messageList = rocketMQMessageDAO.selectByCondition(query, pagingParam);
            }
            result = Result.buildSucc(messageList);
        } catch (Exception e) {
            LOGGER.error(StoreService.errorMsg + ": getPreparedAndTimeOut error", e);
            result = Result.buildFail(StoreService.errorCode, StoreService.errorMsg, e.getMessage());
        }
        return result;
    }


    private Date getTimeLine(int timeOutSec){
        return DateUtils.addSeconds(new Date(), timeOutSec);
    }

}
