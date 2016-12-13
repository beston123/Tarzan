package rocketmq.client;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tarzan.client.ClientConfig;
import com.tongbanjie.tarzan.client.ClientController;
import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.registry.Address;
import com.tongbanjie.tarzan.common.exception.RpcException;
import com.tongbanjie.tarzan.rpc.netty.NettyClientConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client样例测试 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ExampleClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleClientTest.class);

    private ClientController clientController;

    private ExampleClient client;


    @Before
    public void before() throws InterruptedException, RpcException {
        clientController = new ClientController(new ClientConfig("192.168.1.120:2181"), new NettyClientConfig());
        try {
            clientController.start();
            System.out.println("Client startup success!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(1500);

        client = new ExampleClient(clientController, Constants.TEVENT_TEST_P_GROUP);
        //注册
        client.sendHeartbeat();
    }

    /**
     * 事务消息
     * 模拟本地事务各种情况
     case 0: //事务处理异常
        throw new IOException("Check local transaction exception, db is down.");
     case 1: //事务需要提交
        return LocalTransactionState.COMMIT;
     case 2: //事务需要回滚
        return LocalTransactionState.ROLLBACK;
     default: // state>=3 应用挂掉了,没有事务结果
        break;
     */
    @Test
    public void transactionMessageTest() throws InterruptedException {
        for(int i=0; i< 5; i++){
            Address serverAddr = clientController.getClusterClient().selectOne();
            if(serverAddr == null){
                LOGGER.error(">>>Send message failed , can not find a server ");
                continue;
            }
            Message message = new Message();
            message.setTopic(Constants.TEVENT_TEST_TOPIC);
            message.setKeys("msg_" + i);
            message.setBody(("TransactionMessageTest " + i).getBytes());
            try {
                client.transMessage(message, serverAddr.getAddress(), 3000);
            } catch (RpcException e) {
                LOGGER.error(">>>Send message '"+message.getKeys()+"' to server "+serverAddr+" failed", e);
                Thread.sleep(5 << 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 集群方式发送消息，
     * 负载均衡，调用失败策略
     */
    @Test
    public void sendMessageTest() throws InterruptedException {
        for(int i=0; i< 10; i++){
            Message message = new Message();
            message.setTopic(Constants.TEVENT_TEST_TOPIC);
            message.setKeys("cluster_msg_" + i);
            message.setBody(("Hello Tarzan " + i).getBytes());
            try {
                client.sendClusterMessage(message, 1, 3000);
            } catch (RpcException e) {
                LOGGER.error(">>>Send message '"+message.getKeys()+"' to server  failed", e);
                Thread.sleep(5 << 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @After
    public void after() throws InterruptedException {
        Thread.sleep(120<<10);
    }


}
