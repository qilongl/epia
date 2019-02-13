package com.easipass.epia.util;

import com.easipass.epia.beans.SysProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by lql on 2017/7/14.
 */
public class SystemFunction {
    /**
     * 产生唯一的32位字符串
     */
    public static final String GUID = "guid";

    public static final String DATE = "date";

    public static final String SimpleDate = "simpledate";

    public static final String Millisecond = "millisecond";

    public static final String FullDate = "fulldate";

    /**
     * 系统函数
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        String result = null;
        SimpleDateFormat format = null;
        switch (key) {
            case GUID:
                result = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
                break;
            case DATE:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                result = format.format(Calendar.getInstance().getTime());
                break;
            case SimpleDate:
                format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                result = format.format(Calendar.getInstance().getTime());
                break;
            case Millisecond:
                result = String.valueOf(Calendar.getInstance().getTimeInMillis());
                break;
            case FullDate:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
                result = format.format(Calendar.getInstance().getTime());
                break;
            default:
                result = SysProperties.get(key);
        }
        return result;
    }


    public static void main(String args[]) {
        String result = SystemFunction.get("guid");
        System.out.println(result);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        System.out.println(simple.format(calendar.getTime()));
    }

}
