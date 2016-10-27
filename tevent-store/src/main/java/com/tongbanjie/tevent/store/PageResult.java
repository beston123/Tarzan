package com.tongbanjie.tevent.store;

import java.io.Serializable;

/**
 * 返回对象 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public class PageResult<V> extends Result{

    /**
     * 总记录数
     */
    private int totalCount = 0;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public static <T> PageResult<T> buildFail(String errorCode) {
        return buildFail(errorCode, null);
    }

    public static <T> PageResult<T> buildFail(String errorCode, String errorMsg) {
        return buildFail(errorCode, errorMsg, null);
    }

    public static <T> PageResult<T> buildFail(String errorCode, String errorMsg, String exceptionMsg) {
        PageResult<T> result = new PageResult<T>();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMsg(errorMsg);
        result.setExceptionMsg(exceptionMsg);
        return result;
    }

    public static <T> PageResult<T> buildSucc(T data) {
        PageResult<T> result = new PageResult<T>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }


}
