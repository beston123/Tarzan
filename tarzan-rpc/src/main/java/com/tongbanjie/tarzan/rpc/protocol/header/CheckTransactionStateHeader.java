package com.tongbanjie.tarzan.rpc.protocol.header;

import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;
import com.tongbanjie.tarzan.common.message.MQType;

/**
 * 检查事务状态请求 协议头<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public class CheckTransactionStateHeader implements CustomHeader{

    private static final long serialVersionUID = 2242489515056994149L;

    private MQType mqType;

    private Long transactionId;

    private String messageKey;

    @Override
    public void checkFields() throws RpcCommandException {
        if(mqType == null){
            throw new RpcCommandException("mqType can not be null!");
        }
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
