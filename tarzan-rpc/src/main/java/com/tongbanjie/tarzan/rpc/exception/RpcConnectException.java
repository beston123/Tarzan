package com.tongbanjie.tarzan.rpc.exception;

import com.tongbanjie.tarzan.common.exception.RpcException;

public class RpcConnectException extends RpcException {

    private static final long serialVersionUID = -6353151567489596604L;

    public RpcConnectException(String message) {
        super(message);
    }

    public RpcConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
