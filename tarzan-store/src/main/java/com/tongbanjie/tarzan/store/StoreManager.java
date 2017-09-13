package com.tongbanjie.tarzan.store;

import com.tongbanjie.tarzan.common.Service;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.store.service.MessageConsumeService;
import com.tongbanjie.tarzan.store.service.StoreService;

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

    MessageConsumeService getMessageConsumeService();

}
