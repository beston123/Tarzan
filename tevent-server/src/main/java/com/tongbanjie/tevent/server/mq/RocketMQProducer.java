package com.tongbanjie.tevent.server.mq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.tongbanjie.tevent.common.TransactionState;
import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;
import com.tongbanjie.tevent.rpc.protocol.ResponseCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.common.body.RocketMQBody;
import com.tongbanjie.tevent.rpc.protocol.header.TransactionMessageHeader;
import com.tongbanjie.tevent.server.ServerController;
import com.tongbanjie.tevent.store.Result;
import com.tongbanjie.tevent.store.service.RocketMQStoreService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
public class RocketMQProducer implements EventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQProducer.class);

    private Map<String/* Group */, MQProducer> producerTable = new ConcurrentHashMap<String, MQProducer>();

    private AtomicLong atomicLong = new AtomicLong(0);

    private Lock lock = new ReentrantLock();

    private String ROCKET_MQ_ADDRESS = "192.168.1.42:9876";

    private RocketMQStoreService rocketMQStoreService = new RocketMQStoreService();

    private ServerController serverController;

    public RocketMQProducer(ServerController serverController) {
        this.serverController = serverController;
        rocketMQStoreService = new RocketMQStoreService();
    }

    @Override
    public RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request)
            throws RpcCommandException {
        RpcCommand response = null;
        final RocketMQBody mqBody = request.getBody(RocketMQBody.class);

        SendResult sendResult = sendMessage(mqBody);
        if(sendResult != null){
            response = RpcCommandBuilder.buildSuccess();
            LOGGER.info("发送消息 messageKey:" + mqBody.getMessageKey()
                    + ", result:" + sendResult.getSendStatus()
                    + ", msgId:"+sendResult.getMsgId());
        }else{
            response = RpcCommandBuilder.buildFail("发送消息失败");
        }
        return response;
    }

    private SendResult sendMessage(RocketMQBody mqBody){
        MQProducer producer = null;
        try {
            producer = getProducer(mqBody.getProducerGroup());
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        if(producer == null){
            throw new RuntimeException();
        }

        Message msg = new Message(mqBody.getTopic(),// topic
                mqBody.getTags(),       // tag
                mqBody.getMessageBody() // body
        );
        msg.setKeys(mqBody.getMessageKey());
        try {
            SendResult sendResult = producer.send(msg);
            LOGGER.info("Send status {}, msgId {}", sendResult.getSendStatus(), sendResult.getMsgId());
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        for(MQProducer mqProducer : producerTable.values()){
//            DefaultMQProducer mqProducer1 = (DefaultMQProducer) mqProducer;
//            LOGGER.debug("Group {} client {}", mqProducer1.getProducerGroup(),
//                    mqProducer1.getDefaultMQProducerImpl().getmQClientFactory().getMQClientAPIImpl().getRemotingClient());
//        }
        return null;
    }

    @Override
    public RpcCommand prepareMessage(ChannelHandlerContext ctx, RpcCommand request) {
        final RpcCommand response;

        final RocketMQBody mqBody = request.getBody(RocketMQBody.class);

        RocketMQMessage mqMessage = RocketMQMessage.build(mqBody, TransactionState.PREPARE);

        //持久化 消息
        Result<Long> putResult = rocketMQStoreService.put(mqMessage);
        if(putResult.isSuccess()){
            Long tid = putResult.getData();

            TransactionMessageHeader responseHeader = new TransactionMessageHeader();
            responseHeader.setTransactionId(tid);

            response = RpcCommandBuilder.buildSuccess();
            response.setCustomHeader(responseHeader);

            LOGGER.info("准备消息 topic:" + mqMessage.getTopic()
                    + ", messageKey:" + mqMessage.getMessageKey()
                    + ", transactionId:" + tid );
        }else{
            response = RpcCommandBuilder.buildFail("准备消息失败");
        }

        return response;
    }

    @Override
    public RpcCommand commitMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId) {
        final RpcCommand response;
        final RocketMQBody newMqBody = request.getBody(RocketMQBody.class);

        Result<RocketMQMessage> getResult = rocketMQStoreService.get(transactionId);
        if(getResult.isSuccess()){
            if(getResult.getData() == null){
                response = RpcCommandBuilder.buildResponse(ResponseCode.NOT_EXIST, "该事务消息不存在");
                LOGGER.warn("提交事务消息失败, 该消息不存在, transactionId:" + transactionId);
            }else{
                RocketMQMessage mqMessage = getResult.getData();
                if(newMqBody.getMessageBody() != null){
                    //TODO 更新消息体
                    mqMessage.setMessageBody(newMqBody.getMessageBody());
                }
                //发送消息
                sendMessage(newMqBody);

                LOGGER.info("提交消息 topic:" + mqMessage.getTopic()
                        + ", messageKey:" + mqMessage.getMessageKey()
                        + ", transactionId:" + transactionId );
                response = RpcCommandBuilder.buildSuccess();
            }
        }else{
            response = RpcCommandBuilder.buildFail("提交事务消息失败");
        }
        return response;
    }

    @Override
    public RpcCommand rollbackMessage(ChannelHandlerContext ctx, RpcCommand request, Long transactionId) {
        final RpcCommand response;
        Result<RocketMQMessage> getResult = rocketMQStoreService.get(transactionId);
        if(getResult.isSuccess()){
            if(getResult.getData() == null){
                response = RpcCommandBuilder.buildResponse(ResponseCode.NOT_EXIST, "该事务消息不存在");
                LOGGER.warn("回滚事务消息失败, 消息不存在, transactionId:" + transactionId);
            }else{
                RocketMQMessage mqMessage = getResult.getData();

                LOGGER.info("回滚消息 topic:" + mqMessage.getTopic()
                        + ", messageKey:" + mqMessage.getMessageKey()
                        + ", transactionId:" + transactionId );
                response = RpcCommandBuilder.buildSuccess();
            }
        }else{
            response = RpcCommandBuilder.buildFail("回滚事务消息失败");
        }
        return response;
    }

    private MQProducer getProducer(String group) throws MQClientException {
        MQProducer mqProducer = producerTable.get(group);
        if (mqProducer != null) {
            return mqProducer;
        }
        try {
            lock.lock();

            mqProducer = producerTable.get(group);
            if (mqProducer != null) {
                return mqProducer;
            }

            DefaultMQProducer producer = new DefaultMQProducer(group);
            producer.setNamesrvAddr(ROCKET_MQ_ADDRESS);
            producer.start();

            producerTable.put(group, producer);

            return producer;
        } finally {
            lock.unlock();
        }
    }

}
