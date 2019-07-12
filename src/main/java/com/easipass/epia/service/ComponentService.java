package com.easipass.epia.service;

import com.alibaba.fastjson.JSON;
import com.easipass.epia.util.*;
import net.sf.json.JSONArray;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by lql on 2018/12/26 12:33
 **/
@Service
public class ComponentService {
    private static Logger logger = LoggerFactory.getLogger(ComponentService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    XmlBusiService xmlBusiService;

    @Value("${tmp.dir:c:/tmp}")
    private String tmpDir;


    /**
     * 组件解析（统一入口）
     *
     * @param request
     * @param response
     */
    public ApiResult componentAnalysis(Map map, HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        try {
            //处理请求中的附件及调用服务端
            apiResult = callService(map, request);
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
        return apiResult;
    }

    /**
     * 重新加载业务配置文件
     *
     * @param key
     * @return
     */
    public ApiResult reloadBusinessConfiguration(String key) {
        ApiResult apiResult = new ApiResult();
        try {
            xmlBusiService.reloadFunction(key);
            apiResult.setFlag(Constants.FLAG_T);
            apiResult.setData(key + " 重新加载成功！");
        } catch (Exception e) {
            logger.error(getClass().getName(), e.getStackTrace());
            apiResult.setErrorInfo(getClass().getName() + "业务文件" + key + "重载失败!" + OutFormater.stackTraceToString(e));
            apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
        }
        return apiResult;
    }

    private ApiResult callService(Map map, HttpServletRequest request) {
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
            apiResult = service(map, fileMap);
        } catch (Exception ex) {
            ex.printStackTrace();
            apiResult.setFlag(Constants.FLAG_F);
            apiResult.setErrorInfo("调用后台服务出错，请于管理员联系！" + OutFormater.stackTraceToString(ex));
            apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
        }
        //---------------释放缓存------------------------------------------------------
        fileMap = null;
//        jsonParams = null;
        //----------------end---------------------------------------------------------
        return apiResult;
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

    /**
     * =================================================================================================================
     */


    public ApiResult service(Map map, MultiValueMap<String, MultipartFile> fileMap) throws Exception {
        ApiResult apiResult = new ApiResult();
        /**
         * 上传文件过程中产生的临时文件集合
         */
        List<File> tmpFiles = new ArrayList<>();
        /**
         * 附件对应的name属性<file name=""></file>
         */
        List<String> keys = new ArrayList<>();
        /**
         * 原始文件名称
         */
        List<String> fileNames = new ArrayList<>();
        /**
         * 参数载体
         */
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        if (fileMap != null) {
            Iterator iterator = fileMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                LinkedList<MultipartFile> ff = (LinkedList<MultipartFile>) fileMap.get(key);
                for (int i = 0; i < ff.size(); i++) {
                    MultipartFile file = ff.get(i);
                    if (file.getOriginalFilename().equals("")) continue;
                    FileSystemResource fileSystemResource;
                    try {
                        fileSystemResource = changeMultiFileToFSR(file);
                    } catch (Exception e) {
                        apiResult.setErrorInfo("原始文件:" + file.getOriginalFilename() + ",上传错误:" + e.getMessage());
                        apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
                        return apiResult;
                    }
                    param.add("jarFiles", fileSystemResource);
                    keys.add(key);
                    fileNames.add(file.getOriginalFilename());
                    tmpFiles.add(fileSystemResource.getFile());
                }
            }
        }
        apiResult = exec(map, JSON.toJSONString(fileNames), JSON.toJSONString(keys));
//        param.add("fileNames", JSON.toJSONString(fileNames).toString());
//        param.add("keys", JSON.toJSONString(keys).toString());
//        param.add("jsonParams", map);
//        /**
//         * 调用服务端
//         */
//        apiResult = restTemplate.postForObject(url, param, ApiResult.class);
        /**
         * 清理临时文件
         */
        removeTmpFiles(tmpFiles);
        return apiResult;
    }


    /**
     * 执行业务配置文件
     */
    public ApiResult exec(Map map, String fileNames, String keys, MultipartFile... jarFiles) {
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
            apiResult = xmlBusiService.exec(map, filesMap);
        } catch (Exception ex) {
            ex.printStackTrace();
            apiResult.setFlag(Constants.FLAG_F);
            apiResult.setErrorInfo(getClass().getName() + "服务执行异常!" + OutFormater.stackTraceToString(ex));
            apiResult.setErrorCode(Constants.RESULT_STATUS_CODE_ERROR);
        }
        return apiResult;
    }

    /**
     * 清除产生的临时文件
     *
     * @param tmpList
     */
    private void removeTmpFiles(List<File> tmpList) {
        for (int i = 0; i < tmpList.size(); i++) {
            File file = tmpList.get(i);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    /**
     * 转换请求request中的multipartFile 成 FileSystemResource
     *
     * @param multipartFile
     * @return
     */
    private FileSystemResource changeMultiFileToFSR(MultipartFile multipartFile) throws Exception {
        FileSystemResource fileSystemResource;
        File dir = new File(tmpDir);
        if (!dir.exists())
            dir.mkdirs();
        String newFileName = tmpDir + File.separator + StringHelper.getUUID();
        File newFile = new File(newFileName);
        multipartFile.transferTo(newFile);
        fileSystemResource = new FileSystemResource(newFile);
        return fileSystemResource;
    }
}
