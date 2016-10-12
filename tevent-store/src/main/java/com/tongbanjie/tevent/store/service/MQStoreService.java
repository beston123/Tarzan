package com.tongbanjie.tevent.store.service;

import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.store.Result;

/**
 * MQ 存储服务<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public interface MQStoreService<T extends MQMessage> {

    Result put(T mqMessage);

    Result get(Long id);

    Result update(Long id, T mqMessage);

}
