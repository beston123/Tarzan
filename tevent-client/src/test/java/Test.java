import com.alibaba.rocketmq.common.protocol.header.EndTransactionRequestHeader;
import com.alibaba.rocketmq.common.protocol.header.EndTransactionResponseHeader;
import com.alibaba.rocketmq.remoting.protocol.RemotingCommand;
import com.alibaba.rocketmq.remoting.protocol.SerializeType;

import java.nio.ByteBuffer;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Test {

    public static void main(String[] args) {
        EndTransactionRequestHeader responseHeader = new EndTransactionRequestHeader();
        responseHeader.setMsgId("433333");
        responseHeader.setTransactionId("TTTTTT");
        RemotingCommand cmd = RemotingCommand.createRequestCommand(1, responseHeader);
        cmd.setSerializeTypeCurrentRPC(SerializeType.JSON);
        cmd.setBody("Test".getBytes());

        ByteBuffer byteBuffer = cmd.encode();
        System.out.print("Total length: " + byteBuffer.getInt());

        System.out.println("-----After decode----");
        ByteBuffer databf = byteBuffer.slice();
        RemotingCommand newCmd = RemotingCommand.decode(databf);

        System.out.print("Body:" + new String(newCmd.getBody())
                + ", cmdCode:" + newCmd.getCode()
                + ", protocolType:" + newCmd.getSerializeTypeCurrentRPC()
                + ", version:" + newCmd.getVersion());
    }
}
