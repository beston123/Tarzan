package com.tongbanjie.tarzan.store.query;

import com.tongbanjie.tarzan.common.PagingQuery;

/**
 * 〈待发送的消息 查询参数〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/1
 */
public class ToSendMessageQuery extends PagingQuery {

    /**
     * MQ类型
     */
    private Byte mqType;

    /**
     * tid大于
     * Where条件： tid > tidFromExclude
     */
    private Long tidFromExclude;

    public Byte getMqType() {
        return mqType;
    }

    public void setMqType(Byte mqType) {
        this.mqType = mqType;
    }

    public Long getTidFromExclude() {
        return tidFromExclude;
    }

    public void setTidFromExclude(Long tidFromExclude) {
        this.tidFromExclude = tidFromExclude;
    }

    @Override
    public String toString() {
        return "ToSendMessageQuery{" +
                "mqType=" + mqType +
                ", tidFromExclude=" + tidFromExclude +
                '}';
    }
}
