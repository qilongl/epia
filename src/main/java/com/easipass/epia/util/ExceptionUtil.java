package com.easipass.epia.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by lql on 2017/11/6.
 */
public class ExceptionUtil {
    /**
     * 从exception实例中获取异常信息
     *
     * @param e
     * @return
     */
    public static String getErrorInfoFromException(Exception e) {
        if (null == e) return "";
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String str = sw.toString();
            str = str.replaceAll("\r", "&nbsp;&nbsp;&nbsp;&nbsp;");
            str = str.replaceAll("\n", "<br>");
            str = str.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
            return str;
        } catch (Exception e2) {
            return "获取异常信息失败!";
        }
    }

    public static void main(String[] args) {
        int a = 1;
        String b = "0";
        try {
            System.out.println(a / Integer.parseInt(b));
        } catch (Exception ex) {
            String str = getErrorInfoFromException(ex);
            System.out.println(str);
        }
    }
}
