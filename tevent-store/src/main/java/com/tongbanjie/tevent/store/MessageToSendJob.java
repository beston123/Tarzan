package com.tongbanjie.tevent.store;

import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.common.message.MQMessage;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.common.message.SendStatus;
import com.tongbanjie.tevent.common.message.TransactionState;
import com.tongbanjie.tevent.common.util.ResultValidate;
import com.tongbanjie.tevent.store.model.MessageAggregatePlan;
import com.tongbanjie.tevent.store.model.ToSendMessage;
import com.tongbanjie.tevent.store.query.MQMessageQuery;
import com.tongbanjie.tevent.store.service.StoreService;
import com.tongbanjie.tevent.store.service.ToSendMessageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.tongbanjie.tevent.store.model.AggregateType.TO_SEND;

/**
 * 〈待重新发送的消息汇总 任务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/6
 */
@Component
public class MessageToSendJob extends MessageAggregateScheduledService {

    /**
     * Job锁超时毫秒数
     */
    private final int JOB_EXPIRE_MILLIS = 30 * 60 * 1000;

    @Resource
    private ToSendMessageService toSendMessageService;

    @Override
    protected void execute() {
        for(Map.Entry<MQType, StoreService> mqTypeStoreService : storeManager.mqStoreServiceSet()){
            MQType mqType = mqTypeStoreService.getKey();
            StoreService storeService = mqTypeStoreService.getValue();
            //生成[待发送]消息汇总计划并处理
            createAggregatePlan(storeService, mqType, TO_SEND);
            queryAndHandleMessage(storeService, mqType, TO_SEND);
        }
    }

    @Override
    protected String getJobKey() {
        return this.getClass().getCanonicalName();
    }

    @Override
    protected long getJobExpireMillis() {
        return JOB_EXPIRE_MILLIS;
    }

    @Override
    protected String getJobName(){
        return "MessageToSendJob";
    }


    @Override
    protected MQMessageQuery buildQuery(MessageAggregatePlan aggregatePlan) {
        MQMessageQuery query = new MQMessageQuery();
        query.setTransactionState(TransactionState.COMMIT.getCode());
        query.addSendStatus(SendStatus.FAILED.getCode());
        query.addSendStatus(SendStatus.INITIAL.getCode());
        query.setCreateTimeFrom(aggregatePlan.getTimeStart());
        query.setCreateTimeTo(aggregatePlan.getTimeEnd());
        query.setHasAggregated(false);
        return query;
    }

    @Override
    protected void handleMessages(StoreService storeService, List<MQMessage> list, MQType mqType) {
        for(MQMessage mqMessage : list) {
            toSendMessage(storeService, mqMessage, mqType);
        }
    }

    /**
     * 汇总事务状态为［COMMIT］,但发送状态为［FAILED］或［INITIAL］的消息到［待检查事务］表
     * @param storeService
     * @param mqMessage
     * @param mqType
     * @throws InterruptedException
     */
    private void toSendMessage(StoreService storeService,MQMessage mqMessage, MQType mqType) {
        ToSendMessage toSendMessage = new ToSendMessage();
        toSendMessage.setTid(mqMessage.getId());
        toSendMessage.setSourceTime(mqMessage.getCreateTime());
        toSendMessage.setMqType(mqType.getCode());
        if(SendStatus.FAILED.getCode() == mqMessage.getSendStatus()){
            toSendMessage.setRetryCount((short) 1);
        }else{
            toSendMessage.setRetryCount((short) 0);
        }
        Result<Void> insertRet = toSendMessageService.insert(toSendMessage);
        ResultValidate.isTrue(insertRet);
        storeService.updateAggregated(mqMessage.getId());
    }
}
