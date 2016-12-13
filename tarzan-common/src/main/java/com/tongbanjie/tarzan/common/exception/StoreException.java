package com.tongbanjie.tarzan.common.exception;

/**
 * 〈存储异常〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class StoreException extends TarzanException {

    private static final long serialVersionUID = 5428269056550849811L;

    public StoreException(String message) {
        super(message);
    }

    public StoreException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
