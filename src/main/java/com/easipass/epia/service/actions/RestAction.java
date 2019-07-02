package com.easipass.epia.service.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.service.XmlBusiConfigContainer;
import com.easipass.epia.util.StringHelper;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by lql on 2019/1/4 15:07
 **/
public class RestAction implements Serializable {
    public static final String URL = "url";
    public static final String METHOD = "method";
    public static final String VALUE = "value";
    public static final String GET = "GET";
    public static final String POST = "POST";
    private XmlBusiConfig xmlBusiConfig;

    public RestAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }

    /**
     * 发送rest请求
     *
     * @param cmd
     * @return
     * @throws Exception
     */
    public String restCommand(Action cmd) throws Exception {
        String rs;
        try {
            Document document = DocumentHelper.parseText(cmd.getContent());
            Element rootElement = document.getRootElement();
            String urlName = rootElement.attributeValue(URL);
            String methodName = rootElement.attributeValue(METHOD);
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            List<Element> list = rootElement.elements();
            for (int i = 0; i < list.size(); i++) {
                Element element = list.get(i);
                String inParamName = element.getName();//入参名称
                String valueName = element.attributeValue(VALUE);//引用的入参名称
                param.add(inParamName, xmlBusiConfig.getValue(cmd, valueName));
            }
            /**
             * 从spring中获取restTemplate
             */

            RestTemplate restTemplate = (RestTemplate) XmlBusiConfigContainer.ctx.getBean("restTemplate");
            String xmlBusiConfigKey = xmlBusiConfig.getModuleName() + "-" + xmlBusiConfig.getId();
            String key = getXmlBusiConfigKeyByRequetPath(urlName, param);
            if (key.equals(xmlBusiConfigKey)) {
                throw new UnsupportedOperationException(xmlBusiConfigKey + "业务配置文件中存在Rest自身循环调用!");
            }
            switch (methodName.toUpperCase()) {
                case GET:
                    rs = restTemplate.getForObject(urlName, String.class);
                    break;
                case POST:
                    rs = restTemplate.postForObject(urlName, param, String.class);
                    break;
                default:
                    throw new UnsupportedOperationException("暂不支持的请求方式！" + methodName.toUpperCase());
            }
        } catch (Exception ex) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), cmd.getMsg(), ex);
        }
        return rs;
    }

    public static void main(String[] args) throws Exception {
        String st = "{\"Functions\":[{\"FunctionName\":\"test10\",\"Parameters\":{\"test\":{\"test\":7},\"addparams\":[{\"test\":\"1111111\",\"value\":\"wwwwwwww\"},{\"test\":\"222222\",\"value\":\"wwwwwwww\"}]},\"ModuleName\":\"base-test\"}]}";
        com.alibaba.fastjson.JSONObject object = JSON.parseObject(st);

    }

    public String getXmlBusiConfigKeyByRequetPath(String urlStr, MultiValueMap<String, Object> params) throws Exception {
        java.net.URL url = new URL(urlStr);
        String queryStr = url.getQuery();
        String key = "";
        if (url.getPath().contains("/easyComponentService")) {
            String[] paramsArr = queryStr.split("&");
            key = paramsArr[0].split("=")[1];
        }
        if (url.getPath().contains("/componentService")) {
            com.alibaba.fastjson.JSONObject jsonObject1 = null;
            if (queryStr == null) {
                for (String key1 : params.keySet()) {
                    if (key1.equals("jsonParams")) {
                        List result1 = params.get(key1);
                        jsonObject1 = JSON.parseObject(StringHelper.toString(result1.get(0)));
                    }
                }
            } else {
                jsonObject1 = JSON.parseObject(StringHelper.toString(queryStr.split("=")[1]));
            }
            JSONArray jsonArray = jsonObject1.getJSONArray("Functions");
            for (int i = 0; i < jsonArray.size(); i++) {
                com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                String moduleName = StringHelper.toString(jsonObject.get("ModuleName"));
                String functionName = StringHelper.toString(jsonObject.get("FunctionName"));
                key = moduleName + "-" + functionName;
            }
        }
        return key;
    }
}
