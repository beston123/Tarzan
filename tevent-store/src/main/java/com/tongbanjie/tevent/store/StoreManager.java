package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.store.service.StoreService;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public interface StoreManager {

    boolean load();

    void start() throws Exception;

    void shutdown();

    StoreService getStoreService();

}
