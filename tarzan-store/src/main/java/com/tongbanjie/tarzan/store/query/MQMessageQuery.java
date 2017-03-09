package com.tongbanjie.tarzan.store.query;

import com.tongbanjie.tarzan.common.message.SendStatus;
import com.tongbanjie.tarzan.common.message.TransactionState;
import com.tongbanjie.tarzan.common.PagingQuery;
import com.tongbanjie.tarzan.store.model.ToCheckMessage;
import com.tongbanjie.tarzan.store.model.ToSendMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 〈MQ消息 查询参数〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
public class MQMessageQuery extends PagingQuery {

    /**
     * 消息key
     */
    private String messageKey;

    /**
     * 事务状态
     * @see TransactionState
     */
    private Byte transactionState;

    /**
     * 发送状态
     * @see SendStatus
     */
    private List<Byte> sendStatusList;

    /**
     * 创建时间 开始[包含]
     * create_time >= createTimeFromInclude
     */
    private Date createTimeFromInclude;

    /**
     * 创建时间 截至[不包含]
     * create_time < createTimeToExclude
     */
    private Date createTimeToExclude;

    /**
     * 是否被汇总
     * @see ToSendMessage
     * @see ToCheckMessage
     */
    private Boolean hasAggregated;

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public Byte getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(Byte transactionState) {
        this.transactionState = transactionState;
    }

    public void addSendStatus(Byte status) {
        if(sendStatusList == null){
            sendStatusList = new ArrayList<Byte>(8);
        }
        sendStatusList.add(status);
    }

    public List<Byte> getSendStatusList() {
        return sendStatusList;
    }

    public Date getCreateTimeFromInclude() {
        return createTimeFromInclude;
    }

    public void setCreateTimeFromInclude(Date createTimeFromInclude) {
        this.createTimeFromInclude = createTimeFromInclude;
    }

    public Date getCreateTimeToExclude() {
        return createTimeToExclude;
    }

    public void setCreateTimeToExclude(Date createTimeToExclude) {
        this.createTimeToExclude = createTimeToExclude;
    }

    public Boolean getHasAggregated() {
        return hasAggregated;
    }

    public void setHasAggregated(Boolean hasAggregated) {
        this.hasAggregated = hasAggregated;
    }

    @Override
    public String toString() {
        return "MQMessageQuery{" +
                "messageKey='" + messageKey + '\'' +
                ", transactionState=" + transactionState +
                ", sendStatusList=" + sendStatusList +
                ", createTimeFromInclude=" + createTimeFromInclude +
                ", createTimeToExclude=" + createTimeToExclude +
                ", hasAggregated=" + hasAggregated +
                '}';
    }
}
