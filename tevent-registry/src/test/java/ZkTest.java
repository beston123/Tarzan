import com.tongbanjie.tevent.common.config.ServerConfig;
import com.tongbanjie.tevent.registry.RegistryType;
import com.tongbanjie.tevent.registry.ServiceDiscovery;
import com.tongbanjie.tevent.registry.ServiceRegistry;
import com.tongbanjie.tevent.registry.zookeeper.ZkConstants;
import com.tongbanjie.tevent.registry.zookeeper.ZooKeeperServiceDiscovery;
import com.tongbanjie.tevent.registry.zookeeper.ZooKeeperServiceRegistry;
import org.apache.log4j.BasicConfigurator;

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

        String zkAddress = new ServerConfig().getRegistryAddress();
        ServiceRegistry registry = new ZooKeeperServiceRegistry(RegistryType.SERVER, "192.168.1.120:2181");
        registry.start();

        ServiceDiscovery discovery = new ZooKeeperServiceDiscovery(zkAddress);

        registry.register(ZkConstants.SERVERS_ROOT, "127.0.0.1:1100");
        registry.register(ZkConstants.SERVERS_ROOT, "127.0.0.1:1101");

//        String addr = discovery.discover(ZkConstants.SERVERS_ROOT);
//        System.out.println(">>> "+addr);
    }
}
