package com.easipass.epia.config;

import com.alibaba.fastjson.JSON;
import com.easipass.epia.util.ExceptionUtil;
import com.easipass.epia.util.JsonValueFilter;
import com.easipass.epia.util.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created by lql on 2019/1/23 15:22
 **/
@RestControllerAdvice
public class ExceptionHandle {
    private static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);
    ResponseResult rr = new ResponseResult();

    @ExceptionHandler
    public String exceptionHandle(Exception e) {
        logger.debug(ExceptionUtil.getErrorInfoFromException(e));
        rr.setStatusCode(ResponseResult.RESULT_STATUS_CODE_ERROR);
        rr.setMsg(ExceptionUtil.getErrorInfoFromException(e));
        return JSON.toJSONString(rr, JsonValueFilter.changeNullToString());
    }
}
