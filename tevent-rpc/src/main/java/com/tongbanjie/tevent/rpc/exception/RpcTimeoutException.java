package com.tongbanjie.tevent.rpc.exception;

import com.tongbanjie.tevent.common.exception.RpcException;

public class RpcTimeoutException extends RpcException {

    private static final long serialVersionUID = 386352191236280780L;

    public RpcTimeoutException(String message) {
        super(message);
    }

    public RpcTimeoutException(String addr, long timeoutMillis) {
        this(addr, timeoutMillis, null);
    }

    public RpcTimeoutException(String addr, long timeoutMillis, Throwable cause) {
        super("wait response on the channel <" + addr + "> timeout, " + timeoutMillis + "(ms)", cause);
    }
}
