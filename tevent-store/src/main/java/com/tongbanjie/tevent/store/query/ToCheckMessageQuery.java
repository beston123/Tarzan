package com.tongbanjie.tevent.store.query;

import com.tongbanjie.tevent.common.PagingQuery;

/**
 * 〈待事务检查的消息 查询参数〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/1
 */
public class ToCheckMessageQuery extends PagingQuery {

    /**
     * MQ类型
     */
    private Byte mqType;

    public Byte getMqType() {
        return mqType;
    }

    public void setMqType(Byte mqType) {
        this.mqType = mqType;
    }
}
