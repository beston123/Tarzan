package com.tongbanjie.tarzan.common.util;

import java.util.Date;

/**
 * 〈日志工具类〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/10
 */
public class LogUtils {

    private static final String DATA_PATTERN = "MMdd HH:mm:ss SSS";

    private static final int CLASS_NAME_START = "com.tongbanjie.".length();

    public static void stdInfo(String logMsg, Class clazz){
        System.out.println(buildLog(logMsg, clazz, "INFO"));
    }

    public static void stdError(String logMsg, Class clazz, Throwable e){
        System.out.println(buildLog(logMsg, clazz, "ERROR"));
        if(e != null){
            e.printStackTrace();
        }
    }

    private static String buildLog(String logMsg, Class clazz, String logLevel){
        String logTime = DateUtils.format(new Date(), DATA_PATTERN);
        String className = clazz.getCanonicalName().substring(CLASS_NAME_START);
        return String.format("[%s %s] [%s] %s - %s",
                logTime, logLevel, Thread.currentThread().getName(), className, logMsg);
    }

    public static void main(String[] args) {
        LogUtils.stdInfo("123", LogUtils.class);
    }

}
