package store;

import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.store.model.AggregateType;
import com.tongbanjie.tevent.store.model.MessageAggregatePlan;
import com.tongbanjie.tevent.store.service.MessageAggregatePlanService;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/1
 */
public class MessageBatchTest extends BaseTest{

    @Resource
    private MessageAggregatePlanService messageAggregatePlanService;

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
