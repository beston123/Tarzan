package com.tongbanjie.tevent.store.model;

/**
 * 〈汇总类型〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/1
 */
public enum AggregateType {

    TO_CHECK((byte)0),//待检查事务状态
    TO_SEND((byte)1); //待发送消息

    AggregateType(byte code) {
        this.code = code;
    }

    private byte code;

    public byte getCode() {
        return code;
    }

    public static AggregateType valueOf(byte code) {
        for (AggregateType item : AggregateType.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
