package com.easipass.epia.config;

import com.alibaba.fastjson.JSON;
import com.easipass.epia.db.DBFactory;
import com.easipass.epia.db.DBService;
import com.easipass.epia.util.*;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.easipass.epia.db.DBFactory.createDBService;

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
//        requestPreHandle(objects);
        Map params = showBody(request, objects);
        Object result = point.proceed(objects);
        logger.info("--------request[{}]--result:{}", requestUri, JsonUtil.beanToJson(result));
        xmlVisitLog(params, JsonUtil.beanToJson(result));
        return JsonUtil.changeNullToString(result);
    }

    /**
     * 接口访问记录入库
     *
     * @param params
     * @param data
     */
    private void xmlVisitLog(Map params, String data) {
        DBService dbService = DBFactory.createDBService();
        ApiResult apiResult = JsonUtil.jsonToBean(data, ApiResult.class);
        List<Map<String, Object>> functions = (List<Map<String, Object>>) params.get("Functions");
        Map<String, Object> token = (Map<String, Object>) params.get("token");
        String account = StringHelper.toString(token.get("account"));
        for (Map<String, Object> function : functions) {
            String funUrl = StringHelper.toString(function.get("ModuleName")).concat("-").concat(StringHelper.toString(function.get("FunctionName")));
            List paramsList = new ArrayList();
            paramsList.add(funUrl);
            paramsList.add(account);
            paramsList.add(JsonUtil.beanToJson(params));
            paramsList.add(data);
            paramsList.add(apiResult.getFlag().equals(Constants.FLAG_T) ? "1" : "0");
            String sql = "insert into sys_function_visit(id, objid, fun_url, crt_psn, params, data, flag)\n" +
                    "     values (sys_id.nextval, sys_guid(), ?, ?, ?, ?, ?)";
            dbService.update(sql, paramsList);
        }
        dbService.commit();

    }

//    @AfterReturning(returning = "result", pointcut = "componentController()")
//    public Object result(JoinPoint point, Object result) {
//        logger.info("--------result:{}", JsonUtil.beanToJson(result));
//        return result;
//    }

//    private void requestPreHandle(Object[] objects) {
//        if ("1".equals(showRequestHeaderSwitch)) {
//            RequestUtil.showHeaders(request);
//        }
//        RequestUtil.showParams(request);
//        RequestUtil.showBody(request, objects);
//    }

    public Map showBody(HttpServletRequest request, Object[] objects) {
        for (int j = 0; j < objects.length; j++) {
            if (objects[j] instanceof LinkedHashMap) {//对应requestBody
                Map requestBody = (LinkedHashMap) objects[j];
                String result = JsonUtil.beanToJson(requestBody);
                logger.info("--------RequestBody:" + result);
                request.setAttribute("requestBody", result);
                return requestBody;
            }
        }
        return null;
    }
}
