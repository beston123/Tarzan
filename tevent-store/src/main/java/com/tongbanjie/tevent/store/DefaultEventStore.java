package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.store.config.EventStoreConfig;
import com.tongbanjie.tevent.store.service.MQStoreService;
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
public class DefaultEventStore implements EventStore {

    private EventStoreConfig eventStoreConfig;

    private Map<MQType, MQStoreService> mqStoreServiceMap = new ConcurrentHashMap<MQType, MQStoreService>();

    public DefaultEventStore(EventStoreConfig eventStoreConfig) throws IOException{
        this.eventStoreConfig = eventStoreConfig;
    }

    @Override
    public boolean load() {
        mqStoreServiceMap.put(MQType.ROCKET_MQ, new RocketMQStoreService(this.eventStoreConfig));
        return true;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public MQStoreService getMQStoreService() {
        return mqStoreServiceMap.get(MQType.ROCKET_MQ);
    }

}
