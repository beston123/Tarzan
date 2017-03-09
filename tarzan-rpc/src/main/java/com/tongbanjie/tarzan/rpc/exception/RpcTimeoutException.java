package com.tongbanjie.tarzan.rpc.exception;

import com.tongbanjie.tarzan.common.exception.RpcException;

public class RpcTimeoutException extends RpcException {

    private static final long serialVersionUID = 386352191236280780L;

    public static final String ERROR_MSG = "发送超时";

    public RpcTimeoutException(String message) {
        super(message);
    }

    public RpcTimeoutException(String address, long timeoutMillis) {
        this(address, timeoutMillis, null);
    }

    public RpcTimeoutException(String address, long timeoutMillis, Throwable cause) {
        super("wait response on the channel <" + address + "> timeout, " + timeoutMillis + "(ms)", cause);
    }
}
