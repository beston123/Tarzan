package com.tongbanjie.tarzan.rocketmq;

import com.tongbanjie.tarzan.common.NotNull;
import org.apache.commons.lang3.Validate;

/**
 * RocketMQ参数对象 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
@Deprecated
public class RocketMQParam {

    /**
     * 生产者Group
     */
    @NotNull
    private String groupId;

    /**
     * 消息Topic
     */
    @NotNull
    private String topic;

    public RocketMQParam(){

    }

    public RocketMQParam(String groupId, String topic){
        this.groupId = groupId;
        this.topic = topic;
    }

    public void validate() throws Exception{
        Validate.notBlank(groupId, "The 'groupId' can not be blank");
        Validate.notBlank(groupId, "The 'topic' can not be blank");
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTopic() {
        return topic;
    }

    public RocketMQParam setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public RocketMQParam setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    @Override
    public String toString() {
        return "RocketMQParam{" +
                "groupId='" + groupId + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
