package com.easipass.epia.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lql on 2017/8/3.
 */
public class DateFormater {
    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String yyyyMMdd(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String yyyyMMddHHmmss(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss:SSS
     *
     * @param date
     * @return
     */
    public static String yyyyMMddHHmmssSSS(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return format.format(date);
    }

    /**
     * 字符串转date类型
     *
     * @param yyyyMMddHHmmss
     * @return
     * @throws Exception
     */
    public static Date yyyyMMddHHmmss2Date(String yyyyMMddHHmmss) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(yyyyMMddHHmmss);
        return date;
    }
    /**
     * 毫秒时间字符串转yyyy-MM-dd HH:mm:ss
     *
     * @param dateString
     * @return
     */
    public static String DateString2yyyyMMddHHmmss(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long timeOflong = Long.valueOf(dateString);
        Date date = new Date(timeOflong);
        return format.format(date);
    }
    public static Date yyyyMMddHHmm2Date(String yyyyMMddHHmm) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = sdf.parse(yyyyMMddHHmm);
        return date;
    }

    public static void main(String args[]) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(Calendar.getInstance().getTime());
        System.out.println(str);
    }
}
