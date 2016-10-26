package com.tongbanjie.tevent.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
public class BeanUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(BeanUtils.class);

    public static Map<String, Object> beanToMap(Object entity) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            if("serialVersionUID".equals(fieldName)){
                continue;
            }
            Object o = null;
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getMethodName = "get" + firstLetter + fieldName.substring(1);
            Method getMethod = null;
            try {
                getMethod = entity.getClass().getMethod(getMethodName, new Class[] {});
                o = getMethod.invoke(entity, new Object[] {});
            } catch (Exception e) {
                LOGGER.warn("参数[" + fieldName + "]转换失败!");
            }
            if (o != null) {
                parameter.put(fieldName, o);
            }
        }
        return parameter;
    }
}
