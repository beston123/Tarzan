package com.tongbanjie.tarzan.rpc.exception;

import com.tongbanjie.tarzan.common.exception.RpcException;

public class RpcTooMuchRequestException extends RpcException {

    private static final long serialVersionUID = 6216051587080923950L;

    public static final String ERROR_MSG = "请求数过多";

    public RpcTooMuchRequestException(String message) {
        super(message);
    }

    public RpcTooMuchRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
