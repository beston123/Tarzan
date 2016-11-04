package cluster;

import com.tongbanjie.tevent.cluster.loadbalance.LoadBalance;
import com.tongbanjie.tevent.cluster.loadbalance.RoundRobinLoadBalance;
import com.tongbanjie.tevent.cluster.loadbalance.WeightedRandomLoadBalance;
import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.registry.ServerAddress;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/2
 */
public class LoadBalanceTest{

    @Test
    public void roundRobin() {
        List<Address> list = new ArrayList<Address>();

        for(int i=0; i<5; i++){
            Address address1 = new ServerAddress("192.168.1.1", i);
            list.add(address1);
        }

        LoadBalance<Address> loadBalance = new RoundRobinLoadBalance<Address>();

        for(int j=0; j< 12; j++){
            System.out.println(">>>>"+j+":"+loadBalance.select(list));
        }
        System.out.println("************************ list changed ************************");

        Collections.reverse(list);
        for(int j=12; j< 24; j++){
            System.out.println(">>>>" + j + ":" + loadBalance.select(list));
        }
    }

    @Test
    public void weightedRandom() {
        List<Address> list = new ArrayList<Address>();
        for(int i=0; i<5; i++){
            short weight = Short.parseShort((i+1)+"");
            Address address1 = new ServerAddress("192.168.1.1", i , weight);
            list.add(address1);
        }

        LoadBalance<Address> loadBalance = new WeightedRandomLoadBalance<Address>();

        for(int j=0; j< 100; j++){
            System.out.println(">>>>"+j+":"+loadBalance.select(list));
        }
    }



}
