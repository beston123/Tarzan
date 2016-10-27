package test.tevent.registry;

import com.tongbanjie.tevent.registry.zookeeper.ZkConstants;
import org.I0Itec.zkclient.ZkClient;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/24
 */
public class CleanServerIds {


    public static void main(String[] args) {

        //配置加载
        ZkClient zkClient = new ZkClient("192.168.1.120:2181", ZkConstants.SESSION_TIMEOUT, ZkConstants.CONNECTION_TIMEOUT);
        for(int i=0; i<32; i++){
            String serverIdPath = ZkConstants.SERVER_IDS_ROOT + ZkConstants.PATH_SEPARATOR + i;
            if(zkClient.exists(serverIdPath)){
                zkClient.delete(serverIdPath);
            }
        }
    }

}
