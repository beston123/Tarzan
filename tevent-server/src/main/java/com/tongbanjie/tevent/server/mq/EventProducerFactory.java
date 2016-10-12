package com.tongbanjie.tevent.server.mq;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.server.ServerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
public class EventProducerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProducerFactory.class);

    private static final EventProducerFactory eventProducerFactory = new EventProducerFactory();

    private AtomicInteger indexGenerator = new AtomicInteger(0);

    private ReentrantLock createLock = new ReentrantLock();

    private ConcurrentHashMap<MQType/* key */, EventProducer> producerTable =
            new ConcurrentHashMap<MQType, EventProducer>();

    private EventProducerFactory(){}

    public static EventProducerFactory getInstance(){
        return eventProducerFactory;
    }

    /**
     * 创建实例
     * @param mqType
     * @return
     */
    public EventProducer getAndCreate(MQType mqType, ServerController serverController){
        EventProducer instance = this.producerTable.get(mqType);
        if (null != instance) {
            return instance;
        }
        try {
            createLock.lock();

            instance = this.producerTable.get(mqType);
            if (null != instance) {
                return instance;
            }

            instance = createProducer(mqType, serverController);
            this.producerTable.put(mqType, instance);

            return instance;
        }finally {
            createLock.unlock();
        }
    }

    private EventProducer createProducer(MQType mqType, ServerController serverController){
        LOGGER.info("Create {} Producer, count: {}", mqType, indexGenerator.addAndGet(1));
        switch (mqType){
            case ROCKET_MQ:
                return new RocketMQProducer(serverController);
            case RABBIT_MQ:
                return new RabbitMQProducer(serverController);
            default:
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
                    EventProducerFactory.getInstance().getAndCreate(MQType.RABBIT_MQ, null);
                }
            });
        }
    }

}
