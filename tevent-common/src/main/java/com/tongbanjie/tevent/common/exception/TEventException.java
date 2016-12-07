package com.tongbanjie.tevent.common.exception;

/**
 * 〈TEvent异常〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class TEventException extends RuntimeException {

    private static final long serialVersionUID = 2726952917234811023L;

    public TEventException() {
    }

    public TEventException(String message) {
        super(message);
    }

    public TEventException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TEventException(Throwable throwable) {
        super(throwable);
    }
}
