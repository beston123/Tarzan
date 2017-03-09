package store;

import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.common.PagingParam;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.message.RocketMQMessage;
import com.tongbanjie.tarzan.common.message.TransactionState;
import com.tongbanjie.tarzan.common.util.DateUtils;
import com.tongbanjie.tarzan.common.util.DistributedIdGenerator;
import com.tongbanjie.tarzan.store.dao.RocketMQMessageDAO;
import com.tongbanjie.tarzan.store.query.MQMessageQuery;
import com.tongbanjie.tarzan.store.service.RocketMQStoreService;
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
        for(int i=0; i<256; i++){
            final RocketMQMessage mqMessage = new RocketMQMessage();
            mqMessage.setMessageKey("2017_"+String.valueOf(System.currentTimeMillis()));
            mqMessage.setProducerGroup(Constants.TARZAN_TEST_P_GROUP);
            mqMessage.setTopic(Constants.TARZAN_TEST_TOPIC);
            mqMessage.setTransactionState(TransactionState.PREPARE.getCode());
            mqMessage.setMessageBody(("TEST_" + mqMessage.getMessageKey()).getBytes());
            Result<Long> result = RocketMQMessageTest.this.rocketMQStoreService.put(mqMessage);
            if(!result.isSuccess()){
                throw new RuntimeException(result.getErrorDetail());
            }
        }
    }

    @Test
    public void selectByPrimaryKey(){
        Long tid =3593673693466624L;
        RocketMQMessage rocketMQMessage = rocketMQMessageDAO.selectByPrimaryKey(tid);
        Assert.notNull(rocketMQMessage);
        Assert.isTrue(tid.equals(rocketMQMessage.getId()));
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
        messageQuery.setCreateTimeFromInclude(DateUtils.tryParse("2017-01-09 11:40:50"));
        messageQuery.setCreateTimeToExclude(DateUtils.tryParse("2017-01-29 11:40:51"));
        messageQuery.setPagingParam(new PagingParam(5));
        List<RocketMQMessage> list = rocketMQMessageDAO.selectByCondition(messageQuery);
        for(RocketMQMessage mqMessage : list){
            System.out.println(mqMessage.getId());
        }
    }

}
