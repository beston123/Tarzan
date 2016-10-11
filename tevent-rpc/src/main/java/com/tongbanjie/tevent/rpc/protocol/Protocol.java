package com.tongbanjie.tevent.rpc.protocol;

/**
 * 协议格式 <p>
 * 协议包格式
 * <length> <protocol type> <header length> <header data> <body data>
 *  1 int   1 byte           1 int
 *
 * @author zixiao
 * @date 16/9/28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class Protocol {

    //数据总长度为int 占用4个字节
    public static final int TOTAL_LENGTH_SIZE = 4;

    //协议类型为byte 占用1个字节
    public static final int PROTOCOL_TYPE_SIZE = 1;

    //header长度为int 占用4个字节
    public static final int HEADER_LENGTH_SIZE = 4;

}
