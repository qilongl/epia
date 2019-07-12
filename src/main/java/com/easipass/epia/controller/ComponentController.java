package com.easipass.epia.controller;

import com.easipass.epia.service.ComponentService;
import com.easipass.epia.util.ApiResult;
import com.easipass.epia.util.StringHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by lql on 2018/12/26 12:29
 **/
@Api("组件服务")
@RestController
public class ComponentController {

    @Autowired
    ComponentService componentService;

    /**
     * 组件解析（统一入口）
     *
     * @param request
     * @param response
     */
    @ApiOperation("组件解析")
    @RequestMapping(value = "/componentAnalysis", method = {RequestMethod.GET, RequestMethod.POST})
    public Object componentAnalysis(@RequestBody Map map, HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = componentService.componentAnalysis(map, request, response);
        return apiResult;
    }

    /**
     * 重新加载业务配置文件
     */
    @ApiOperation("重新加载业务配置文件")
    @RequestMapping(value = "/reloadBusinessConfiguration", method = {RequestMethod.GET, RequestMethod.POST})
    public Object reloadBusinessConfiguration(@RequestBody Map map) {
        String key = StringHelper.toString(map.get("key"));
        ApiResult apiResult = componentService.reloadBusinessConfiguration(key);
        return apiResult;
    }


}
