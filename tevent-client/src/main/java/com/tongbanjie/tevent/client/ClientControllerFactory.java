package com.tongbanjie.tevent.client;

import com.tongbanjie.tevent.rpc.netty.NettyClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class ClientControllerFactory {

    private static final ClientControllerFactory instance = new ClientControllerFactory();

    private final ReentrantLock createLock = new ReentrantLock();

    private final ConcurrentHashMap<String, ClientController> clientTable = new ConcurrentHashMap<String, ClientController>();

    public static final String DEFAULT_CLIENT = "DEFAULT_CLIENT";

    private ClientControllerFactory(){}

    public static ClientControllerFactory getInstance(){
        return instance;
    }

    public ClientController getAndCreate(){
        ClientController instance = this.clientTable.get(DEFAULT_CLIENT);
        if (null != instance) {
            return instance;
        }
        try {
            createLock.lock();

            instance = this.clientTable.get(DEFAULT_CLIENT);
            if (null != instance) {
                return instance;
            }

            instance = new ClientController(new ClientConfig(), new NettyClientConfig());
            this.clientTable.put(DEFAULT_CLIENT, instance);

            return instance;
        }finally {
            createLock.unlock();
        }
    }

}
