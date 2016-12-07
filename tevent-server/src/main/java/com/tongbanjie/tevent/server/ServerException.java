package com.tongbanjie.tevent.server;

import com.tongbanjie.tevent.common.exception.TEventException;

/**
 * 服务端异常 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public class ServerException extends TEventException{

    private static final long serialVersionUID = -4367427925604644287L;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

}