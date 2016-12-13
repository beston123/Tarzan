package com.tongbanjie.tarzan.store.model;

import com.tongbanjie.tarzan.common.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * 〈待发送的消息〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/30
 */
public class ToSendMessage implements Serializable {

    private static final long serialVersionUID = 2000494453797571292L;

    /**
     * tid
     */
    @NotNull
    private Long tid;

    /**
     * MQ类型
     */
    @NotNull
    private Byte mqType;

    /**
     * 消息来源时间
     */
    @NotNull
    private Date sourceTime;

    /**
     * 发送次数
     */
    @NotNull
    private Short retryCount;

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

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public Byte getMqType() {
        return mqType;
    }

    public void setMqType(Byte mqType) {
        this.mqType = mqType;
    }

    public Date getSourceTime() {
        return sourceTime;
    }

    public void setSourceTime(Date sourceTime) {
        this.sourceTime = sourceTime;
    }

    public Short getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Short retryCount) {
        this.retryCount = retryCount;
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