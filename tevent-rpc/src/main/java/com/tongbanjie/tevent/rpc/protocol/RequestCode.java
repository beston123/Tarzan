package com.tongbanjie.tevent.rpc.protocol;

/**
 * 请求码 <p>
 * code > 0
 *
 * @author zixiao
 * @date 16/9/29
 */
public abstract class RequestCode {

    public static final int HEART_BEAT = 1;

    public static final int REGISTER_CLIENT = 2;

    public static final int UNREGISTER_CLIENT = 3;

    public static final int SEND_MSG = 9;
    
    public static final int TRANSACTION_MSG = 10;

}
