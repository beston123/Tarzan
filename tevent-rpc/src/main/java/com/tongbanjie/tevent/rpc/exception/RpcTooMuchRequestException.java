package com.tongbanjie.tevent.rpc.exception;

import com.tongbanjie.tevent.common.exception.RpcException;

public class RpcTooMuchRequestException extends RpcException {

    private static final long serialVersionUID = 6216051587080923950L;

    public RpcTooMuchRequestException(String message) {
        super(message);
    }

    public RpcTooMuchRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
