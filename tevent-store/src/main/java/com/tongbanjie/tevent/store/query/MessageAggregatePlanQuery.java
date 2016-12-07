package com.tongbanjie.tevent.store.query;

import com.tongbanjie.tevent.common.PagingQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈消息汇总计划 查询参数〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/1
 */
public class MessageAggregatePlanQuery extends PagingQuery {

    /**
     * MQ类型
     */
    private Byte mqType;

    /**
     * 汇总类型
     */
    private Byte aggregateType;

    /**
     * 处理状态 列表
     */
    private List<Byte> statusList;

    public Byte getMqType() {
        return mqType;
    }

    public void setMqType(Byte mqType) {
        this.mqType = mqType;
    }

    public Byte getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(Byte aggregateType) {
        this.aggregateType = aggregateType;
    }

    public void addStatus(Byte status) {
        if(statusList == null){
            statusList = new ArrayList<Byte>(8);
        }
        statusList.add(status);
    }

    public List<Byte> getStatusList() {
        return statusList;
    }

}
