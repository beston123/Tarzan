package com.tongbanjie.tevent.store.service;

import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.store.Result;

import java.util.List;

/**
 * MQ 存储服务<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public interface StoreService<T extends MQMessage> {

    String errorCode = "STORE_ERROR";
    String errorMsg = "消息存储异常";

    Result<Long> put(T mqMessage);

    Result<T> get(Long id);

    Result<T> update(Long id, T mqMessage);

    Result<List<T>> getPreparedAndTimeOut(int timeOutSec);

}
