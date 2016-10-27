package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.store.service.StoreService;

/**
 * 存储管理接口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public interface StoreManager {

    void start() throws Exception;

    void shutdown();

    StoreService getMQStoreService(MQType mqType);

}
