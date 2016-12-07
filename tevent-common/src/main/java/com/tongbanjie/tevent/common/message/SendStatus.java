package com.tongbanjie.tevent.common.message;

/**
 * 消息发送状态 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
public enum SendStatus {

    INITIAL((byte) 0),
    SUCCESS((byte) 1),
    FAILED((byte) -1);

    SendStatus(byte code) {
        this.code = code;
    }

    private byte code;

    public byte getCode() {
        return code;
    }

    public static SendStatus valueOf(byte code) {
        for (SendStatus item : SendStatus.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

    }
