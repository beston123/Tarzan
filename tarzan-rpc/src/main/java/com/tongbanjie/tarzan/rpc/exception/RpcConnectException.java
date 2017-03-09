package com.tongbanjie.tarzan.rpc.exception;

import com.tongbanjie.tarzan.common.exception.RpcException;

public class RpcConnectException extends RpcException {

    private static final long serialVersionUID = -6353151567489596604L;

    public static final String ERROR_MSG = "连接失败";

    public RpcConnectException(String message) {
        super(message);
    }

    public RpcConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
