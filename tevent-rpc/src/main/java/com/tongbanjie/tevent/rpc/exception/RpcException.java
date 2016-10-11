package com.tongbanjie.tevent.rpc.exception;

public class RpcException extends Exception {
    private static final long serialVersionUID = -5690687334570505110L;


    public RpcException(String message) {
        super(message);
    }


    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
