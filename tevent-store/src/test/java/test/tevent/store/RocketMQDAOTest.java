package test.tevent.store;

import com.tongbanjie.tevent.common.Constants;
import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.common.message.TransactionState;
import com.tongbanjie.tevent.store.Result;
import com.tongbanjie.tevent.store.dao.RocketMQMessageDAO;
import com.tongbanjie.tevent.store.query.RocketMQMessageQuery;
import com.tongbanjie.tevent.store.service.RocketMQStoreService;
import com.tongbanjie.tevent.store.util.DistributedIdGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.*;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-context.xml")
public class RocketMQDAOTest {

    @Resource
    private RocketMQMessageDAO rocketMQMessageDAO;

    @Resource
    private RocketMQStoreService rocketMQStoreService;

    private ExecutorService executorService = new ThreadPoolExecutor(//
            20,//
            20,//
            1000 * 60,//
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(500000));

    @Before
    public void before(){
        DistributedIdGenerator.setUniqueWorkId(1);
    }

    @Test
    public void insert() throws InterruptedException {
        for(int i=100000; i<120000; i++){
            final RocketMQMessage mqMessage = new RocketMQMessage();
            mqMessage.setMessageKey("TEST_KEY_" + i);
            mqMessage.setProducerGroup(Constants.TEVENT_TEST_P_GROUP);
            mqMessage.setTopic(Constants.TEVENT_TEST_TOPIC);
            mqMessage.setTransactionState(TransactionState.PREPARE.getCode());
            mqMessage.setMessageBody(("BODY_JJJJ_" + i).getBytes());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Result<Long> result = RocketMQDAOTest.this.rocketMQStoreService.put(mqMessage);
                    if(!result.isSuccess()){
                        System.out.println(result.getErrorString());
                    }
                }
            });
        }
        executorService.shutdown();
        while(!executorService.isTerminated()){
            Thread.sleep(500);
        }
    }

    @Test
    public void select(){
        RocketMQMessageQuery query = new RocketMQMessageQuery();
        query.setMessageKey("TEST_KEY_" + 10000);
        long start = System.currentTimeMillis();
        List<RocketMQMessage> list = rocketMQMessageDAO.selectByCondition(query);
        System.out.println("Costs: " + (System.currentTimeMillis() - start) + "ms");

        if(CollectionUtils.isNotEmpty(list)){
            System.out.println("TEST_KEY_" + 10000 +", "+ new String(list.get(0).getMessageBody()));
        }

        query.setMessageKey("TEST_KEY_" + 99999);
        start = System.currentTimeMillis();
        list = rocketMQMessageDAO.selectByCondition(query);
        System.out.println("Costs: " +(System.currentTimeMillis()-start)+"ms");
        if(CollectionUtils.isNotEmpty(list)){
            System.out.println("TEST_KEY_" + 99999 +", "+ new String(list.get(0).getMessageBody()));
        }

    }


}
