package com.tongbanjie.tarzan.client.sender;

import com.tongbanjie.tarzan.client.ClientController;
import com.tongbanjie.tarzan.client.transaction.TransactionCheckListener;
import com.tongbanjie.tarzan.common.message.MQType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MQ消息发送者工厂<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public class MQMessageSenderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MQMessageSenderFactory.class);

    private MQMessageSenderFactory(){}

    /**
     * 创建实例
     *
     * @param mqType
     * @param clientController
     * @return
     */
    public static MQMessageSender create(MQType mqType, ClientController clientController){
        return create(mqType, clientController, null);
    }

    /**
     * 创建实例
     *
     * @param mqType
     * @param clientController
     * @param transactionCheckListener
     * @return
     */
    public static MQMessageSender create(MQType mqType, ClientController clientController,
                                         TransactionCheckListener transactionCheckListener){
        switch (mqType){
            case ROCKET_MQ:
                return new RocketMQMessageSender(clientController, transactionCheckListener);
            case KAFKA:
                return new KafkaMQMessageSender(clientController, transactionCheckListener);
            default:
                LOGGER.warn("Unsupported mqType '{}'", mqType);
                break;
        }
        return null;
    }

}
