package registry;

import com.tongbanjie.tarzan.registry.Address;
import com.tongbanjie.tarzan.registry.ServerAddress;
import com.tongbanjie.tarzan.registry.ServerRegistry;
import com.tongbanjie.tarzan.registry.zookeeper.ServerZooKeeperRegistry;
import com.tongbanjie.tarzan.registry.zookeeper.ZkConstants;
import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ServerRegistryTest <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/24
 */
public class ServerRegistryTest {

    private ZkClient zkClient;

    @Before
    public void init(){
        PropertyConfigurator.configure("log4j.properties");
        zkClient = new ZkClient("zk.tbj.com:2181",
                ZkConstants.SESSION_TIMEOUT, ZkConstants.CONNECTION_TIMEOUT);
    }

    @After
    public void destroy(){
        zkClient.close();
    }

    @Test
    public void cleanServers() {
        List<String> pathList = zkClient.getChildren(ZkConstants.SERVERS_ROOT);
        for(String path : pathList){
            Address address = zkClient.readData(ZkConstants.SERVERS_ROOT + ZkConstants.PATH_SEPARATOR + path);
            zkClient.delete(ZkConstants.SERVERS_ROOT + ZkConstants.PATH_SEPARATOR + path);
            System.out.println("Clean server '" + path + "':" + address.getAddress());
        }
    }

    @Test
    public void cleanServerIds(){
        for(int i=0; i<32; i++){
            String serverIdPath = ZkConstants.SERVER_IDS_ROOT + ZkConstants.PATH_SEPARATOR + i;
            if(zkClient.exists(serverIdPath)){
                zkClient.delete(serverIdPath);
                System.out.println("Clean server id '" + i + "' success.");
            }
        }
    }

    @Test
    public void printServers(){
        List<String> pathList = zkClient.getChildren(ZkConstants.SERVERS_ROOT);
        for(String path : pathList){
            Address address = zkClient.readData(ZkConstants.SERVERS_ROOT + ZkConstants.PATH_SEPARATOR + path);
            System.out.println("Server '" + path + "':" + address.getAddress());
        }
    }

    @Test
    public void disableServer(){
        List<String> pathList = zkClient.getChildren(ZkConstants.SERVERS_ROOT);
        if(pathList.size()>1){
            Random random = new Random();
            String path = pathList.get(random.nextInt(pathList.size()+1));
            Address address = zkClient.readData(ZkConstants.SERVERS_ROOT + ZkConstants.PATH_SEPARATOR + path);
            address.setEnable(false);
            zkClient.writeData(ZkConstants.SERVERS_ROOT + ZkConstants.PATH_SEPARATOR + path, address);
            System.out.println("Disable server '"+address+"' success.");
        }else{
            System.out.println("Only one server is running.");
        }
    }

    @Test
    public void register() throws Exception {
        List<ServerRegistry> registries = new ArrayList<ServerRegistry>();
        for(int i=0; i<10; i++){
            ServerRegistry registry = new ServerZooKeeperRegistry("zk.tbj.com:2181");
            registry.start();
            registries.add(registry);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        final AtomicInteger port = new AtomicInteger(2000);
        for (final ServerRegistry registry : registries){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    registry.registerServer(new ServerAddress("127.0.0.1", port.addAndGet(1)), 0, 32);
                    System.out.println("isMaster:"+registry.isMaster());
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);
    }

}
