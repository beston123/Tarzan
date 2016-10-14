package com.tongbanjie.tevent.rpc.protocol.header;

import com.tongbanjie.tevent.common.message.MQType;
import com.tongbanjie.tevent.rpc.exception.RpcCommandException;

/**
 * 检查事务状态请求 协议头<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public class CheckTransactionStateHeader implements CustomHeader{

    private MQType mqType;

    private Long transactionId;

    private String messageKey;

    @Override
    public void checkFields() throws RpcCommandException {
        if(transactionId == null){
            throw new RpcCommandException("transactionId can not be null!");
        }
    }

    public MQType getMqType() {
        return mqType;
    }

    public void setMqType(MQType mqType) {
        this.mqType = mqType;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public String toString() {
        return "CheckTransactionStateMessageHeader{" +
                "mqType=" + mqType +
                ", transactionId=" + transactionId +
                ", messageKey='" + messageKey + '\'' +
                '}';
    }
}
