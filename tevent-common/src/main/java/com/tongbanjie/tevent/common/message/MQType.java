package com.tongbanjie.tevent.common.message;

/**
 * MQ 类型<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/30
 */
public enum MQType {

    ROCKET_MQ((byte) 0),
    RABBIT_MQ((byte) 1);

    MQType(byte code) {
        this.code = code;
    }

    private byte code;

    public byte getCode() {
        return code;
    }

    public static MQType valueOf(byte code) {
        for (MQType item : MQType.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
