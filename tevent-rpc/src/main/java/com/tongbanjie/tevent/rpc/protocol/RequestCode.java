package com.tongbanjie.tevent.rpc.protocol;

/**
 * 请求码 <p>
 * code > 0
 *
 * @author zixiao
 * @date 16/9/29
 */
public abstract class RequestCode {

    /**
     * 心跳
     */
    public static final int HEART_BEAT = 1;

    /**
     * 注册客户端
     */
    public static final int REGISTER_CLIENT = 2;

    /**
     * 反注册客户端
     */
    public static final int UNREGISTER_CLIENT = 3;

    /**
     * 发送消息
     */
    public static final int SEND_MESSAGE = 4;

    /**
     * 事务消息
     */
    public static final int TRANSACTION_MESSAGE = 5;

    /**
     * 服务端检查事务状态
     */
    public static final int CHECK_TRANSACTION_STATE = 6;


}
