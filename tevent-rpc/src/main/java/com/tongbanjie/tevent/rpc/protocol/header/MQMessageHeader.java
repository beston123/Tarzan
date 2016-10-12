package com.tongbanjie.tevent.rpc.protocol.header;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;

/**
 * MQ消息 协议头 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public class MQMessageHeader implements CustomHeader {

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
