package com.tongbanjie.tarzan.common.exception;

/**
 * 〈参数异常〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class ParameterException extends TarzanException {

    private static final long serialVersionUID = -6859259007046792067L;

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
