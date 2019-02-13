package com.easipass.epia.service.actions;


import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.Parameter;
import com.easipass.epia.beans.Parameters;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.util.*;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2017/7/14.
 */
public class VarAction implements Serializable {
    private XmlBusiConfig xmlBusiConfig;
    /**
     * 属性遍历标签
     */
    private static final String AttrIterator = "attriterator";


    public VarAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }

    /**
     * 变量处理命令
     *
     * @param cmd
     * @param inParams
     * @return
     */
    public List<Map<String, Object>> varCommand(Action cmd, JSONObject inParams) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            //入参结果集名称
            String paramName = cmd.getParams();
            //实际值
            String value = cmd.getValue();
            String content = cmd.getContent();
            Document document = DocumentHelper.parseText(content);
            Element rootElement = document.getRootElement();
            String patten1 = "[\\s\\S]*[$][{][\\s\\S]*[}][\\s\\S]*";
            String patten2 = "[\\s\\S]*[#][{][\\s\\S]*[.][\\s\\S]*[}][\\s\\S]*";
            String patten3 = "[\\s\\S]*[#][{][\\s\\S]*[}][\\s\\S]*";
            //整体循环次数
            int range = 1;
            if (StringHelper.isNotNull(cmd.getRange())) {
                range = Integer.parseInt(cmd.getRange().trim());
            }
            for (int u = 0; u < range; u++) {
                /**
                 * <var id='test' params='addparams'>
                 *     <attriterator>
                 *     <tablename value='reports'></tablename>
                 *     <colsName value='{name}'></colsName>
                 *     <value value='{value}'></value>
                 *     <creator value='#{creator.id}'></creator>
                 *     <intime value='${fulldate}'></intime>
                 *     </attriterator>
                 * </var>
                 */
                if (ParameterUtil.hasChildrenNamed(rootElement, AttrIterator)) {
                    // 取到入参数据
                    List<Map<String, Object>> paramsArray = TypeUtil.changeToListMap(xmlBusiConfig.getParamsFromInParams_CmdResult(paramName, inParams));
                    // 取到参数模型
                    Parameters paramModel = xmlBusiConfig.getParametersMap().get(paramName);
                    // 取到参数列表
                    Element attrIteratorElement = (Element) rootElement.elements().get(0);
                    List<Element> elementList = attrIteratorElement.elements();
                    // 遍历入参数据(可能是批量操作)
                    for (int a = 0; a < paramsArray.size(); a++) {
                        // 实际参数对象
                        Map parameMap = paramsArray.get(a);
                        JSONObject jsonObject = JSONObject.fromObject(parameMap);
                        // 遍历参数模型,取出需要用到的参数
                        Iterator it = paramModel.getParameterMap().keySet().iterator();
                        while (it.hasNext()) {
                            String key = it.next().toString();
                            Parameter parameter = paramModel.getParameterMap().get(key);
                            // 当前属性名称
                            String name = parameter.getName();
                            // 当前属性对应的值
                            String realValue = parameMap.get(name).toString();
                            Map<String, Object> onedata = new HashedMap();
                            // 遍历元素
                            for (int i = 0; i < elementList.size(); i++) {
                                Element element = elementList.get(i);
                                String eleName = element.getName();
                                String eleValue = element.attributeValue("value");
                                Object rsObject = ParameterUtil.getValue(xmlBusiConfig, paramName, eleValue, inParams, jsonObject);
                                if (null == rsObject)
                                    rsObject = ParameterUtil.getValue(parameter, eleValue, realValue);
                                onedata.put(eleName, rsObject);
                            }
                            result.add(onedata);
                        }
                    }
                }
                /**
                 * <var id='test' params='addparams'>
                 *     <name value='#{name}'></name>
                 *     <id value='${guid}'></name>
                 *     <content value='测试的正文'></name>
                 *     <date value='${date}'></name>
                 * </var>
                 */
                else if (ParameterUtil.hasChildren(rootElement)) {
                    // var　中没有配置params属性
                    if (!StringHelper.isNotNull(paramName)) {

                        List<Element> elementList = rootElement.elements();
                        Map resultMap = new HashedMap();
                        for (int j = 0; j < elementList.size(); j++) {
                            Element element = elementList.get(j);
                            String elementName = element.getName();
                            String elementValue = element.attributeValue("value");
                            /**
                             * #{a.b}或者${guid}或者'abc'
                             */
                            if (ExpressionUtil.machExpression(patten1, elementValue) || ExpressionUtil.machExpression(patten2, elementValue)) {
                                Object rsObject = ParameterUtil.getValue(xmlBusiConfig, paramName, elementValue, inParams, null);
                                resultMap.put(elementName, rsObject);
                            } else {
                                resultMap.put(elementName, elementValue);
                            }
                        }
                        result.add(resultMap);
                    }// 配置了params属性
                    else {
                        List<Map<String, Object>> paramsArray = TypeUtil.changeToListMap(xmlBusiConfig.getParamsFromInParams_CmdResult(paramName, inParams));
                        for (int i = 0; i < paramsArray.size(); i++) {
                            Map<String, Object> map = paramsArray.get(i);
                            JSONObject mapObject = JSONObject.fromObject(map);
                            List<Element> elementList = rootElement.elements();
                            Map resultMap = new HashedMap();
                            for (int j = 0; j < elementList.size(); j++) {
                                Element element = elementList.get(j);
                                String elementName = element.getName();
                                String elementValue = element.attributeValue("value");
                                Object rsObject = ParameterUtil.getValue(xmlBusiConfig, paramName, elementValue, inParams, mapObject);
                                resultMap.put(elementName, rsObject);
                            }
                            result.add(resultMap);
                        }
                    }
                }
                /**
                 * <var id='test' params='addparams'></var>
                 */
                else if (ParameterUtil.isNotNull(paramName)) {
                    List<Map<String, Object>> paramsArray = TypeUtil.changeToListMap(xmlBusiConfig.getParamsFromInParams_CmdResult(paramName, inParams));
                    result = paramsArray;
                }
                /**
                 * <var id='test' value='addparams'></var> 或者<var id='test' value='${guid}'></var>
                 * 或者 <var id='test' value='#{report.id}'></var>
                 */
                else if (ParameterUtil.isNotNull(value)) {
                    /**
                     *  <var id='test' value='#{test.id}'></var>
                     */
                    if (ExpressionUtil.machExpression(patten2, value)) {
                        String p = HtmlParserUtil.getValue(value, "#{", "}");
                        String pn = p.substring(0, p.indexOf("."));
                        String attriName = p.substring(p.indexOf(".") + 1, p.length());
                        List<Map<String, Object>> paramsArray = TypeUtil.changeToListMap(xmlBusiConfig.getParamsFromInParams_CmdResult(pn, inParams));
//                if (paramsArray.size() != 1)
//                    throw new UnsupportedOperationException(cmd.getCmdType() + "标签" + cmd.getId() + "中,引用的参数" + pn + ",大小不是1,而是" + paramsArray.size());
                        StringBuffer stringBuffer = new StringBuffer(value);
                        for (int i = 0; i < paramsArray.size(); i++) {
                            Map map = paramsArray.get(i);
                            if (map.containsKey(attriName)) {
                                Object object = map.get(attriName);
                                stringBuffer = stringBuffer.replace(stringBuffer.indexOf("#{"), stringBuffer.indexOf("}") + 1, object.toString());
                                Map rsMap = new HashedMap();
                                rsMap.put(attriName, stringBuffer.toString());
                                result.add(rsMap);
                            } else {
                                throw new UnsupportedOperationException(cmd.getCmdType() + "标签" + cmd.getId() + "中,value 的引用入参属性" + pn + "不存在!");
                            }
                        }
//                /**
//                 *  <var id='test' value='#{id}'></var>
//                 */
//            } else if (value.startsWith("#{") && value.endsWith("}") && value.indexOf(".") == -1) {
//                String attrname = HtmlParserUtil.getValue(value, "#{", "}");
//                if (!ParameterUtil.isNotNull(paramName))
//                    throw new UnsupportedOperationException(cmd.getCmdType() + " " + cmd.getId() + " 没有定义入参!");
//                List<Map<String, Object>> paramsArray = xmlBusiConfig.getParamsFromInParams_CmdResult(paramName, inParams);
//                if (paramsArray.size() != 1)
//                    throw new UnsupportedOperationException(cmd.getCmdType() + "标签" + cmd.getId() + "中,引用的参数" + paramName + "大小不是1,而是" + paramsArray.size());
//                Map<String, Object> objectMap = paramsArray.get(0);
//                if (objectMap.containsKey(attrname)) {
//                    Object obj = objectMap.get(attrname);
//                    Map resMap = new HashedMap();
//                    resMap.put(attrname, obj);
//                    result.add(resMap);
//                } else {
//                    throw new UnsupportedOperationException(cmd.getCmdType() + "标签" + cmd.getId() + "中,value 的引用入参属性" + attrname + "不存在!");
//                }
                        /**
                         *  <var id='test' value='${guid}'></var>
                         */
                    } else if (ExpressionUtil.machExpression(patten1, value)) {
                        StringBuffer stringBuffer = new StringBuffer(value);
                        String p = HtmlParserUtil.getValue(value, "${", "}");
                        String object = SystemFunction.get(p);
                        Map rsMap = new HashedMap();
                        stringBuffer = stringBuffer.replace(stringBuffer.indexOf("${"), stringBuffer.indexOf("}") + 1, object);
                        rsMap.put(p, stringBuffer.toString());
                        result.add(rsMap);
                        /**
                         *  <var id='test' value='test'></var>
                         */
                    } else if (!value.startsWith("#") && !value.startsWith("$") && !value.startsWith("{")) {
                        Map map = new HashedMap();
                        map.put("value", value);
                        result.add(map);
                    } else {
                        throw new UnsupportedOperationException(cmd.getCmdType() + "标签" + cmd.getId() + "中, 的 var 标签的 value 参数配置不正确!");
                    }
                } else {
                    throw new UnsupportedOperationException(cmd.getCmdType() + "标签" + cmd.getId() + "中, 的 var 标签的参数配置不正确!");
                }
            }
        } catch (Exception ex) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), "", ex);
        }
        return result;
    }

    public static void main(String[] args) throws DocumentException {
        String cmdContent = "<var id =\"test\"><name value=\"张三\"></name></var>";
        Document document = DocumentHelper.parseText(cmdContent);
    }
}
