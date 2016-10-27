package test.tevent.rpc;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.rpc.protocol.SerializeType;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;
import com.tongbanjie.tevent.rpc.protocol.RequestCode;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tevent.rpc.protocol.header.TransactionMessageHeader;

import java.nio.ByteBuffer;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/27
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RpcCommandTest {

    public static void main(String[] args) {
        testSerialize();
    }

    private static void testSerialize() {
        byte[] bytes = "紫箫3".getBytes();

        TransactionMessageHeader customHeader = new TransactionMessageHeader();
        customHeader.setTransactionId(1222233L);
        customHeader.setMqType(MQType.ROCKET_MQ);
        RpcCommand cmd = RpcCommandBuilder.buildRequest(RequestCode.HEART_BEAT, customHeader);
        cmd.setBody(bytes);
        cmd.setSerializeType(SerializeType.JSON);
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = cmd.encode();
        } catch (RpcCommandException e) {
            System.out.println("Encode error "+ e.getMessage());
        }
        System.out.print("Total length: " + byteBuffer.getInt());


        System.out.println("-----After decode----");
        ByteBuffer databf = byteBuffer.slice();
        RpcCommand newCmd = RpcCommand.decode(databf);

        System.out.print("Body:" + new String(newCmd.getBody())
                + ", cmdCode:" + newCmd.getCmdCode()
                + ", protocolType:" + newCmd.getSerializeType()
                + ", version:" + newCmd.getVersion());

        try {
            TransactionMessageHeader header = (TransactionMessageHeader)newCmd.decodeCustomHeader(TransactionMessageHeader.class);
            System.out.println(", custom header:"+ header );
        } catch (RpcCommandException e) {
            e.printStackTrace();
        }

    }

    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

}
