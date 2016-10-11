package com.tongbanjie.tevent.rpc.util;

import com.tongbanjie.tevent.rpc.protocol.RpcCommand;
import com.tongbanjie.tevent.rpc.codec.JsonSerializer;
import com.tongbanjie.tevent.rpc.codec.ProtostuffSerializer;
import com.tongbanjie.tevent.rpc.codec.SerializeType;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RpcSerializeUtils {

    private RpcSerializeUtils(){}

    public static byte[] serialize(RpcCommand rpcCommand){
        if(null == rpcCommand){
            return null;
        }
        return serialize(rpcCommand, rpcCommand.getSerializeType());
    }

    public static byte[] serialize(Object obj, SerializeType serializeType){
        switch (serializeType){
            case JSON:
                return JsonSerializer.serialize(obj);
            case PROTOSTUFF:
                return ProtostuffSerializer.serialize(obj);
            default:
                break;
        }
        return null;
    }

    public static <T> T deserialize(final byte[] data, Class<T> classOfT, SerializeType serializeType){
        switch (serializeType){
            case JSON:
                return JsonSerializer.deserialize(data, classOfT);
            case PROTOSTUFF:
                return ProtostuffSerializer.deserialize(data, classOfT);
            default:
                break;
        }
        return null;
    }

}
