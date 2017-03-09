package com.tongbanjie.tarzan.store.model;

import com.tongbanjie.tarzan.common.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * 〈待事务检查的消息〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/30
 */
public class ToCheckMessage implements Serializable {

    private static final long serialVersionUID = -886742619320076798L;

    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * 主键
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
     * 事务检查次数
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
    @NotNull
    private Date modifyTime;

    /**
     * 备注
     */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "ToCheckMessage{" +
                "id=" + id +
                ", tid=" + tid +
                ", mqType=" + mqType +
                ", sourceTime=" + sourceTime +
                ", retryCount=" + retryCount +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", remark='" + remark + '\'' +
                '}';
    }
}
