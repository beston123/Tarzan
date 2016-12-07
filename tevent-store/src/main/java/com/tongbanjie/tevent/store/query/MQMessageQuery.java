package com.tongbanjie.tevent.store.query;

import com.tongbanjie.tevent.common.PagingQuery;
import com.tongbanjie.tevent.store.model.ToCheckMessage;
import com.tongbanjie.tevent.store.model.ToSendMessage;

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
     * @see com.tongbanjie.tevent.common.message.TransactionState
     */
    private Byte transactionState;

    /**
     * 发送状态
     * @see com.tongbanjie.tevent.common.message.SendStatus
     */
    private List<Byte> sendStatusList;

    /**
     * 创建时间 起始范围
     */
    private Date createTimeFrom;

    private Date createTimeTo;

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

    public Date getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(Date createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public Date getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(Date createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    public Boolean getHasAggregated() {
        return hasAggregated;
    }

    public void setHasAggregated(Boolean hasAggregated) {
        this.hasAggregated = hasAggregated;
    }



}
