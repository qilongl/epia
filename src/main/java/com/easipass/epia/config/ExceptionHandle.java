package com.easipass.epia.config;

import com.alibaba.fastjson.JSON;
import com.easipass.epia.util.*;
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

    @ExceptionHandler
    public String exceptionHandle(Exception e) {
        logger.error("-------ExceptionHandler{}", ExceptionUtil.getErrorInfoFromException(e));
        ApiResult apiResult = new ApiResult(Constants.RESULT_STATUS_CODE_ERROR, ExceptionUtil.getErrorInfoFromException(e));
        return JsonUtil.beanToJson(apiResult);
    }
}
