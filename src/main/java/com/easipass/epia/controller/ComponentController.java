package com.easipass.epia.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.easipass.epia.service.ComponentService;
import com.easipass.epia.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2018/12/26 12:29
 **/
@Api("组件接口管理")
@RestController
public class ComponentController {
    private static Logger logger = LoggerFactory.getLogger(ComponentController.class);

    @Autowired
    ComponentService componentService;

//    @Autowired
//    TokenCloudService tokenCloudService;

    @Value("${server.port}")
    String port;

    @Value("${server.context-path}")
    String contextPath;


    /**
     * 处理json请求
     *
     * @param jsonParams
     * @param request
     * @param response
     */
    @ApiOperation("Json请求处理")
    @RequestMapping(value = "/componentService", method = {RequestMethod.GET, RequestMethod.POST})
    public String jsonParamsRquest(@RequestParam(value = "jsonParams", required = true) String jsonParams,
                                   HttpServletRequest request, HttpServletResponse response) {
//        ResponseResult rr = new ResponseResult();
        ApiResult apiResult = new ApiResult();
        String url = getRestUrl("extendService");
//        ResponseResult rr_token = tokenCloudService.checkTokenInfoByAccount("", jsonParams);
//        if (rr_token.getStatusCode().equals(ResponseResult.RESULT_STATUS_CODE_SUCCESS)) {
//            jsonParams = rr_token.getResult().toString();
        return exec(jsonParams, apiResult, request, response, url);
//        } else {
//            writerResult(JSON.toJSONString(rr_token, JsonValueFilter.changeNullToString()), response);
//        }
    }

    /**
     * 处理url请求,只适用于 get
     *
     * @param request
     * @param response
     */
    @ApiOperation("Url请求处理")
    @RequestMapping(value = "/easyComponentService", method = {RequestMethod.GET})
    public String urlRquest(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        ApiResult apiResult = new ApiResult();
        String paramsUrl = request.getQueryString();
        String jsonParams = null;
        String url = getRestUrl("extendService");
//            ResponseResult rr_token = tokenCloudService.checkTokenInfoByAccount(decodeUrl, null);
//            if (rr_token.getStatusCode().equals(ResponseResult.RESULT_STATUS_CODE_SUCCESS)) {
//                jsonParams = JSON.toJSONString(parseUrl2JsonObj(rr_token.getResult().toString()));
        jsonParams = JSON.toJSONString(parseUrl2JsonObj(paramsUrl));
        return exec(jsonParams, apiResult, request, response, url);

    }

    /**
     * 重新加载业务文件
     */
    @ApiOperation("重新加载业务文件")
    @RequestMapping(value = "/componentService/reloadFunction", method = {RequestMethod.GET, RequestMethod.POST})
    public String reloadFunction(@RequestParam(value = "jsonParams", required = true) String jsonParams) {
        ApiResult apiResult = new ApiResult();
        JSONObject jsonObject = JSONObject.parseObject(jsonParams);
        String key = jsonObject.getString("key");
        String url = getRestUrl("extendService/reloadFunction");
        try {
            apiResult = componentService.reloadFunction(url, key);
        } catch (Exception ex) {
            apiResult.setFlag(Constants.FLAG_F);
            apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
            apiResult.setErrorInfo("重新加载" + key + "失败,调用加载接口异常！" + ex.getMessage());
        }
        return JSON.toJSONString(apiResult, JsonValueFilter.changeNullToString());
    }

    /**
     * 把url转成json对象
     *
     * @param url
     * @return
     */
    public JSONObject parseUrl2JsonObj(String url) {
        String[] params = url.split("&&");
        JSONObject jsonObject = new JSONObject();
        JSONArray functions = new JSONArray();
        JSONObject oneObj = new JSONObject();
        JSONObject parameters = new JSONObject();
        for (int i = 0; i < params.length; i++) {
            String strs = params[i];
            String[] parms = strs.split("&");
            for (int j = 0; j < parms.length; j++) {
                String str = parms[j];
                if (str.indexOf("=") == -1)
                    throw new UnsupportedOperationException(str + "参数格式错误,缺少'='无法解析出属性的值!");
                String[] ps = str.split("=");
                String name = ps[0];
                String attrValue;
                if (ps.length == 1)
                    attrValue = "";
                else
                    attrValue = ps[1];
                if (name.equalsIgnoreCase("functionname")) {
                    String moduleName = attrValue.substring(0, attrValue.lastIndexOf("-"));
                    String functionName = attrValue.substring(attrValue.lastIndexOf("-") + 1, attrValue.length());
                    oneObj.put("ModuleName", moduleName);
                    oneObj.put("FunctionName", functionName);
                } else {
                    if (name.indexOf("-") == -1)
                        throw new UnsupportedOperationException("参数" + name + "格式不正确,缺少'-'无法解析出属性!");
                    String objName = name.split("-")[0];
                    String attrName = name.split("-")[1];
                    if (parameters.containsKey(objName)) {
                        JSONObject obj = parameters.getJSONObject(objName);
                        obj.put(attrName, attrValue);
                    } else {
                        JSONObject obj = new JSONObject();
                        obj.put(attrName, attrValue);
                        parameters.put(objName, obj);
                    }
                }
            }
        }
        oneObj.put("Parameters", parameters);
        functions.add(oneObj);
        jsonObject.put("Functions", functions);
        return jsonObject;
    }

