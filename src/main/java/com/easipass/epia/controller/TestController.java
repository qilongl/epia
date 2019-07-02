package com.easipass.epia.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easipass.epia.util.JsonValueFilter;
import com.easipass.epia.util.ResponseResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by StrangeDragon on 2019/6/5 16:10
 **/
@RestController
public class TestController {
    @RequestMapping("test")
    public String test(@RequestParam(value = "param1", required = true) String param1) {
        ResponseResult rr = new ResponseResult();
        rr.setStatusCode(ResponseResult.RESULT_STATUS_CODE_SUCCESS);
        rr.setMsg(param1);
        return JSON.toJSONString(rr, JsonValueFilter.changeNullToString());
    }
}
