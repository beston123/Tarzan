package com.tongbanjie.tarzan.rpc.protocol.header;

import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;

/**
 * 〈tarzan-admin请求 协议头〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/22
 */
public class AdminRequestHeader implements CustomHeader {

    private static final long serialVersionUID = -6065593685105777635L;

    private Long transactionId;

    private MQType mqType;

    @Override
    public void checkFields() throws RpcCommandException {
        if(mqType == null){
            throw new RpcCommandException("mqType can not be null!");
        }
        if(transactionId == null){
            throw new RpcCommandException("transactionId can not be null");
        }
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public MQType getMqType() {
        return mqType;
    }

    public void setMqType(MQType mqType) {
        this.mqType = mqType;
    }

    @Override
    public String toString() {
        return "QueryMessageHeader{" +
                "transactionId=" + transactionId +
                ", mqType=" + mqType +
                '}';
    }
}
