package com.tongbanjie.tevent.rpc.exception;

import com.tongbanjie.tevent.common.exception.RpcException;

public class RpcSendRequestException extends RpcException {
    
    private static final long serialVersionUID = 6424234526891851248L;

    public RpcSendRequestException(String addr) {
        this(addr, null);
    }

    public RpcSendRequestException(String addr, Throwable cause) {
        super("send request to <" + addr + "> failed", cause);
    }
}
