package com.tongbanjie.tarzan.server.handler;

import com.tongbanjie.tarzan.server.ServerController;
import com.tongbanjie.tarzan.common.message.MQType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MQ消息处理者工厂<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
public class MQMessageHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MQMessageHandlerFactory.class);

    private static final MQMessageHandlerFactory MQ_MESSAGE_HANDLER_FACTORY = new MQMessageHandlerFactory();

    private final ReentrantLock createLock = new ReentrantLock();

    private final ConcurrentHashMap<MQType, MQMessageHandler> handlerTable = new ConcurrentHashMap<MQType, MQMessageHandler>();

    private MQMessageHandlerFactory(){}

    public static MQMessageHandlerFactory getInstance(){
        return MQ_MESSAGE_HANDLER_FACTORY;
    }

    /**
     * 创建实例
     * @param mqType
     * @return
     */
    public MQMessageHandler getAndCreate(MQType mqType, ServerController serverController){
        MQMessageHandler instance = this.handlerTable.get(mqType);
        if (null != instance) {
            return instance;
        }
        try {
            createLock.lock();

            instance = this.handlerTable.get(mqType);
            if (null != instance) {
                return instance;
            }

            instance = createHandler(mqType, serverController);
            this.handlerTable.put(mqType, instance);

            return instance;
        }finally {
            createLock.unlock();
        }
    }

    private MQMessageHandler createHandler(MQType mqType, ServerController serverController){
        switch (mqType){
            case ROCKET_MQ:
                return new RocketMQHandler(serverController);
            case RABBIT_MQ:
                return new RabbitMQHandler(serverController);
            default:
                LOGGER.warn("Unsupported mqType '{}'", mqType);
                break;
        }
        return null;
    }


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for(int i=0; i<1000;i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    MQMessageHandlerFactory.getInstance().getAndCreate(MQType.RABBIT_MQ, null);
                }
            });
        }
    }

}
