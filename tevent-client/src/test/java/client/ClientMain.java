package client;

import com.alibaba.rocketmq.common.message.Message;
import com.tongbanjie.tevent.client.ClientConfig;
import com.tongbanjie.tevent.client.ClientController;
import com.tongbanjie.tevent.rpc.exception.RpcException;
import com.tongbanjie.tevent.rpc.netty.NettyClientConfig;

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
        ClientController clientController = startup();
        try {
            clientController.start();
            System.out.println("Client startup success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //test();
    }

    public static ClientController startup(){
        ClientController clientController = new ClientController(new ClientConfig(), new NettyClientConfig());
        clientController.initialize();
        return clientController;
    }


}
