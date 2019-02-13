package com.easipass.epia.service.expression;

import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.util.ExpressionUtil;
import com.easipass.epia.util.HtmlParserUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by lql on 2018/12/29 10:02
 **/
public class IfExpression implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(IfExpression.class);

    /**
     * if条件表达式执行,返回执行后的sql
     *
     * @param sql
     * @param xmlBusiConfig
     * @param inAllParams
     * @param jsonObject
     * @return
     */
    public static String exec(String sql, XmlBusiConfig xmlBusiConfig, JSONObject inAllParams, Map<String, Object> jsonObject) {
        /**
         * 找到单个表达式的完整sql语句区域
         */
        int startIndex = sql.indexOf(ExpressionUtil.If);
        int endIndex = ExpressionUtil.getCloseExpressionIndex(sql, ExpressionUtil.If, ExpressionUtil.endIf);
        StringBuffer sqlBuffer = new StringBuffer(sql);
        String ifContent = sqlBuffer.substring(startIndex, endIndex);
        //取出表达式原文
        String expression = HtmlParserUtil.getValue(sql, "test=\"", "\">");
        boolean flag = ExpressionUtil.calExpression(expression, xmlBusiConfig, inAllParams, jsonObject);
        if (flag) {
            String trueSql = ifContent.substring(ifContent.indexOf(">") + 1, ifContent.lastIndexOf("</if>"));
            sqlBuffer = sqlBuffer.replace(startIndex, endIndex, trueSql);
        } else {
            sqlBuffer = sqlBuffer.replace(startIndex, endIndex, "");
        }
        return sqlBuffer.toString();
    }
}
