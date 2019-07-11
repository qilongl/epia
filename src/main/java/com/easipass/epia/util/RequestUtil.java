package com.easipass.epia.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestUtil {
    private static final Log log = LogFactory.getLog(RequestUtil.class);

    public static void showHeaders(HttpServletRequest request){
        if(null == request.getAttribute("requestHeaders")){
            log.info("--------RequestHeaders:");
            StringBuffer requestHeaders = new StringBuffer();
            Enumeration er = request.getHeaderNames();//获取请求头的所有name值
            while(er.hasMoreElements()){
                String name	=(String) er.nextElement();
                String value = request.getHeader(name);
                log.info(name+"——>"+value);
                requestHeaders.append(name + "——>" + value + "\n");
            }
            log.info("----------------");
            request.setAttribute("requestHeaders" , requestHeaders.toString());
        }
    }

    public static void showParams(HttpServletRequest request) {
        if(null == request.getAttribute("fullUrl")){
            log.info("--------RequestParameters:");
            Enumeration paramNames = request.getParameterNames();
            StringBuffer stringBuffer = new StringBuffer(request.getRequestURI() + "?");
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    String paramValue = paramValues[0];
                    if (paramValue.length() != 0) {
                        log.info(paramName + "——>" + paramValue);
                        try {
                            stringBuffer.append(paramName + "=" + URLEncoder.encode(paramValue , "utf-8") + "&");
                        } catch (UnsupportedEncodingException e) {
                            log.error(e.getMessage() , e);
                        }
                        request.setAttribute( paramName, paramValue);
                    }
                }
            }
            log.info("----------------");
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            log.info("Full URL:" + stringBuffer.toString());
            request.setAttribute("fullUrl" , stringBuffer.toString());
        }
    }

    public static void showBody(HttpServletRequest request , Object[] objects){
        for (int j = 0 ; j < objects.length ; j++) {
            if (objects[j] instanceof LinkedHashMap) {//对应requestBody
                Map requestBody = (LinkedHashMap) objects[j];
                log.info("--------RequestBody:" + JsonUtil.beanToJson(requestBody));
                request.setAttribute("requestBody" , JsonUtil.beanToJson(requestBody));
            }
        }
    }
}
