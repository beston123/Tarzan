package com.tongbanjie.tarzan.rpc.protocol.header;

import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;
import com.tongbanjie.tarzan.common.message.MQType;

/**
 * 发送消息请求 协议头 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public class SendMessageHeader implements CustomHeader {

    private static final long serialVersionUID = -1378348758378944301L;

    protected MQType mqType;

    @Override
    public void checkFields() throws RpcCommandException {
        if(mqType == null){
            throw new RpcCommandException("mqType can not be null!");
        }
    }

    public MQType getMqType() {
        return mqType;
    }

    public void setMqType(MQType mqType) {
        this.mqType = mqType;
    }

    @Override
    public String toString() {
        return "MQMessageHeader{" +
                "mqType=" + mqType +
                '}';
    }
}
