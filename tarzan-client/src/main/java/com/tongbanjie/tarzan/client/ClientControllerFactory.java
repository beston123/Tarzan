package com.tongbanjie.tarzan.client;

import com.tongbanjie.tarzan.rpc.netty.NettyClientConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Client控制器工厂 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class ClientControllerFactory {

    private static final ClientControllerFactory INSTANCE = new ClientControllerFactory();

    public static final String DEFAULT_CLIENT = "DEFAULT_CLIENT";

    private final ReentrantLock createLock = new ReentrantLock();

    private final ConcurrentHashMap<String, ClientController> clientTable = new ConcurrentHashMap<String, ClientController>(1);

    private ClientControllerFactory(){}

    public static ClientControllerFactory getInstance(){
        return INSTANCE;
    }

    public ClientController getAndCreate(ClientConfig clientConfig){
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

            instance = new ClientController(clientConfig, new NettyClientConfig());
            this.clientTable.put(DEFAULT_CLIENT, instance);

            return instance;
        }finally {
            createLock.unlock();
        }
    }

}
