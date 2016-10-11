package com.tongbanjie.tevent.rpc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ClassUtils {

    /*********** 简单数据类型 ************/

    private static final String StringClass = String.class.getCanonicalName();

    private static final String DoubleClass = Double.class.getCanonicalName();
    private static final String doubleClass = double.class.getCanonicalName();

    private static final String LongClass = Long.class.getCanonicalName();
    private static final String longClass = long.class.getCanonicalName();

    private static final String FloatClass = Float.class.getCanonicalName();
    private static final String floatClass = float.class.getCanonicalName();

    private static final String IntegerClass = Integer.class.getCanonicalName();
    private static final String intClass = int.class.getCanonicalName();

    private static final String ShortClass = Short.class.getCanonicalName();
    private static final String shortClass = short.class.getCanonicalName();

    private static final String ByteClass = Byte.class.getCanonicalName();
    private static final String byteClass = byte.class.getCanonicalName();

    private static final String BooleanClass = Boolean.class.getCanonicalName();
    private static final String booleanClass = boolean.class.getCanonicalName();

    private static final String utilDateClass = java.util.Date.class.getCanonicalName();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //简单数据类型set
    private static final Set<String /**/> simpleTypeSet = new HashSet<String>();

    static {
        simpleTypeSet.add(StringClass);
        simpleTypeSet.add(DoubleClass);
        simpleTypeSet.add(doubleClass);
        simpleTypeSet.add(LongClass);
        simpleTypeSet.add(longClass);
        simpleTypeSet.add(FloatClass);
        simpleTypeSet.add(floatClass);
        simpleTypeSet.add(IntegerClass);
        simpleTypeSet.add(intClass);
        simpleTypeSet.add(ShortClass);
        simpleTypeSet.add(shortClass);
        simpleTypeSet.add(ByteClass);
        simpleTypeSet.add(byteClass);
        simpleTypeSet.add(BooleanClass);
        simpleTypeSet.add(booleanClass);
        simpleTypeSet.add(utilDateClass);
    }

    /**
     * 简单数据类型
     * @param type
     * @param value
     * @return
     */
    public static Object parseSimpleValue(String type, String value){
        Object valueParsed = null;
        if (type.equals(StringClass)) {
            valueParsed = value;
        } else if (type.equals(DoubleClass) || type.equals(doubleClass)) {
            valueParsed = Double.parseDouble(value);
        } else if (type.equals(LongClass) || type.equals(longClass)) {
            valueParsed = Long.parseLong(value);
        } else if (type.equals(FloatClass) || type.equals(floatClass)) {
            valueParsed = Float.parseFloat(value);
        } else if (type.equals(IntegerClass) || type.equals(intClass)) {
            valueParsed = Integer.parseInt(value);
        } else if (type.equals(ShortClass) || type.equals(shortClass)) {
            valueParsed = Short.parseShort(value);
        } else if (type.equals(ByteClass) || type.equals(byteClass)) {
            valueParsed = Byte.parseByte(value);
        } else if (type.equals(BooleanClass) || type.equals(booleanClass)) {
            valueParsed = Boolean.parseBoolean(value);
        } else if (type.equals(utilDateClass)) {
            try {
                valueParsed = dateFormat.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return valueParsed;
    }

    /**
     *
     * @param type
     * @param value
     * @return
     */
    public static String simpleValueToString(String type, Object value){
        if(simpleTypeSet.contains(type)){
            if(utilDateClass.equals(type)){
                return dateFormat.format((java.util.Date)value);
            }else{
                return value.toString();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(StringClass);
        System.out.println(IntegerClass);
        System.out.println(intClass);

    }

}
