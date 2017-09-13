package store;

import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.store.MessageToCheckJob;
import com.tongbanjie.tarzan.store.MessageToSendJob;
import com.tongbanjie.tarzan.store.StoreManager;
import com.tongbanjie.tarzan.store.model.AggregateType;
import com.tongbanjie.tarzan.store.model.MessageAggregatePlan;
import com.tongbanjie.tarzan.store.service.MessageAggregatePlanService;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 〈消息汇聚测试〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/5
 */
public class MessageAggregateTest extends BaseTest{

    @Resource
    private MessageToCheckJob messageToCheckJob;

    @Resource
    private MessageToSendJob messageToSendJob;

    @Resource
    private MessageAggregatePlanService messageAggregatePlanService;

    @Resource
    private StoreManager storeManager;

    @Before
    public void before() throws Exception {
        storeManager.start();
    }

    @After
    public void after() {
        storeManager.shutdown();
    }

    @Test
    public void messageToCheck(){
        messageToCheckJob.schedule();
    }

    @Test
    public void messageToSend(){
        messageToSendJob.schedule();
    }

    @Test
    public void create(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        while(true){
            Result<MessageAggregatePlan> latestRet = messageAggregatePlanService.getLatest(MQType.ROCKET_MQ, AggregateType.TO_CHECK);
            Date start;
            MessageAggregatePlan latest = latestRet.getData();
            if(latest != null){
                start = latest.getTimeEnd();
            }else{
                start = new Date();
            }
            Result<MessageAggregatePlan> messageBatchRet = messageAggregatePlanService.create(start, MQType.ROCKET_MQ, AggregateType.TO_CHECK);
            if(messageBatchRet.getData() == null){
                break;
            }
            System.out.println(messageBatchRet.getData());
        }
        stopWatch.stop();
        System.out.println(stopWatch.toString());
    }
}
