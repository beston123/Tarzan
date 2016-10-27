package con.tongbanjie.tevent.mq.rocketmq;

import org.apache.commons.lang3.Validate;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/27
 */
public class RocketMQParam {

    private String groupId;

    private String name;

    private String topic;

    private String tag;

    private String namesrvAddr;

    public RocketMQParam(){

    }

    public RocketMQParam(String groupId, String name, String topic, String tag, String namesrvAddr){
        this.groupId = groupId;
        this.name = name;
        this.topic = topic;
        this.tag = tag;
        this.namesrvAddr = namesrvAddr;
    }

    public void validate() throws Exception{
        Validate.notBlank(groupId, "The 'groupId' can not be blank");
        Validate.notBlank(name, "The 'name' can not be blank");
        Validate.notBlank(groupId, "The 'topic' can not be blank");
        Validate.notBlank(namesrvAddr, "The 'namesrvAddr' can not be blank");
    }

    public String getGroupId() {
        return groupId;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public String getTopic() {
        return topic;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public RocketMQParam setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public RocketMQParam setName(String name) {
        this.name = name;
        return this;
    }

    public RocketMQParam setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public RocketMQParam setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public RocketMQParam setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
        return this;
    }

    @Override
    public String toString() {
        return "RocketMQParam{" +
                "groupId='" + groupId + '\'' +
                ", name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", tag='" + tag + '\'' +
                ", namesrvAddr='" + namesrvAddr + '\'' +
                '}';
    }
}
