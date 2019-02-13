package com.easipass.epia.util;


import com.easipass.epia.beans.Parameter;
import com.easipass.epia.beans.XmlBusiConfig;
import net.sf.json.JSONObject;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lql on 2017/7/18.
 */
public class ParameterUtil {
    /**
     * 检验参数名称是否有效
     *
     * @param paramsName
     * @return
     */
    public static boolean isNotNull(String paramsName) {
        if (null == paramsName)
            return false;
        if ("null".equalsIgnoreCase(paramsName))
            return false;
        if ("".equals(paramsName))
            return false;
        return true;
    }

    /**
     * 校验标签集合中是否包含指定名称的标签
     *
     * @param rootElement
     * @param elementName
     * @return
     */
    public static boolean hasChildrenNamed(Element rootElement, String elementName) {
        List<Element> elementList = rootElement.elements();
        for (int i = 0; i < elementList.size(); i++) {
            Element element = elementList.get(i);
            if (element.getName().equals(elementName))
                return true;
        }
        return false;
    }

    /**
     * 有子元素
     *
     * @param rootElement
     * @return
     */
    public static boolean hasChildren(Element rootElement) {
        List<Element> elementList = rootElement.elements();
        if (elementList.size() > 0)
            return true;
        return false;
    }

    /**
     * 获取参数的值
     *
     * @param params 对应 params 属性的值
     * @param attr   ${guid}\#{name}\tablename\#{user.id}
     * @return
     */
    public static Object getValue(XmlBusiConfig busiConfig, String params, String attr, JSONObject inParams, JSONObject jsonObject) {
        String patten1="[\\s\\S]*[$][{][\\s\\S]*[}][\\s\\S]*";
        String patten2="[\\s\\S]*[#][{][\\s\\S]*[.][\\s\\S]*[}][\\s\\S]*";
        String patten3="[\\s\\S]*[#][{][\\s\\S]*[}][\\s\\S]*";
        /**
         * 1.常量 ${guid}
         */
        // TODO: 2019/1/17 待完善 、无法在value中使用多个系统属性
        if (ExpressionUtil.machExpression(patten1,attr)) {
            String pName = HtmlParserUtil.getValue(attr, "${", "}");
            String obj = SystemFunction.get(pName);
            StringBuffer stringBuffer=new StringBuffer(attr);
            stringBuffer=stringBuffer.replace(stringBuffer.indexOf("${"),stringBuffer.indexOf("}")+1,obj);
            return stringBuffer.toString();
        }
        /**
         * 2.其他的集合的属性#{report.id}
         */
        else if (ExpressionUtil.machExpression(patten2,attr)) {
            StringBuffer stringBuffer=new StringBuffer(attr);
            String fullName = HtmlParserUtil.getValue(attr, "#{", "}");
            params = fullName.substring(0, fullName.indexOf("."));
            attr = fullName.substring(fullName.indexOf(".") + 1, fullName.length());
            List<Map<String, Object>> list = TypeUtil.changeToListMap(busiConfig.getParamsFromInParams_CmdResult(params, inParams));
            if (list.size() != 1)
                throw new UnsupportedOperationException(params + "的集合大小应该是1,而实际上是:" + list.size());
            Map<String, Object> map = list.get(0);
            Object object = map.get(attr);
            stringBuffer=stringBuffer.replace(stringBuffer.indexOf("#{"),stringBuffer.indexOf("}")+1,object+"");
            return stringBuffer.toString();
        }
        /**
         * 3.当前引用集合的属性 #{name}
         */
        else if (ExpressionUtil.machExpression(patten3,attr) && attr.indexOf(".") == -1) {
            String pName = HtmlParserUtil.getValue(attr, "#{", "}");
            String obj= jsonObject.get(pName).toString();
            StringBuffer stringBuffer=new StringBuffer(attr);
            stringBuffer=stringBuffer.replace(stringBuffer.indexOf("#{"),stringBuffer.indexOf("}")+1,obj+"");
            return stringBuffer.toString();
        }
        /**
         * 对于只有在循环属性的标签下才会使用的符号,返回null
         */
        else if (attr.startsWith("{") && attr.endsWith("}")) {
            return null;
        /**
         * 4.手动设定值 tablename
         */
        } else {
            return attr;
        }
    }

    /**
     * 取得标签元素中,当前对象属性的名称和值
     *
     * @param parameter
     * @param value
     * @param realValue
     * @return
     */
    public static Object getValue(Parameter parameter, String value, String realValue) {
        if (value.equalsIgnoreCase("{name}")) {
            return parameter.getName();
        } else if (value.equalsIgnoreCase("{value}")) {
            return realValue;
        } else {
            throw new UnsupportedOperationException("无法识别标签格式:" + value);
        }
    }


    public static void main(String args[])
    {
        String str="aadfdffd你好#{abc}发的发的就";
//        String patt="^[a-z]*#[{]abc[}]$";
        String patten2="[\\s\\S]*[#][{](?!.)[\\s\\S]*[}][\\s\\S]*";
        Pattern pattern = Pattern.compile(patten2);
        Matcher matcher = pattern.matcher(str);
        System.out.println(matcher.matches());
    }
}
