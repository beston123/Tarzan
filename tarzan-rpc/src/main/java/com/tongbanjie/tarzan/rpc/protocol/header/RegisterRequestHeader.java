package com.tongbanjie.tarzan.rpc.protocol.header;

import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;

/**
 * 注册请求 协议头 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RegisterRequestHeader implements CustomHeader {

    private static final long serialVersionUID = -4192931458381984926L;

    private String clientId;

    private String group;

    @Override
    public void checkFields() throws RpcCommandException {
        if(group == null){
            throw new RpcCommandException("group can not be null!");
        }
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
