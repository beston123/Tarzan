package com.tongbanjie.tevent.common.body;

/**
 * 心跳数据 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 */
public class HeartbeatData implements CustomBody {

    private static final long serialVersionUID = 2612739800029760734L;

    private String clientId;

    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "HeartbeatData{" +
                "clientId='" + clientId + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
