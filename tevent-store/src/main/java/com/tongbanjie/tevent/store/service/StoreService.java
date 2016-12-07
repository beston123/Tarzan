package com.tongbanjie.tevent.store.service;

import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.common.PagingParam;
import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.store.query.MQMessageQuery;

import java.util.Date;
import java.util.List;

/**
 * MQ 存储服务<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public interface StoreService<T extends MQMessage> {

    String errorMsg = "数据库操作异常";

    /************************************** 基本增删改查 **************************************/

    Result<Long> put(T mqMessage);

    Result<T> get(Long id);

    Result<Void> update(Long id, T mqMessage);

    Result<List<T>> selectByCondition(MQMessageQuery query, PagingParam pagingParam);

    Result<Integer> countByCondition(MQMessageQuery query);

    /**
     * 按MessageKey查询
     * @param messageKey
     * @return
     */
    Result<List<T>> queryByMessageKey(String messageKey);

    /**
     * 查询待检查事务状态的消息列表
     * @param mqType
     * @param pagingParam
     * @return
     */
    Result<List<T>> getToCheck(MQType mqType, PagingParam pagingParam);

    Result<Integer> countToCheck(MQType mqType);

    /**
     * 查询待发送的消息列表
     * @param mqType
     * @param pagingParam
     * @return
     */
    Result<List<T>> getToSend(MQType mqType, PagingParam pagingParam);

    Result<Integer> countToSend(MQType mqType);

    /**
     * 查询最早的创建时间
     * @return
     */
    Result<Date> getEarliestCreateTime();

    /**
     * 更新HasAggregated为True
     * @return
     */
    Result<Void> updateAggregated(Long id);

    Result<Void> updateSendSuccess(Long id);

}
