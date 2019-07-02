package com.easipass.epia.beans;

import lombok.Data;
import org.dom4j.Element;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2018/12/25 14:29
 * XML中<Parameters></Parameters>中一级标签，参数的父级对象
 * 入参模型（包含入参基础单位）
 **/
@Data
public class Parameters implements Serializable {

    public Parameters() {
    }

    /**
     * 初始化
     *
     * @param element
     */
    public Parameters(Element element) {
        init(element);
    }

    /**
     * 初始化一个参数对象
     *
     * @param element
     */
    public void init(Element element) {
        if (null == element) return;
        String name = element.getName();
        String islist = element.attributeValue("islist");
        List<Element> elementList = element.elements();
        for (int j = 0; j < elementList.size(); j++) {
            Element element2 = elementList.get(j);
            /**
             * 2018/12/16  updated by lql
             */
            parameterMap.put(element2.getName(), new Parameter(element2));
            /**
             * 优化
             * start------------------------------2018/12/16-----------------------------start
             */
//            String p_name = element2.getName();
//            String defaultValue = element2.attributeValue("default");
//            String required = element2.attributeValue("required");
//            String expression = element2.attributeValue("expression");
//            String desc = element2.attributeValue("desc");
//            expression = null == expression ? "" : expression;
//            expression = "null".equalsIgnoreCase(expression) ? "" : expression;
//            desc = null == desc ? "" : desc;
//            desc = "null".equalsIgnoreCase(desc) ? "" : desc;
//            Parameter parameter = new Parameter();
//            parameter.setName(p_name);
//            parameter.setDefaultValue(defaultValue);
//            parameter.setRequired(null != required && required.equals("true") ? true : false);
//            parameter.setExpression(expression);
//            parameter.setDesc(desc);
//            parameterMap.put(parameter.getName(), parameter);
            /**
             * end------------------------------2018/12/16优化-----------------------------end
             */

        }
        setName(name);
        setIslist(null != islist && islist.equals("true") ? true : false);
        setParameterMap(parameterMap);
    }

    /**
     * 参数名称
     */
    private String name;
    /**
     * 是否批量参数
     */
    private boolean islist = false;
    /**
     * 参数列表
     * key:参数标签名
     * value:该参数对应的参数模型
     */
    private Map<String, Parameter> parameterMap = new HashMap<>();

}
