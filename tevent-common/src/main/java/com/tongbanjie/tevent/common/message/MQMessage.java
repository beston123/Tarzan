package com.tongbanjie.tevent.common.message;

import java.io.Serializable;

/**
 * 消息存储Model <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public interface MQMessage extends Serializable{

    void setId(Long id);

    Long getId();
}
