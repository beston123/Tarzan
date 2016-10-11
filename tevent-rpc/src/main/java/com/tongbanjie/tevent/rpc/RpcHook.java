package com.tongbanjie.tevent.rpc;

import com.tongbanjie.tevent.rpc.protocol.RpcCommand;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface RpcHook {

    void doBeforeRequest(final String remoteAddr, final RpcCommand request);

    void doAfterResponse(final String remoteAddr, final RpcCommand request,
                         final RpcCommand response);
}
