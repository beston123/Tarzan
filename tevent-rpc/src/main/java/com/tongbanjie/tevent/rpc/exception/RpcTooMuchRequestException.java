package com.tongbanjie.tevent.rpc.exception;


public class RpcTooMuchRequestException extends RpcException {

    public RpcTooMuchRequestException(String message) {
        super(message);
    }

    public RpcTooMuchRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
