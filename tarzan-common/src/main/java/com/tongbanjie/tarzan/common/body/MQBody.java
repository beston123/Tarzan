package com.tongbanjie.tarzan.common.body;

/**
 * MQ 协议body<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public interface MQBody extends CustomBody {

    String getMessageKey();

    void setMessageKey(String messageKey);

}
