package com.tongbanjie.tevent.client;

/**
 * 客户端异常 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/31
 */
public class ClientException extends RuntimeException{

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
