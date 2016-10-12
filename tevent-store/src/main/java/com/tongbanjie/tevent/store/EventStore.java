package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.store.service.MQStoreService;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public interface EventStore {

    boolean load();

    void start() throws Exception;

    void shutdown();

    MQStoreService getMQStoreService();

}
