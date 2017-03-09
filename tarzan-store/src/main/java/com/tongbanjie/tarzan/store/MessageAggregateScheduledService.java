package com.tongbanjie.tarzan.store;

import com.tongbanjie.tarzan.store.model.MessageAggregatePlan;
import com.tongbanjie.tarzan.store.query.MQMessageQuery;
import com.tongbanjie.tarzan.store.service.MessageAggregatePlanService;
import com.tongbanjie.tarzan.common.PagingParam;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.ScheduledService;
import com.tongbanjie.tarzan.common.exception.TimeoutException;
import com.tongbanjie.tarzan.common.message.MQMessage;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.common.redis.RedisComponent;
import com.tongbanjie.tarzan.common.util.NamedSingleThreadFactory;
import com.tongbanjie.tarzan.common.util.ResultValidate;
import com.tongbanjie.tarzan.common.util.Timeout;
import com.tongbanjie.tarzan.store.model.AggregateType;
import com.tongbanjie.tarzan.store.service.StoreService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 〈消息汇总 任务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/1
 */
public abstract class MessageAggregateScheduledService implements ScheduledService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAggregateScheduledService.class);

    /**
     * 每次处理数量
     */
    private final int PAGE_SIZE = 2048;

    /**
     * 每个Plan超时毫秒数
     */
    private final int PLAN_TIMEOUT = 10 * 60 * 1000;

    @Resource
    protected StoreManager storeManager;

    @Resource
    private MessageAggregatePlanService messageAggregatePlanService;

    @Resource
    private RedisComponent redisComponent;

    private ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(new NamedSingleThreadFactory(getJobName()));

    public MessageAggregateScheduledService() {}

    @Override
    public void start() {
        this.scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    schedule();
                } catch (Exception e) {
                    LOGGER.error("MessageAggregateJob 执行失败", e);
                }
            }
        }, RandomUtils.nextInt(30, 120), 10 * 60, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    @Override
    public void schedule(){
        Assert.notNull(getJobKey());
        Assert.isTrue(getJobExpireMillis() > 0L);
        if(!redisComponent.acquireLock(getJobKey(), getJobExpireMillis())){
            LOGGER.warn("Job [{}] 并发执行", getJobName());
            return;
        }
        LOGGER.info("Job [{}] 开始执行", getJobName());
        try{
            execute();
        } finally {
            redisComponent.releaseLock(getJobKey());
        }
        LOGGER.info("Job [{}] 执行结束", getJobName());
    }

    protected abstract void execute();

    protected abstract String getJobKey();

    protected abstract long getJobExpireMillis();

    protected abstract String getJobName();

    /**
     * 创建消息汇总计划
     * @param storeService
     * @param mqType
     * @param aggregateType
     */
    protected void createAggregatePlan(StoreService storeService, MQType mqType, AggregateType aggregateType){
        Timeout timeout = new Timeout(PLAN_TIMEOUT);
        while (!timeout.isTimeout()){
            //1、获取新计划的开始时间
            Date start = getNextPlanTimeStart(storeService, mqType, aggregateType);

            //2、新建下一个计划
            Assert.notNull(start);
            Result<MessageAggregatePlan> aggregatePlanResult = messageAggregatePlanService.create(start, mqType, aggregateType);
            ResultValidate.isTrue(aggregatePlanResult);
            if(aggregatePlanResult.getData() == null){
                break;
            }
        }
    }

    /**
     * 获取下一个计划的开始时间
     *
     * @param storeService
     * @param mqType
     * @param aggregateType
     * @return
     */
    private Date getNextPlanTimeStart(StoreService storeService, MQType mqType, AggregateType aggregateType){
        //1、查找最新一期的计划
        Result<MessageAggregatePlan> latestResult = messageAggregatePlanService.getLatest(mqType, aggregateType);
        ResultValidate.isTrue(latestResult);
        MessageAggregatePlan latest = latestResult.getData();
        Date start;
        if(latest != null){
            //上个周期结束时间
            start = latest.getTimeEnd();
        } else{
            //2、查找数据中最早的创建时间
            Result<Date> ret = storeService.getEarliestCreateTime();
            ResultValidate.isTrue(ret);
            start = ret.getData();
        }
        //3、取当前时间前推一个周期
        if(start == null){
            Result<Date> nowRet = messageAggregatePlanService.getNow();
            ResultValidate.isTrue(nowRet);
            start = DateUtils.addSeconds(nowRet.getData(), -1 * MessageAggregatePlanService.BATCH_PERIOD_SEC);
        }
        return start;
    }

    /**
     * 查询并处理消息
     * @param storeService
     * @param mqType
     * @param aggregateType
     */
    protected void queryAndHandleMessage(StoreService storeService, MQType mqType, AggregateType aggregateType) {
        Result<List<MessageAggregatePlan>> listResult = messageAggregatePlanService.getToDo(mqType, aggregateType);
        ResultValidate.isTrue(listResult);
        for(MessageAggregatePlan aggregatePlan : listResult.getData()){
            /*************** 1、构造查询对象 ***************/
            MQMessageQuery query = buildQuery(aggregatePlan);
            if(query == null){
                continue;
            }

            Timeout timeout = new Timeout(PLAN_TIMEOUT);
            /*************** 2、查询总记录数 ***************/
            Result<Integer> totalRet = storeService.countByCondition(query);
            if(!totalRet.isSuccess()){
                messageAggregatePlanService.updateFail(aggregatePlan.getId(), null, timeout.cost());
                continue;
            }
            int total = totalRet.getData();
            if(total == 0){
                messageAggregatePlanService.updateSuccess(aggregatePlan.getId(), 0, timeout.cost());
                continue;
            }

            /*************** 3、分页查询，并处理***************/
            PagingParam pagingParam = new PagingParam(PAGE_SIZE, total);
            Result<List<MQMessage>> dataRet;
            try {
                for(int times=0; times < pagingParam.getTotalPage(); times++){
                    dataRet = storeService.selectByCondition(query, pagingParam);
                    ResultValidate.isTrue(dataRet);

                    handleMessages(storeService, dataRet.getData(), mqType);

                    //如果当前查询数量小于PAGE_SIZE
                    if( dataRet.getData().size() < PAGE_SIZE){
                        break;
                    }
                    //超时退出
                    timeout.validate();
                }
                LOGGER.info(String.format("Aggregate Plan [%s] exec success, cost %s ms",
                        aggregatePlan.getTimeStart(), timeout.cost()));
                messageAggregatePlanService.updateSuccess(aggregatePlan.getId(), total, timeout.cost());
            } catch (TimeoutException e) {
                LOGGER.error(String.format("Aggregate Plan [%s] exec timeout, cost %s ms",
                        aggregatePlan.getTimeStart(), timeout.cost()), e);
                messageAggregatePlanService.updateTimeout(aggregatePlan.getId(), total, timeout.cost());
            } catch (Exception e){
                LOGGER.error(String.format("Aggregate Plan [%s] exec fail, cost %s ms",
                        aggregatePlan.getTimeStart(), timeout.cost()), e);
                messageAggregatePlanService.updateFail(aggregatePlan.getId(), total, timeout.cost());
            }
        }
    }

    protected abstract MQMessageQuery buildQuery(MessageAggregatePlan aggregatePlan);

    protected abstract void handleMessages(StoreService storeService, List<MQMessage> list, MQType mqType);

}
