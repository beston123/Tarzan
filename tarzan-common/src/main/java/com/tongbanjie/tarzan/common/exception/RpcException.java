package com.tongbanjie.tarzan.common.exception;

public class RpcException extends TarzanException {

    private static final long serialVersionUID = 4676367489569556104L;

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
