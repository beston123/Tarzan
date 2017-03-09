package com.tongbanjie.tarzan.rpc.exception;

import com.tongbanjie.tarzan.common.exception.RpcException;

public class RpcSendRequestException extends RpcException {
    
    private static final long serialVersionUID = 6424234526891851248L;

    public static final String ERROR_MSG = "发送请求失败";

    public RpcSendRequestException(String address) {
        this(address, null);
    }

    public RpcSendRequestException(String address, Throwable cause) {
        super("send request to <" + address + "> failed", cause);
    }
}
