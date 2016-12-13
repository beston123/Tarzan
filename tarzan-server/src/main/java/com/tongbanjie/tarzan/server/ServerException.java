package com.tongbanjie.tarzan.server;

import com.tongbanjie.tarzan.common.exception.TarzanException;

/**
 * 服务端异常 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/1
 */
public class ServerException extends TarzanException {

    private static final long serialVersionUID = -4367427925604644287L;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

}