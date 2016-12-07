package com.tongbanjie.tevent.store.model;

import com.sun.istack.internal.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * 〈消息处理批次〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/30
 */
public class MessageAggregatePlan implements Serializable {

    private static final long serialVersionUID = -6217856998573935213L;

    /**
     * 主键
     */
    @NotNull
    private Integer id;

    /**
     * 开始时间点
     */
    @NotNull
    private Date timeStart;

    /**
     * 截至时间点
     */
    @NotNull
    private Date timeEnd;

    /**
     * MQ类型
     */
    @NotNull
    private Byte mqType;

    /**
     * 汇总类型
     * @see AggregateType
     */
    @NotNull
    private Byte aggregateType;

    /**
     * 处理状态
     * @see AggregateStatus
     */
    @NotNull
    private Byte status;

    /**
     * 处理记录数
     */
    private Integer recordCount;

    /**
     * 执行时间ms
     */
    private Long elapsedTime;

    /**
     * 创建时间
     */
    @NotNull
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 备注
     */
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
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

    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}
