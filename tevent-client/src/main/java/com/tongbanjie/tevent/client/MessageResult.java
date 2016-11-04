package com.tongbanjie.tevent.client;

import java.io.Serializable;

/**
 * 消息结果 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public class MessageResult implements Serializable {

    private static final long serialVersionUID = 6781030660269943247L;

    private boolean success = false;

    private String msgId;

    private Long transactionId;

    private String errorMsg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static MessageResult buildFail(String errorMsg) {
        return buildFail(null, errorMsg);
    }

    public static MessageResult buildFail(String errorMsg, Exception e){
        return buildFail(null, errorMsg + ", exception:" + e.getMessage());
    }

    public static MessageResult buildFail(Long transactionId, String errorMsg) {
        MessageResult result = new MessageResult();
        result.setSuccess(false);
        result.setTransactionId(transactionId);
        result.setErrorMsg(errorMsg);
        return result;
    }

    public static MessageResult buildSucc(Long transactionId) {
        return buildSucc(transactionId, null);
    }

    public static MessageResult buildSucc(String msgId) {
        return buildSucc(null, msgId);
    }

    public static MessageResult buildSucc(Long transactionId, String msgId) {
        MessageResult result = new MessageResult();
        result.setSuccess(true);
        result.setTransactionId(transactionId);
        result.setMsgId(msgId);
        return result;
    }

}
