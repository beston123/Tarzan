package com.tongbanjie.tarzan.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * 时间工具类 <p>
 * 解决SimpleDateFormat非线程安全问题：
 * 用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat
 * 这样每个线程只会有一个SimpleDateFormat
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
     * 万能时间格式
     * yyyy-MM-dd-HH-mm-ss
     * yyyyMMddHHmmss
     * EEE MMM dd HH:mm:ss Z yyyy
     * yyyyMMdd
     */
    private static final List<DateFormatRegex> dateFormatRegexList = new ArrayList<DateFormatRegex>();

    private static final String REGEX_yyyy_MM_dd_HH_mm_ss = "^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$";

    private static final String REGEX_yyyyMMddHHmmss = "^\\d{14}$";

    private static final String REGEX_EEE_MMM_dd_HH_mm_ss_Z_yyyy = "^[A-Z]{1}[a-z]{2}\\s[A-Z]{1}[a-z]{2}\\s\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s[A-Z]{3}\\s\\d{4}$";

    private static final String REGEX_yyyyMMdd = "^\\d{8}$";

    static {
        dateFormatRegexList.add(new DateFormatRegex("yyyy-MM-dd-HH-mm-ss", REGEX_yyyy_MM_dd_HH_mm_ss));
        dateFormatRegexList.add(new DateFormatRegex("yyyyMMddHHmmss", REGEX_yyyyMMddHHmmss));
        dateFormatRegexList.add(new DateFormatRegex("EEE MMM dd HH:mm:ss Z yyyy", REGEX_EEE_MMM_dd_HH_mm_ss_Z_yyyy, Locale.US));
        dateFormatRegexList.add(new DateFormatRegex("yyyyMMdd", REGEX_yyyyMMdd));
    }

    private static class DateFormatRegex{

        public String format;
        public Pattern regexPattern;
        public Locale locale;

        public  DateFormatRegex(String format, String regex){
            this(format, regex, null);
        }

        public  DateFormatRegex(String format, String regex, Locale locale){
            this.format = format;
            this.regexPattern = Pattern.compile(regex);
            this.locale = locale;
        }
    }

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern){
        return getSdf(pattern, null);
    }

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @param locale
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern, final Locale locale) {
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
                            if(locale == null){
                                return new SimpleDateFormat(pattern);
                            }else{
                                return new SimpleDateFormat(pattern, locale);
                            }
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
     * Date to String
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    /**
     * String to Date
     *
     * @param dateStr
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }

    /**
     * String to Date
     *
     * @param dateStr
     * @param pattern
     * @param locale
     * @return
     * @throws ParseException
     */
    public static Date parse(String dateStr, String pattern, Locale locale) throws ParseException {
        return getSdf(pattern, locale).parse(dateStr);
    }

    /**
     * 尝试解析时间字符串
     * 可以识别常见时间格式的字符串:
     * 2014年3月12日 13时5分34秒，2014-03-12 12:05:34，2014/3/12 12:5:34，2014 03 12 12 05 34
     * 20140312120534
     * Wed Mar 12 13:05:34 CST 2014
     * 20140312
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date tryParse(String dateStr) throws ParseException {
        String copy = dateStr;
        for(DateFormatRegex dfr: dateFormatRegexList){
            if (dfr.regexPattern.matcher(dateStr).matches()) {
                if(dfr.regexPattern.pattern().equals(REGEX_yyyy_MM_dd_HH_mm_ss)){
                    copy = copy.replaceAll("\\D+", "-");
                }
                if(dfr.locale != null){
                    return parse(copy, dfr.format, dfr.locale);
                }
                return parse(copy, dfr.format);
            }
        }
        throw new ParseException("Unsupported date format, '" + dateStr + "'", 0);
    }

    public static void main(String[] args) throws ParseException {
        dateParseTest("20140312120534");

        dateParseTest("2014年3月12日 13时5分34秒");
        dateParseTest("2014-03-12 12:05:34");
        dateParseTest("2014/3/12 12:5:34");
        dateParseTest("2014 03 12 12 05 34");

        dateParseTest("Wed Mar 12 13:05:34 CST 2014");

        dateParseTest("20140312");
    }

    private static void dateParseTest(String dateStr) throws ParseException {
        System.out.println(dateStr + " >>>>: "+ tryParse(dateStr));
    }

}