package com.tongbanjie.tarzan.store.query;

import com.tongbanjie.tarzan.common.PagingQuery;

import java.util.ArrayList;
import java.util.Date;
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
     * 处理状态
     */
    private Byte status;

    /**
     * 处理状态 列表
     */
    private List<Byte> statusList;

    /**
     * 开始时间点From
     */
    private Date timeStartFrom;

    /**
     * 开始时间点To
     */
    private Date timeStartTo;

    public MessageAggregatePlanQuery(){
        this.setOrderByClause(ORDER_BY_ID_ASC);
    }

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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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

    public Date getTimeStartFrom() {
        return timeStartFrom;
    }

    public void setTimeStartFrom(Date timeStartFrom) {
        this.timeStartFrom = timeStartFrom;
    }

    public Date getTimeStartTo() {
        return timeStartTo;
    }

    public void setTimeStartTo(Date timeStartTo) {
        this.timeStartTo = timeStartTo;
    }

    @Override
    public String toString() {
        return "MessageAggregatePlanQuery{" +
                "mqType=" + mqType +
                ", aggregateType=" + aggregateType +
                ", statusList=" + statusList +
                ", timeStartFrom=" + timeStartFrom +
                ", timeStartTo=" + timeStartTo +
                '}';
    }

}
