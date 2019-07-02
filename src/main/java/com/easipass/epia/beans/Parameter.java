package com.easipass.epia.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.dom4j.Element;

import java.io.Serializable;

/**
 * Created by lql on 2018/12/25.
 * XML中<Parameters></Parameters>最小单位标签，参数标签
 * 入参中的参数模型（基本单位）
 **/
@Data
public class Parameter implements Serializable {

    public Parameter() {}

    public Parameter(Element element) {
        init(element);
    }

    /**
     * 参数的名称
     */
    private String name;
    /**
     * 参数的默认值
     */
    private String defaultValue;
    /**
     * 是否必填
     */
    private boolean required;
    /**
     * 正则表达式
     */
    private String expression;
    /**
     * 当正则表达式匹配是失败时的描述
     */
    private String desc;

    public void init(Element element) {
        if (null == element) return;
        String p_name = element.getName();
        String defaultValue = element.attributeValue("default");
        String required = element.attributeValue("required");
        String expression = element.attributeValue("expression");
        String desc = element.attributeValue("desc");
        expression = null == expression ? "" : expression;
        expression = "null".equalsIgnoreCase(expression) ? "" : expression;
        desc = null == desc ? "" : desc;
        desc = "null".equalsIgnoreCase(desc) ? "" : desc;
        setName(p_name);
        setDefaultValue(defaultValue);
        setRequired(null != required && required.equals("true") ? true : false);
        setExpression(expression);
        setDesc(desc);
    }
}
