package com.tongbanjie.tevent.common.exception;

public class RpcException extends TEventException {

    private static final long serialVersionUID = 4676367489569556104L;

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
