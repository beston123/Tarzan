package com.tongbanjie.tevent.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/19
 */
public class DateUtils {

    /**
     * 锁对象
     */
    private static final Lock lock = new ReentrantLock();


    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String/* pattern */, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> sdf = sdfMap.get(pattern);
        // 双重判断和加锁
        if (sdf == null) {
            try {
                lock.lock();
                sdf = sdfMap.get(pattern);
                if (sdf == null) {
                    // 使用ThreadLocal<SimpleDateFormat>替代 new SimpleDateFormat
                    sdf = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, sdf);
                }
            }finally {
                lock.unlock();
            }
        }
        return sdf.get();
    }

    /**
     * 用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat
     * 这样每个线程只会有一个SimpleDateFormat
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }

}