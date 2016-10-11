package com.tongbanjie.tevent.rpc.exception;

/**
 * Created by swy on 2016/9/28.
 */
public class RpcConnectException extends RpcException{

    public RpcConnectException(String message) {
        super(message);
    }

    public RpcConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
