package com.tongbanjie.tarzan.store.query;

import com.tongbanjie.tarzan.common.PagingQuery;

import java.util.Date;

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

    /**
     * 来源时间 开始[包含]
     * source_time >= sourceTimeFrom
     */
    private Date sourceTimeFrom;

    /**
     * 来源时间 截至[包含]
     * source_time <= sourceTimeTo
     */
    private Date sourceTimeTo;

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

    public Date getSourceTimeFrom() {
        return sourceTimeFrom;
    }

    public void setSourceTimeFrom(Date sourceTimeFrom) {
        this.sourceTimeFrom = sourceTimeFrom;
    }

    public Date getSourceTimeTo() {
        return sourceTimeTo;
    }

    public void setSourceTimeTo(Date sourceTimeTo) {
        this.sourceTimeTo = sourceTimeTo;
    }

    @Override
    public String toString() {
        return "ToSendMessageQuery{" +
                "mqType=" + mqType +
                ", tidFromExclude=" + tidFromExclude +
                ", sourceTimeFrom=" + sourceTimeFrom +
                ", sourceTimeTo=" + sourceTimeTo +
                '}';
    }
}
