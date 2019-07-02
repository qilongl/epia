package com.easipass.epia.controller;

import com.easipass.epia.service.XmlBusiService;
import com.easipass.epia.util.*;
import net.sf.json.JSONArray;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2018/12/26 15:35
 **/
@RestController
@Scope("prototype")//防止单例出现竞争访问
public class ExtendController {
    private static Logger logger = LoggerFactory.getLogger(ExtendController.class);

    @Autowired
    XmlBusiService xmlBusiService;

    /**
     * 服务接口
     */
    @ResponseBody
    @RequestMapping(value = "/extendService", method = RequestMethod.POST)
    public String exec(String jsonParams, String fileNames, String keys, MultipartFile... jarFiles) {
        /**
         * 构建参数,转换附件成字节流
         */
        ApiResult apiResult = new ApiResult();
        Map<String, List<Map<String, byte[]>>> filesMap = new HashedMap();
        try {
            if (StringHelper.isNotNull(fileNames)) {
                JSONArray fileNameJSonArray = JSONArray.fromObject(fileNames);
                JSONArray keysJSONArray = JSONArray.fromObject(keys);
                change2FilesMap(keysJSONArray, filesMap, fileNameJSonArray, jarFiles);
            }
            // 调用服务、执行
            apiResult = xmlBusiService.exec(jsonParams, filesMap);
        } catch (Exception ex) {
            ex.printStackTrace();
            apiResult.setFlag(Constants.FLAG_F);
            apiResult.setErrorInfo(getClass().getName() + "服务执行异常!" + OutFormater.stackTraceToString(ex));
            apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
        }
        /**
         * 响应
         */
        byte[] bytes = new byte[10];
        try {
            bytes = ObjectFormater.toByteArray(apiResult);
        } catch (Exception e) {
            logger.error(getClass().getName(), e.getStackTrace());
        }
        return new BASE64Encoder().encode(bytes);
    }

    /**
     * 导出xml的结果集入excel
     *
     * @param jsonParams
     * @param fileNames
     * @param keys
     * @param systemJsonObject
     * @param jarFiles
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/exportService", method = RequestMethod.POST)
    public String export(String jsonParams, String fileNames, String keys, String systemJsonObject, MultipartFile... jarFiles) {
        /**
         * 构建参数,转换附件成字节流
         */
        ApiResult apiResult = new ApiResult();
        Map<String, List<Map<String, byte[]>>> filesMap = new HashedMap();
        try {
            //附件格式转换
            if (StringHelper.isNotNull(fileNames)) {
                JSONArray fileNameJSonArray = JSONArray.fromObject(fileNames);
                JSONArray keysJSONArray = JSONArray.fromObject(keys);
                change2FilesMap(keysJSONArray, filesMap, fileNameJSonArray, jarFiles);
            }
            // 调用服务、执行
//            rr = xmlBusiService.export(jsonParams, filesMap);
        } catch (Exception ex) {
            ex.printStackTrace();
            apiResult.setFlag(Constants.FLAG_F);
            apiResult.setErrorInfo(getClass().getName() + "服务执行异常!" + OutFormater.stackTraceToString(ex));
            apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
        }
        /**
         * 响应
         */
        byte[] bytes = new byte[10];
        try {
            bytes = ObjectFormater.toByteArray(apiResult);
        } catch (Exception e) {
            logger.error(getClass().getName(), e.getStackTrace());
        }
        return new BASE64Encoder().encode(bytes);
    }

    /**
     * 重新加载业务功能
     *
     * @param key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/extendService/reloadFunction", method = RequestMethod.POST)
    public String reloadFunction(String key) {
        ApiResult apiResult = new ApiResult();
        try {
            xmlBusiService.reloadFunction(key);
            apiResult.setFlag(Constants.FLAG_T);
        } catch (Exception e) {
            logger.error(getClass().getName(), e.getStackTrace());
            apiResult.setErrorInfo(getClass().getName() + "业务文件" + key + "重载失败!" + OutFormater.stackTraceToString(e));
            apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
        }
        /**
         * 响应
         */
        byte[] bytes = new byte[10];
        try {
            bytes = ObjectFormater.toByteArray(apiResult);
        } catch (Exception e) {
            logger.error(getClass().getName(), e.getStackTrace());
        }
        return new BASE64Encoder().encode(bytes);
    }


    /**
     * 把附件转成字节流
     *
     * @param keysJSONArray
     * @param filesMap
     * @param fileNameJSonArray
     * @param jarFiles
     * @throws Exception
     */
    private void change2FilesMap(JSONArray keysJSONArray, Map<String, List<Map<String, byte[]>>> filesMap, JSONArray fileNameJSonArray, MultipartFile... jarFiles) throws Exception {
        if (null == jarFiles)
            return;
        for (int i = 0; i < jarFiles.length; i++) {
            MultipartFile file = jarFiles[i];
            String fileName = fileNameJSonArray.getString(i);
            String key = keysJSONArray.getString(i);
            if (filesMap.containsKey(key)) {
                Map<String, byte[]> map = new HashedMap();
                List<Map<String, byte[]>> fileList = filesMap.get(key);
                map.put(fileName, file.getBytes());
                fileList.add(map);
                filesMap.put(key, fileList);
            } else {
                Map<String, byte[]> map = new HashedMap();
                List<Map<String, byte[]>> fileList = new ArrayList<>();
                map.put(fileName, file.getBytes());
                fileList.add(map);
                filesMap.put(key, fileList);
            }
        }
    }


}
