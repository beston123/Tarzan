package com.tongbanjie.tevent.store;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public class Result<V> implements Serializable {

    private static final long serialVersionUID = 6781030660269943247L;

    /**
     * 调用是否成功
     */
    private boolean success = false;

    /**
     * 参数或返回结果
     */
    private V data;

    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * 业务错误信息代码
     */
    private String errorCode;

    /**
     * 一般是 e.getMessage();
     */
    private String exceptionMsg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    /**
     * 将 {@link #success} , {@link #errorMsg} , {@link #errorCode},
     * {@link #exceptionMsg} 拼接成String 返回, 方便调用方记录Log.
     *
     * @return
     */
    public String getErrorString() {
        return "ResultErrorString [success=" + success + ", errorCode=" + errorCode + ", errorMsg=" + errorMsg + ", exceptionMsg="
                + exceptionMsg + "]";
    }

    public static <T> Result<T> buildFail(String errorCode) {
        return buildFail(errorCode, null);
    }

    public static <T> Result<T> buildFail(String errorCode, String errorMsg) {
        return buildFail(errorCode, errorMsg, null);
    }

    public static <T> Result<T> buildFail(String errorCode, String errorMsg, String exceptionMsg) {
        Result<T> result = new Result<T>();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMsg(errorMsg);
        result.setExceptionMsg(exceptionMsg);
        return result;
    }

    public static <T> Result<T> buildSucc(T data) {
        Result<T> result = new Result<T>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }


}
