package rpc;

import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.rpc.protocol.SerializeType;
import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.TransactionMessageHeader;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * RPC 命令测试 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/27
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RpcCommandTest {

    @Test
    public void testSerialize() {
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
