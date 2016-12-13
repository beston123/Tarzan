package com.tongbanjie.tarzan.rpc.protocol;

/**
 * 响应码<p>
 * code <= 0
 *
 * @author zixiao
 * @date 16/9/29
 */
public abstract class ResponseCode {

    public static final int SUCCESS = 0;

    public static final int SYSTEM_ERROR = -1;

    public static final int SYSTEM_BUSY = -2;

    public static final int INVALID_REQUEST = -3;

    public static final int NOT_EXIST = -4;

}
