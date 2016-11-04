package com.tongbanjie.tevent.rpc.protocol.header;

import com.tongbanjie.tevent.rpc.exception.RpcCommandException;

/**
 * 消息结果 协议头<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public class MessageResultHeader implements CustomHeader {

    private String msgId;

    private Long transactionId;


    @Override
    public void checkFields() throws RpcCommandException {

    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "MessageResponseHeader{" +
                "msgId='" + msgId + '\'' +
                ", transactionId=" + transactionId +
                '}';
    }
}
