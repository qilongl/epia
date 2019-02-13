package com.easipass.epia.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2017/7/31.
 */
public class OutFormater {
    private static Logger logger = LoggerFactory.getLogger(OutFormater.class);

    public static String print(String id, Object result) {
        /**
         * 1.取出所有的列集合
         * 2.对应取出所有的列的值
         */
        if (!logger.isDebugEnabled()) {
            return "over";
        }
        logger.debug("==================操作:" + id + "===================");
        List<Map<String, Object>> list = null;
        if (result instanceof List) {
            list = (List<Map<String, Object>>) result;
            List<String> colNameList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Object object = list.get(i);
                if (!(object instanceof Map)) continue;
                Map<String, Object> map = list.get(i);
                if (i == 0) {
                    StringBuffer colsBuffer = new StringBuffer();
                    Iterator iterator = map.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        colNameList.add(key);
                        colsBuffer.append(key + "\t");
                    }
                    logger.debug(colsBuffer.toString());
                }
                StringBuffer valueBuffer = new StringBuffer();
                for (int j = 0; j < colNameList.size(); j++) {
                    String colName = colNameList.get(j);
                    Object value = map.get(colName);
                    valueBuffer.append(value + "\t");
                }
                logger.debug(valueBuffer.toString());
            }
            logger.debug("==============OVER: 共" + list.size() + "条结果集============");
        } else if (result instanceof Object) {
            logger.debug(JSON.toJSONString(result));
        }

        return "over";
    }

    /**
     * 输出异常栈中的所有信息
     *
     * @param ex
     * @return
     */
    public static String stackTraceToString(Exception ex) {
        StringBuffer exceptionString = new StringBuffer();
//        exceptionString.append(ex.getMessage() + "<br>");
        exceptionString.append(ex + "<br>");
        for (int i = 0; i < ex.getStackTrace().length; i++) {
            StackTraceElement stackTraceElement = ex.getStackTrace()[i];
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            String fileName = stackTraceElement.getFileName();
            int lineNum = stackTraceElement.getLineNumber();
            exceptionString.append("&nbsp;&nbsp;&nbsp;&nbsp;at:" + className);
            exceptionString.append("." + methodName);
            exceptionString.append("(" + fileName);
            exceptionString.append(":" + lineNum + ")<br>");
        }
        return exceptionString.toString();
    }

    public static void main(String args[]) {
        try {
//            int[] a={};
//            int b=a[3];
            throw new UnsupportedOperationException("不支持此格式");
        } catch (Exception ex) {
            System.out.println(stackTraceToString(ex));
        }
    }
}
