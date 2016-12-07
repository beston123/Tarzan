package com.tongbanjie.tevent.common.body;

/**
 * RabbitMQ 协议体 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public class RabbitMQBody implements MQBody {

    private static final long serialVersionUID = -434925588851925781L;

    private String messageKey;

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
