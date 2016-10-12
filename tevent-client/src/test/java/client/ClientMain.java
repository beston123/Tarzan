package client;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.ExampleClient;
import com.tongbanjie.tevent.rpc.exception.RpcException;

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

    public static void main(String[] args)  {
        ExampleClient client = new ExampleClient();

        client.init();

        Random random = new Random();

        for(int i=0; i< 10; i++){
            try {
                Message message = new Message();
                message.setTopic("TOPIC_TEVENT_KE");
                message.setKeys("test_000_" + i);
                message.setBody(("Hello TEvent " + i).getBytes());
                client.sendMessage(message, "Group"+random.nextInt(2), true);
                //Thread.sleep(10<<10);
            } catch (RpcException e) {
                e.printStackTrace();
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

        try {
            client.unregister();
        } catch (RpcException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
