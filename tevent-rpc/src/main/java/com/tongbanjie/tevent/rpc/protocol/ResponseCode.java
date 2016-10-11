package com.tongbanjie.tevent.rpc.protocol;

/**
 * 响应码<p>
 * code <= 0
 *
 * @author zixiao
 * @date 16/9/29
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class ResponseCode {

    public static final int SUCCESS = 0;

    public static final int SYSTEM_ERROR = -1;

    public static final int SYSTEM_BUSY = -2;

    public static final int INVALID_REQUEST = -3;

    public static final int NOT_EXIST = -4;

}
