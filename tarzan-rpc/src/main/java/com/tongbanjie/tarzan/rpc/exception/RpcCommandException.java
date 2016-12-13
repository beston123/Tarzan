package com.tongbanjie.tarzan.rpc.exception;

import com.tongbanjie.tarzan.common.exception.RpcException;

/**
 * RPC 命令异常 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RpcCommandException extends RpcException {

    private static final long serialVersionUID = 6043282122067443721L;

    public RpcCommandException(String message){
        super(message);
    }

    public RpcCommandException(String message, Throwable cause){
        super(message, cause);
    }

}
