package com.tongbanjie.tarzan.rpc.protocol;

/**
 * 序列化方式枚举 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public enum SerializeType {

    JSON((byte) 0),
    PROTOSTUFF((byte) 1);

    SerializeType(byte code) {
        this.code = code;
    }

    private byte code;


    public byte getCode() {
        return code;
    }


    public static SerializeType valueOf(byte code) {
        for (SerializeType serializeType : SerializeType.values()) {
            if (serializeType.getCode() == code) {
                return serializeType;
            }
        }
        return null;
    }
}
