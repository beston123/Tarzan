package com.tongbanjie.tarzan.rpc.protocol.header;

import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;
import com.tongbanjie.tarzan.common.message.MQType;

/**
 * 〈消息查询 协议头〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/22
 */
public class QueryMessageHeader implements CustomHeader {

    private static final long serialVersionUID = 4595652075600784985L;

    private Long transactionId;

    private String messageKey;

    private MQType mqType;

    @Override
    public void checkFields() throws RpcCommandException {
        if(mqType == null){
            throw new RpcCommandException("mqType can not be null!");
        }
        if(transactionId == null && messageKey == null){
            throw new RpcCommandException("transactionId and messageKey can not both be null");
        }
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
                ", messageKey='" + messageKey + '\'' +
                ", mqType=" + mqType +
                '}';
    }
}
