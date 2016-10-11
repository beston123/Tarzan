package com.tongbanjie.tevent.rpc.exception;

/**
 * Created by swy on 2016/9/28.
 */
public class RpcSendRequestException extends RpcException {

    private static final long serialVersionUID = 5391285827332471674L;


    public RpcSendRequestException(String addr) {
        this(addr, null);
    }


    public RpcSendRequestException(String addr, Throwable cause) {
        super("send request to <" + addr + "> failed", cause);
    }
}
