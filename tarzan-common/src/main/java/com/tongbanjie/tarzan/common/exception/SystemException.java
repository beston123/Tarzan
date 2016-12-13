package com.tongbanjie.tarzan.common.exception;

/**
 * 〈系统异常〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class SystemException extends TarzanException {

    private static final long serialVersionUID = -5018631028214414871L;

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
