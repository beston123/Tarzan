package com.tongbanjie.tevent.common;

/**
 * 事务状态 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/9
 */
public enum TransactionState {

    PREPARE((byte) 0),
    COMMIT((byte) 1),
    ROLLBACK((byte) 2),
    UNKNOWN((byte) 3);

    TransactionState(byte code) {
        this.code = code;
    }

    private byte code;

    public byte getCode() {
        return code;
    }

    public static TransactionState valueOf(byte code) {
        for (TransactionState item : TransactionState.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
