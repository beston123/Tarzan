package com.tongbanjie.tarzan.common.exception;

/**
 * 〈TEvent异常〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class TarzanException extends RuntimeException {

    private static final long serialVersionUID = 2726952917234811023L;

    public TarzanException() {
    }

    public TarzanException(String message) {
        super(message);
    }

    public TarzanException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TarzanException(Throwable throwable) {
        super(throwable);
    }
}
