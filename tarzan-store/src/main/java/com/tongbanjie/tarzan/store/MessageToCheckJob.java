package com.tongbanjie.tarzan.store;

import com.tongbanjie.tarzan.store.model.AggregateType;
import com.tongbanjie.tarzan.store.model.MessageAggregatePlan;
import com.tongbanjie.tarzan.store.model.ToCheckMessage;
import com.tongbanjie.tarzan.store.query.MQMessageQuery;
import com.tongbanjie.tarzan.store.service.ToCheckMessageService;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.message.MQMessage;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.common.message.TransactionState;
import com.tongbanjie.tarzan.common.util.ResultValidate;
import com.tongbanjie.tarzan.store.service.StoreService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 〈待检查事务状态的消息汇总 任务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/6
 */
@Component
public class MessageToCheckJob extends MessageAggregateScheduledService {

    /**
     * Job锁超时毫秒数
     */
    private final int JOB_EXPIRE_MILLIS = 30 * 60 * 1000;

    @Resource
    private ToCheckMessageService toCheckMessageService;

    @Override
    protected void execute() {
        for(Map.Entry<MQType, StoreService> mqTypeStoreService : storeManager.mqStoreServiceSet()){
            MQType mqType = mqTypeStoreService.getKey();
            StoreService storeService = mqTypeStoreService.getValue();
            //生成[待检测事务]消息汇总计划并处理
            createAggregatePlan(storeService, mqType, AggregateType.TO_CHECK);
            queryAndHandleMessage(storeService, mqType, AggregateType.TO_CHECK);
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
        return "MessageToCheckJob";
    }

    @Override
    protected MQMessageQuery buildQuery(MessageAggregatePlan aggregatePlan) {
        MQMessageQuery query = new MQMessageQuery();
        query.setTransactionState(TransactionState.PREPARE.getCode());
        query.setCreateTimeFromInclude(aggregatePlan.getTimeStart());
        query.setCreateTimeToExclude(aggregatePlan.getTimeEnd());
        query.setHasAggregated(false);
        return query;
    }

    @Override
    protected void handleMessages(StoreService storeService, List<MQMessage> list, MQType mqType) {
        for(MQMessage mqMessage : list){
            toCheckMessage(storeService, mqMessage, mqType);
        }
    }

    /**
     * 汇总事务状态为［PREPARE］的消息到［待检查事务］表
     * @param storeService
     * @param mqMessage
     * @param mqType
     * @throws InterruptedException
     */
    private void toCheckMessage(StoreService storeService, MQMessage mqMessage, MQType mqType) {
        ToCheckMessage toCheckMessage = new ToCheckMessage();
        toCheckMessage.setTid(mqMessage.getId());
        toCheckMessage.setSourceTime(mqMessage.getCreateTime());
        toCheckMessage.setMqType(mqType.getCode());
        toCheckMessage.setRetryCount((short) 0);
        Result<Void> insertRet = toCheckMessageService.insert(toCheckMessage);
        ResultValidate.isTrue(insertRet);
        storeService.updateAggregated(mqMessage.getId());
    }

}
