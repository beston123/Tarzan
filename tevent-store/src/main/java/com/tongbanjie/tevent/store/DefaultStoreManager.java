package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.store.service.RocketMQStoreService;
import com.tongbanjie.tevent.store.service.StoreService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
@Component
public class DefaultStoreManager implements StoreManager {

    private Map<MQType, StoreService> mqStoreServiceMap = new ConcurrentHashMap<MQType, StoreService>();

    @Resource
    private RocketMQStoreService rocketMQStoreService;

    @Override
    public void start() throws Exception {
        mqStoreServiceMap.put(MQType.ROCKET_MQ, rocketMQStoreService);
    }

    @Override
    public void shutdown() {
        mqStoreServiceMap.clear();
    }

    @Override
    public StoreService getStoreService() {
        return mqStoreServiceMap.get(MQType.ROCKET_MQ);
    }

}
