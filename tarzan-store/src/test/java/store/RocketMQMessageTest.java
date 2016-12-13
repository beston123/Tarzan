package store;

import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.message.RocketMQMessage;
import com.tongbanjie.tarzan.common.message.TransactionState;
import com.tongbanjie.tarzan.common.util.DateUtils;
import com.tongbanjie.tarzan.common.util.DistributedIdGenerator;
import com.tongbanjie.tarzan.store.dao.RocketMQMessageDAO;
import com.tongbanjie.tarzan.store.query.MQMessageQuery;
import com.tongbanjie.tarzan.store.service.RocketMQStoreService;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * RocketMQMessage 测试<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
public class RocketMQMessageTest extends BaseTest{

    @Resource
    private RocketMQMessageDAO rocketMQMessageDAO;

    @Resource
    private RocketMQStoreService rocketMQStoreService;

    @Before
    public void before(){
        DistributedIdGenerator.setUniqueWorkId(1);
    }

    @Test
    public void insert() throws InterruptedException {
        for(int i=1; i<= 256; i++){
            final RocketMQMessage mqMessage = new RocketMQMessage();
            mqMessage.setMessageKey(RandomUtils.nextLong(100000, 200000) + "_" + Constants.TEVENT_TEST_TOPIC);
            mqMessage.setProducerGroup(Constants.TEVENT_TEST_P_GROUP);
            mqMessage.setTopic(Constants.TEVENT_TEST_TOPIC);
            mqMessage.setTransactionState(TransactionState.PREPARE.getCode());
            mqMessage.setMessageBody(("TEST_" + mqMessage.getMessageKey()).getBytes());
            Result<Long> result = RocketMQMessageTest.this.rocketMQStoreService.put(mqMessage);
            if(!result.isSuccess()){
                System.out.println(result.getErrorDetail());
            }
        }
    }

    @Test
    public void selectByPrimaryKey(){
        Long tid =6542728632467544L;
        RocketMQMessage rocketMQMessage = rocketMQMessageDAO.selectByPrimaryKey(tid);
        Assert.notNull(rocketMQMessage);
        Assert.isTrue(rocketMQMessage.getId() == tid);
    }

    @Test
    public void selectByPrimaryKeys(){
        List<Long> ids = new ArrayList<Long>(5);
        ids.add(6542728632467544L);
        ids.add(6542728625127467L);
        ids.add(6542728595767305L);
        ids.add(6542728578990108L);
        ids.add(6542728532852767L);
        List<RocketMQMessage> list = rocketMQMessageDAO.selectByPrimaryKeys(ids);
        Assert.isTrue(list.size() == ids.size());
    }

    @Test
    public void selectByCondition() throws ParseException {
        MQMessageQuery messageQuery = new MQMessageQuery();
        messageQuery.setCreateTimeFromInclude(DateUtils.tryParse("2016-12-08 15:22:06"));
        messageQuery.setCreateTimeToExclude(DateUtils.tryParse("2016-12-08 15:22:07"));
        List<RocketMQMessage> list = rocketMQMessageDAO.selectByCondition(messageQuery);
        list.size();
    }

}
