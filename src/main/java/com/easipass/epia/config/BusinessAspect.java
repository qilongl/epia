package com.easipass.epia.config;

import com.alibaba.fastjson.JSON;
import com.easipass.epia.util.JsonUtil;
import com.easipass.epia.util.JsonValueFilter;
import com.easipass.epia.util.RequestUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by StrangeDragon on 2019/7/11 16:03
 **/
@Aspect
@Component
public class BusinessAspect {
    private Logger logger = LoggerFactory.getLogger(BusinessAspect.class);

    @Autowired
    HttpServletRequest request;

    @Value("${settings.showRequestHeaderSwitch:0}")
    private String showRequestHeaderSwitch;

    @Pointcut("execution(* com.easipass.epia.controller.*.*(..))")
    public void componentController() {
    }

    @Around("componentController()")
    public Object componentController(ProceedingJoinPoint point) throws Throwable {
        logger.info("============================组件解析开始===================================");
        String requestUri = request.getRequestURI();
        logger.info("--------requestUri:{}", requestUri);
        Object[] objects = point.getArgs();
        requestPreHandle(objects);
        Object result = point.proceed(objects);
        logger.info("--------request[{}]--result:{}", requestUri, JsonUtil.beanToJson(result));
//        return result;
        return JsonUtil.changeNullToString(result);
    }

//    @AfterReturning(returning = "result", pointcut = "componentController()")
//    public Object result(JoinPoint point, Object result) {
//        logger.info("--------result:{}", JsonUtil.beanToJson(result));
//        return result;
//    }

    private void requestPreHandle(Object[] objects) {
        if ("1".equals(showRequestHeaderSwitch)) {
            RequestUtil.showHeaders(request);
        }
        RequestUtil.showParams(request);
        RequestUtil.showBody(request, objects);
    }
}
