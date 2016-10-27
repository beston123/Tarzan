package con.tongbanjie.tevent.mq.rocketmq;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.sender.TransactionCheckListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class RocketMQNotifyManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQNotifyManager.class);

    private RocketMQParam rocketMQParam;

    private final TransactionCheckListener transactionCheckListener;

    private RocketMQClient mqClient;

    public RocketMQNotifyManager(String groupId, String name, String topic, String tag, String namesrvAddr,
                                 TransactionCheckListener transactionCheckListener) {
        this(new RocketMQParam(groupId, name, topic, tag, namesrvAddr), transactionCheckListener);
    }

    public RocketMQNotifyManager(RocketMQParam rocketMQParam,
                                 TransactionCheckListener transactionCheckListener) {
        this.rocketMQParam = rocketMQParam;
        this.transactionCheckListener = transactionCheckListener;
    }

    public void init(){
        try {
            this.rocketMQParam.validate();
            mqClient = new RocketMQClient(rocketMQParam, transactionCheckListener);
        }catch (Exception e){
            LOGGER.error("Init rocketMQ client failed! " + rocketMQParam, e);
        }
    }

    public void sendMessage(Message message){
        this.mqClient.sendMessage(message);
    }

//    @Override
//    public SendResult sendMessage(Message message) {
//        SendResult result = null;
//        try {
//            byte[] body = null;
//            if (message instanceof BytesMessage) {
//                body = ((BytesMessage) message).getBody();
//            }else if(message instanceof StringMessage){
//                body = ((StringMessage) message).getBody().getBytes();
//            }else if(message instanceof DapperMessage){
//                Map map = new HashMap();
//                map.put("body", ((DapperMessage) message).getBody());
//                map.put("attachment", ((DapperMessage) message).getAttachment());
//                body = ObjectUtil.changeObject2byte(map);
//            }else {
//                throw new IllegalArgumentException("message:" + message.getClass() + " not support");
//            }
//            com.alibaba.rocketmq.common.message.Message msg = new com.alibaba.rocketmq.common.message.Message(topic,
//                    message.getTags(),
//                    message.getKeys(),
//                    body);
//            result = producer.send(msg);
//        }catch (Exception e){
//            LOGGER.error("DefaultNotifyManager sendMessage error!",e);
//        }
//        return result;
//    }

}
