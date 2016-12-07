package store;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.store.MessageAggregateScheduledService;
import com.tongbanjie.tevent.store.model.AggregateType;
import com.tongbanjie.tevent.store.service.RocketMQStoreService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/5
 */
public class MessageAggregateTest extends BaseTest{

    @Resource
    private MessageAggregateScheduledService messageAggregateScheduledService;

    @Resource
    private RocketMQStoreService rocketMQStoreService;

    @Test
    public void messageAggregate(){
        messageAggregateScheduledService.createAggregatePlan(rocketMQStoreService,
                MQType.ROCKET_MQ, AggregateType.TO_SEND);
    }
}
