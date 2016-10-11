package com.tongbanjie.tevent.common.message;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public interface MQMessage extends Serializable{

    void setId(Long id);

    Long getId();
}
