package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.common.config.ServerConfig;
import com.tongbanjie.tevent.store.config.EventStoreConfig;

import java.io.IOException;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public class DefaultEventStore implements EventStore {

    private EventStoreConfig eventStoreConfig;

    private ServerConfig serverConfig;

    public DefaultEventStore(EventStoreConfig eventStoreConfig, ServerConfig serverConfig) throws IOException{
        this.eventStoreConfig = eventStoreConfig;
        this.serverConfig = serverConfig;
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void shutdown() {

    }
}
