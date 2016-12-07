package com.tongbanjie.tevent.common.exception;

/**
 * 〈业务异常〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class BusinessException extends TEventException{

    private static final long serialVersionUID = 4188232070213821571L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
