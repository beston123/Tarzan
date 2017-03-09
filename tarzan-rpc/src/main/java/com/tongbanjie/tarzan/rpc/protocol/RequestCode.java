package com.tongbanjie.tarzan.rpc.protocol;

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
     * 健康检查
     */
    public static final int HEALTH_CHECK = 2;

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

    /**
     * 查询消息
     */
    public static final int QUERY_MESSAGE = 7;


    /**
     * 记录消费结果
     */
    public static final int RECORD_CONSUME = 8;

}
