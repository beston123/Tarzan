package common;

import com.tongbanjie.tarzan.common.util.IdWorker;
import com.tongbanjie.tarzan.common.util.Timeout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈Id生成测试〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/9/28
 */
public class IdGenerateTest {

    private ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(20);

    @Before
    public void before(){

    }

    @After
    public void after(){
        executor.shutdown();
    }

    @Test
    public void idGenerate() throws InterruptedException {
        final Map<Integer, AtomicInteger> tables = new TreeMap<Integer, AtomicInteger>();
        for(int i=0; i<256; i++){
            tables.put(i, new AtomicInteger(0));
        }
        final IdWorker idWorker = new IdWorker(0);
        Timeout timeout = new Timeout(10 * 60 * 1000);
        for(int t=0; t<20; t++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 500000; i++) {
                        long id = idWorker.nextId();
                        Long tableIndex = (id % 256);
                        tables.get(tableIndex.intValue()).addAndGet(1);
                    }
                }
            });
        }
        while (executor.getActiveCount() > 0){
            Thread.sleep(1L);
        }
        System.out.println("Costs: "+timeout.cost()+"ms");
        for(Map.Entry<Integer, AtomicInteger> entry : tables.entrySet()){
            System.out.println("Table:"+entry.getKey() +", count:"+ entry.getValue());
        }
    }
}
