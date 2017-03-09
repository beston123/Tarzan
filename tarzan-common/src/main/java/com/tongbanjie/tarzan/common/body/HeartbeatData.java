package com.tongbanjie.tarzan.common.body;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    private List<String> groups = new ArrayList<String>();

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void addGroup(String group) {
        this.groups.add(group);
    }

    @Override
    public String toString() {
        return "HeartbeatData{" +
                "clientId='" + clientId + '\'' +
                ", groups='" + StringUtils.join(groups.iterator(), ",") + '\'' +
                '}';
    }
}
