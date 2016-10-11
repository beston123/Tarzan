package com.tongbanjie.tevent.rpc.protocol.header;

import com.tongbanjie.tevent.rpc.exception.RpcCommandException;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RegisterRequestHeader implements CustomHeader {

    private String clientId;

    private String group;

    @Override
    public void checkFields() throws RpcCommandException {

    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
