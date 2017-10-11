package com.huotu.shopo2o.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by hxh on 2017-09-11.
 */
public class StringUtil {
    public static final String NETSHOP_SECRET = "123456";
    public static String DATE_PATTERN = "yyyy-MM-dd";
    public static String MONTH_PATTERN = "yyyy-MM";
    public static String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String TIME_WITHOUT_SECOND_PATTERN = "yyyy-MM-dd HH:mm";
    public static String DATETIME_PATTERN_WITH_NOSUP = "yyyyMMddHHmmss";
    public static String DATE_PATTERN_WITH_NOSUP = "yyyyMMdd";
    public static String UTF8 = "utf-8";
    /**
     * 更具规则格式化时?
     *
     * @param date
     * @param newPattern
     * @return
     */
    public static String DateFormat(Date date, String newPattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(newPattern);

        return sdf.format(date);
    }

    /**
     * 将null的字符串转换?""
     *
     * @param str
     * @return
     */
    public static String getNullStr(String str) {
        if (str == null)
            return "";

        if (str.equals("null") || str.equals("NULL")) {
            return "";
        } else {
            return str;
        }
    }
    /**
     * 根据字符串格式化成时?
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date DateFormat(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
        }

        return date;
    }

    /**
     * 得到随机字符串
     *
     * @return
     */
    public static String createRandomStr(int digit) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int baseLength = base.length();
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < digit; i++) {
            int number = random.nextInt(baseLength);
            stringBuilder.append(base.charAt(number));
        }
        return stringBuilder.toString();
    }
}
