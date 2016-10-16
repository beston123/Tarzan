package client;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.ClientConfig;
import com.tongbanjie.tevent.client.ClientController;
import com.tongbanjie.tevent.common.Constants;
import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.rpc.exception.RpcException;
import com.tongbanjie.tevent.rpc.netty.NettyClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ClientMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientMain.class);


    public static void main(String[] args)  {
        ClientController clientController = startup();
        try {
            clientController.start();
            System.out.println("Client startup success!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }

        sendMessageTest(clientController);

    }

    public static ClientController startup(){
        ClientController clientController = new ClientController(new ClientConfig(), new NettyClientConfig());
        clientController.initialize();
        return clientController;
    }

    public static void sendMessageTest(final ClientController clientController){
        ExampleClient client = new ExampleClient(clientController, Constants.TEVENT_TEST_P_GROUP);

        Random random = new Random();

        for(int i=0; i< 5; i++){
            Address serverAddr = clientController.getServerManager().discover();
            if(serverAddr == null){
                LOGGER.error(">>>Send message failed , can not find a server ");
                continue;
            }
            Message message = new Message();
            message.setTopic(Constants.TEVENT_TEST_TOPIC);
            message.setKeys("test_000_" + i);
            message.setBody(("Hello TEvent " + i).getBytes());
            try {
                client.sendMessage( message, serverAddr, Constants.TEVENT_TEST_P_GROUP, true);
                //Thread.sleep(10<<10);
            } catch (RpcException e) {
                LOGGER.error(">>>Send message '"+message.getKeys()+"' to server "+serverAddr+" failed", e);
                try {
                    Thread.sleep(5<<10);
                } catch (InterruptedException e1) {
                    //
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(5000<<10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



}