    /**
     * 请求处理
     *
     * @param jsonParams
     * @param request
     * @param response
     */
//    public void exec(String jsonParams, ApiResult apiResult, HttpServletRequest request, HttpServletResponse response, String url) {
    public String exec(String jsonParams, ApiResult apiResult, HttpServletRequest request, HttpServletResponse response, String url) {
        try {
            //处理请求中的附件及调用服务端
            apiResult = callService(jsonParams, request, url);
            //检测是否有附件下载
            int fileCode = writeFiles(apiResult, response);
//            if (fileCode == 1)
//                return;
        } catch (Exception ex) {
            ex.printStackTrace();
            apiResult.setFlag(Constants.FLAG_F);
            apiResult.setErrorInfo("调用服务出错!" + OutFormater.stackTraceToString(ex));
            apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
        }
//        String s = JSON.toJSONString(apiResult, JsonValueFilter.changeNullToString());
//        JSONObject jsonObject = JSON.parseObject(s);
//        String resStr = JSON.toJSONStringWithDateFormat(jsonObject, "yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);
//        writerResult(resStr, response);
        return JSON.toJSONString(apiResult, JsonValueFilter.changeNullToString());
    }


    /**
     * 响应文件流
     *
     * @param response
     * @return
     * @throws Exception
     */
    private int writeFiles(ApiResult apiResult, HttpServletResponse response) throws Exception {
        if (Constants.FLAG_T.equals(apiResult.getFlag())) {
            Map<String, Map<String, List<Map<String, Object>>>> result = (Map<String, Map<String, List<Map<String, Object>>>>) apiResult.getData();
            Iterator iterator = result.keySet().iterator();
            while (iterator.hasNext()) {
                //取出单个函数的结果集
                String key = iterator.next().toString();
                Map<String, List<Map<String, Object>>> resultMap = result.get(key);
                Iterator resultIterator = resultMap.keySet().iterator();
                while (resultIterator.hasNext()) {
                    // 取出操作的结果集
                    String resultKey = resultIterator.next().toString();
                    Object resultsObject = resultMap.get(resultKey);
                    // 遍历结果集
                    if (!TypeUtil.isListMap(resultsObject))
                        continue;
                    List<Map<String, Object>> results = (List<Map<String, Object>>) resultsObject;
                    for (int i = 0; i < results.size(); i++) {
                        Map mm = results.get(i);
                        Iterator keyIterator = mm.keySet().iterator();
                        while (keyIterator.hasNext()) {
                            String fileName = keyIterator.next().toString();
                            // 属性中包含附件下载的标签@download,则下载内容
                            if (fileName.endsWith("@download")) {
                                byte[] bytes = new BASE64Decoder().decodeBuffer(mm.get(fileName).toString());
                                fileName = fileName.substring(0, fileName.lastIndexOf("@download"));
                                OutputStream out = response.getOutputStream();
                                response.setCharacterEncoding("utf-8");
                                response.setContentType("application/octet-stream");
                                response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
                                out.write(bytes);
                                out.flush();
                                out.close();
                                return 1;
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 返回字符串结果集
     *
     * @param str
     * @param response
     */
    private void writerResult(String str, HttpServletResponse response) {
        try {
            response.setCharacterEncoding("UTF-8");
            Writer writer = response.getWriter();
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("返回流异常:" + OutFormater.stackTraceToString(ex));
        }
    }

    private ApiResult callService(String jsonParams, HttpServletRequest request, String url) {
        /**
         * 读取请求中的附件，转成字节流
         */
        ApiResult apiResult = new ApiResult();
        MultiValueMap<String, MultipartFile> fileMap = null;
        //创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //判断 request 是否有文件上传,即多部分请求...(实则判断request中的Content-Type 是否以multi开头)
        if (multipartResolver.isMultipart(request)) {
            fileMap = ((MultipartHttpServletRequest) request).getMultiFileMap();
        }
        // 调用服务端
        try {
            apiResult = componentService.service(jsonParams, fileMap, url);
        } catch (Exception ex) {
            ex.printStackTrace();
            apiResult.setFlag(Constants.FLAG_F);
            apiResult.setErrorInfo("调用后台服务出错，请于管理员联系！" + OutFormater.stackTraceToString(ex));
            apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
        }
        //---------------释放缓存------------------------------------------------------
        fileMap = null;
        jsonParams = null;
        //----------------end---------------------------------------------------------
        return apiResult;
    }

    private String getRestUrl(String serverName) {
        return "http://localhost:" + port + contextPath + serverName;
    }
}
