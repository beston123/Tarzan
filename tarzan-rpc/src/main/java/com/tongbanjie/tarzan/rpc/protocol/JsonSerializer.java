package com.tongbanjie.tarzan.rpc.protocol;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

/**
 * Json 序列化工具类 <p>
 *
 *
 * @author zixiao
 * @date 16/9/28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class JsonSerializer {

    public final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private JsonSerializer(){}

    public static byte[] serialize(final Object obj) {
        final String json = toJson(obj, false);
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    public static String toJson(final Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }

    public static <T> T deserialize(final byte[] data, Class<T> classOfT) {
        final String json = new String(data, CHARSET_UTF8);
        return fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

}
