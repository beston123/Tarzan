import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.RecoverableRegistry;
import com.tongbanjie.tevent.registry.zookeeper.ClientZooKeeperRegistry;
import com.tongbanjie.tevent.registry.zookeeper.ServerZooKeeperRegistry;
import org.apache.log4j.BasicConfigurator;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public class ZkTest {


    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        String zkAddress = "192.168.1.120:2181";
        final RecoverableRegistry registry = new ServerZooKeeperRegistry(zkAddress);
        registry.start();

        final RecoverableRegistry discovery = new ClientZooKeeperRegistry(zkAddress);
        discovery.start();

        ExecutorService executorService1 = Executors.newFixedThreadPool(10);
        for(int i = 0; i<2000; i++) {
            executorService1.execute(new Runnable() {
                @Override
                public void run() {

                    registry.register(new Address("127.0.0.1", 1000 + new Random().nextInt(2000)));
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(int i = 0; i<2000; i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //discovery.discover();
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        Thread.sleep(Long.MAX_VALUE);
//        String addr = discovery.discover(ZkConstants.SERVERS_ROOT);
//        System.out.println(">>> "+addr);
    }
}
