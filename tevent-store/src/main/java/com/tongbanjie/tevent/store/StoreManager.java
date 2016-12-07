package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.common.Service;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.common.redis.RedisComponent;
import com.tongbanjie.tevent.store.service.StoreService;
import com.tongbanjie.tevent.store.service.ToCheckMessageService;
import com.tongbanjie.tevent.store.service.ToSendMessageService;

import java.util.Map;
import java.util.Set;

/**
 * 存储管理接口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public interface StoreManager extends Service {

    void start() throws Exception;

    void shutdown();

    StoreService getMQStoreService(MQType mqType);

    Set<Map.Entry<MQType, StoreService>> mqStoreServiceSet();

    ToCheckMessageService getToCheckMessageService();

    ToSendMessageService getToSendMessageService();

    RedisComponent getRedisComponent();

}
