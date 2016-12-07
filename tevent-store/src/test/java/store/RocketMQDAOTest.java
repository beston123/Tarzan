package store;

import com.tongbanjie.tevent.common.Constants;
import com.tongbanjie.tevent.common.PagingParam;
import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.common.message.TransactionState;
import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.common.util.DateUtils;
import com.tongbanjie.tevent.common.util.ResultValidate;
import com.tongbanjie.tevent.common.util.Timeout;
import com.tongbanjie.tevent.store.dao.RocketMQMessageDAO;
import com.tongbanjie.tevent.store.query.MQMessageQuery;
import com.tongbanjie.tevent.store.service.RocketMQStoreService;
import com.tongbanjie.tevent.common.util.DistributedIdGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RocketMQ DAO 测试<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/store-context.xml")
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
                        System.out.println(result.getErrorDetail());
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
        MQMessageQuery query = new MQMessageQuery();
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
        System.out.println("Costs: " + (System.currentTimeMillis() - start) + "ms");
        if(CollectionUtils.isNotEmpty(list)){
            System.out.println("TEST_KEY_" + 99999 +", "+ new String(list.get(0).getMessageBody()));
        }

    }

    @Test
    public void selectWithPage() throws ParseException {
        MQMessageQuery query = new MQMessageQuery();
        query.setTransactionState(TransactionState.PREPARE.getCode());
        query.setCreateTimeFrom(DateUtils.tryParse("2016-11-23 20:10:00"));
        query.setCreateTimeTo(DateUtils.tryParse("2016-11-23 20:20:00"));
        Result<List<RocketMQMessage>> dataRet;
        PagingParam pagingParam = new PagingParam(2048, 10014);
        int count = 0;
        Timeout timeout = new Timeout(600*1000);
        while (timeout.validate()){
            dataRet = rocketMQStoreService.selectByCondition(query, pagingParam);
            ResultValidate.isTrue(dataRet);
            //处理
            //toCheckMessage(dataRet.getData(), mqType);
            count += dataRet.getData().size();
            System.out.println(String.format("第%s页，共%s条.", pagingParam.getPageNo(), dataRet.getData().size()));

            if(pagingParam.hasNextPage()){
                pagingParam.nextPage();
            }else{
                break;
            }
        }
        System.out.println("Query Count "+count);
    }


}
