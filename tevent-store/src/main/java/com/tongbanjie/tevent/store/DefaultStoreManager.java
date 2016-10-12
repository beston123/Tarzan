package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.store.config.StoreConfig;
import com.tongbanjie.tevent.store.service.StoreService;
import com.tongbanjie.tevent.store.service.RocketMQStoreService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public class DefaultStoreManager implements StoreManager {

    private StoreConfig storeConfig;

    private Map<MQType, StoreService> mqStoreServiceMap = new ConcurrentHashMap<MQType, StoreService>();

    public DefaultStoreManager(StoreConfig storeConfig) throws IOException{
        this.storeConfig = storeConfig;
    }

    @Override
    public boolean load() {
        mqStoreServiceMap.put(MQType.ROCKET_MQ, new RocketMQStoreService(this.storeConfig));
        return true;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public StoreService getStoreService() {
        return mqStoreServiceMap.get(MQType.ROCKET_MQ);
    }

}
