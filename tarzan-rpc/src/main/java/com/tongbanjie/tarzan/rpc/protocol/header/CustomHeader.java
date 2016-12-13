package com.tongbanjie.tarzan.rpc.protocol.header;

import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;

import java.io.Serializable;

/**
 * 自定义头 <p>
 * 属性只支持基本类型(byte,short,int,long,double,float,boolean)、字符串、枚举、日期（java.util.Date）
 *
 * @author zixiao
 * @date 16/9/28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface CustomHeader extends Serializable{

    void checkFields() throws RpcCommandException;

}
