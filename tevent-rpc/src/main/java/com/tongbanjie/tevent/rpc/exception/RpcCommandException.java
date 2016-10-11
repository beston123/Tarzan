package com.tongbanjie.tevent.rpc.exception;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RpcCommandException extends RpcException {

    public RpcCommandException(String message){
        super(message);
    }

    public RpcCommandException(String message, Throwable cause){
        super(message, cause);
    }

}
