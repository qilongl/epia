package com.easipass.epia.util;

import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.service.expression.IfExpression;
import com.googlecode.aviator.AviatorEvaluator;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lql on 2017/7/19.
 * SQL表达式解析类
 */
public class ExpressionUtil {

    /**
     * 定义表达式
     */
    public static final String If = "<if";
    public static final String endIf = "</if>";
    public static final String Foreach = "<foreach";
    public static final String endForeach = "</foreach>";
    public static List<String> expressionList = new ArrayList<>();

    static {
        expressionList.add(If);
        expressionList.add(Foreach);
    }

    private static Logger logger = LoggerFactory.getLogger(ExpressionUtil.class);


    public static String calExpression(Action cmd, XmlBusiConfig busiConfig, JSONObject inAllParams, Map<String, Object> inParams, Map paramMap) throws Exception {
        // 得到sql正文
        String sql = getAllContent(cmd.getContent());
        // 取得最外层表达式,然后执行,得到结果
        while (true) {
            String newSql = calOuterExpression(sql, busiConfig, inAllParams, inParams, paramMap);
            if (newSql.equalsIgnoreCase(sql)) {
                break;
            }
            sql = newSql;
        }
        return sql;
    }

    /**
     * 最外外层sql的表达式
     *
     * @param inParams 当前参数
     * @return
     */
    public static String calOuterExpression(String sql, XmlBusiConfig busiConfig, JSONObject inAllParams, Map<String, Object> inParams, Map paramMap) throws Exception {
        // 取最外层表达式
        TreeMap expressionMap = new TreeMap();
        for (int i = 0; i < expressionList.size(); i++) {
            String expressionName = expressionList.get(i);
            int index = sql.indexOf(expressionName);
            if (index != -1) {
                expressionMap.put(new Integer(index), expressionName);
            }
        }
        //如果最外层没有表达式,则说明么有表达式了,返回sql
        if (expressionMap.size() == 0)
            return sql;
        int num = 0;
        String expressionName = "";
        for (Object object : expressionMap.keySet()) {
            if (num == 0) {
                expressionName = expressionMap.get(object).toString();
            }
            num++;
        }
        /**
         * 对表达式进行处理
         */
        switch (expressionName) {
            case If:
                sql = IfExpression.exec(sql, busiConfig, inAllParams, inParams);
                break;
            case Foreach:
                // TODO: 2019/2/12
                break;
            default:
                break;
        }
        return sql;
    }

    /**
     * 获取结束标签的下标
     *
     * @param sql
     * @param startExpression
     * @param endExpression
     * @return
     */
    public static int getCloseExpressionIndex(String sql, String startExpression, String endExpression) {
        int startNum = 0;
        int position = 0;
        StringBuffer stringBuffer = new StringBuffer(sql);
        startNum++;
        position = stringBuffer.indexOf(startExpression) + startExpression.length();
        stringBuffer = stringBuffer.replace(0, stringBuffer.indexOf(startExpression) + startExpression.length(), "");
        while (startNum > 0) {
            int startIndex = stringBuffer.indexOf(startExpression);
            int endIndex = stringBuffer.indexOf(endExpression);
            if (startIndex != -1 && startIndex < endIndex) {
                startNum++;
                position = stringBuffer.indexOf(startExpression) + startExpression.length() + position;
                stringBuffer = stringBuffer.replace(0, stringBuffer.indexOf(startExpression) + startExpression.length(), "");
            } else {
                startNum--;
                position = stringBuffer.indexOf(endExpression) + endExpression.length() + position;
                stringBuffer = stringBuffer.replace(0, stringBuffer.indexOf(endExpression) + endExpression.length(), "");
            }
        }
        return position;
    }

    public static boolean calExpression(String expression, XmlBusiConfig busiConfig, JSONObject inAllParams, Map<String, Object> jsonObject) {
        /**
         * 去掉表达式中的参数符号
         */
        logger.debug("表达式原文:" + expression);
        if (expression.indexOf("#{") != -1) {
            StringBuffer expressionBuffer = new StringBuffer(expression);
            while (true) {
                StringBuffer newExpression = ExpressionUtil.getParameter(expressionBuffer, busiConfig, inAllParams);
                if (newExpression != null) {
                    expressionBuffer = newExpression;
                } else {
                    break;
                }
            }
            expression = expressionBuffer.toString();
        }
        /**
         *取出数据集合对象,调用AviatorEvaluator 引擎执行
         */
        logger.debug("表达式预处理后:" + expression);
        Map<String, Object> map = jsonObject;
        Calendar begin = Calendar.getInstance();
        boolean flag = (boolean) AviatorEvaluator.execute(expression, map);
        Calendar end = Calendar.getInstance();
        logger.debug("执行" + expression + " 结果:" + flag + " ,耗时:" + (end.getTime().getTime() - begin.getTime().getTime()) + " ms");
        return flag;
    }

    /**
     * <select id="test2"  isreturn="true" params="addparams">
     * <![CDATA[
     * SELECT * from reports where 1=1
     * and title=#{addparams.title}
     * and reportcontent <='测试正文'
     * and reportcontent=@{tt} or title=#{tt} or tt like %#{}
     * and reportcontent=@{tt} or title=#{tt} or tt like %#{}%
     * and reportcontent=@{tt} or title=#{tt} or tt like #{}_
     * ]]>
     * </select>
     *
     * @param jsonObject
     * @param paramMap
     * @return
     */
    public static String setParams(Action action, String sql, Map<String, Object> jsonObject, List<Object> paramList, Map paramMap, XmlBusiConfig busiConfig, JSONObject allInParams) {
        StringBuffer stringBuffer = new StringBuffer(sql);
        while (replaceParam(action, stringBuffer, jsonObject, paramList, paramMap, busiConfig, allInParams) != null) ;
        return stringBuffer.toString();
    }

    /**
     * 替换当前语句最近一个参数
     *
     * @param sql
     * @param paramList
     * @param paramMap
     * @return
     */
    public static StringBuffer replaceParam(Action action, StringBuffer sql, Map<String, Object> jsonObject, List<Object> paramList, Map paramMap, XmlBusiConfig busiConfig, JSONObject allInParams) {
        if (sql.indexOf("#") == -1 && sql.indexOf("@") == -1 && sql.indexOf("$") == -1)
            return null;
        String paramContent = "";
        String symbol = getNearestSymbol(sql);
        switch (symbol) {
            case "%#{":
                paramContent = HtmlParserUtil.getValue(sql.toString(), "%#{", "}%");
                setParamsForJSONObject(action, paramContent, sql, "%#{", "}%", paramList, jsonObject, busiConfig, allInParams);
                break;
            case "#{":
                paramContent = HtmlParserUtil.getValue(sql.toString(), "#{", "}");
                setParamsForJSONObject(action, paramContent, sql, "#{", "}", paramList, jsonObject, busiConfig, allInParams);
                break;
            case "${":
                paramContent = HtmlParserUtil.getValue(sql.toString(), "${", "}");
                sql = HtmlParserUtil.replace(sql, "${", "}", "?");
                paramList.add(SystemFunction.get(paramContent));
                break;
            case "@{":
                paramContent = HtmlParserUtil.getValue(sql.toString(), "@{", "}");
                sql = HtmlParserUtil.replace(sql, "@{", "}", "?");
                paramList.add(paramMap.get(paramContent));
                break;
            case "#[":
                paramContent = HtmlParserUtil.getValue(sql.toString(), "#[", "]");
                sql = setParamsForList(action, paramContent, sql, "#[", "]", paramList, jsonObject, busiConfig, allInParams);
                break;
            default:
                logger.debug(sql + "\t 所有参数替换完毕!");
                return null;
        }
        return sql;
    }

    /**
     * 得到最近的符号
     *
     * @param sql
     * @return
     */
    public static String getNearestSymbol(StringBuffer sql) {
        int index1 = sql.indexOf("%#{");
        int index2 = sql.indexOf("#{");
        int index3 = sql.indexOf("${");
        int index4 = sql.indexOf("@{");
        int index5 = sql.indexOf("#[");
        TreeMap treeMap = new TreeMap();
        if (index1 != -1)
            treeMap.put(Integer.valueOf(index1), "%#{");
        if (index2 != -1)
            treeMap.put(Integer.valueOf(index2), "#{");
        if (index3 != -1)
            treeMap.put(Integer.valueOf(index3), "${");
        if (index4 != -1)
            treeMap.put(Integer.valueOf(index4), "@{");
        if (index5 != -1)
            treeMap.put(Integer.valueOf(index5), "#[");
        int num = 0;
        String str = "";
        for (Object object : treeMap.keySet()) {
            if (num == 0) {
                str = treeMap.get(object).toString();
            }
            num++;
        }
        return str;
    }

    /**
     * 直接从参数或者结果集中取得参数对象
     *
     * @param busiConfig
     * @param allInParams
     * @param paramName
     * @return
     */
    public static Map<String, Object> getMapObject(XmlBusiConfig busiConfig, JSONObject allInParams, String paramName) {
        List<Map<String, Object>> list = TypeUtil.changeToListMap(busiConfig.getParamsFromInParams_CmdResult(paramName, allInParams));
        if (list.size() != 1) {
            throw new UnsupportedOperationException("在参数或者结果集中," + paramName + "对应的结果集大小不为 1 .实际是 " + list.size());
        }
        Map<String, Object> map = list.get(0);
        return map;
    }

    /**
     * 给从param 中取值的#{}符号的设置参数
     *
     * @param content
     * @param sqlBuffer
     * @param startStr
     * @param endStr
     * @param paramsList
     * @param jsonObject
     * @param busiConfig
     * @param allInParams
     * @return
     */
    public static StringBuffer setParamsForJSONObject(Action action, String content, StringBuffer sqlBuffer, String startStr, String endStr, List paramsList, Map<String, Object> jsonObject, XmlBusiConfig busiConfig, JSONObject allInParams) {
        if (content.indexOf(".") != -1) {
            String name = content.substring(0, content.indexOf("."));
            String prop = content.substring(content.indexOf(".") + 1, content.length());
            Map<String, Object> map = getMapObject(busiConfig, allInParams, name);
            sqlBuffer = HtmlParserUtil.replace(sqlBuffer, startStr, endStr, "?");
            paramsList.add(map.get(prop));
        } else {
            sqlBuffer = HtmlParserUtil.replace(sqlBuffer, startStr, endStr, "?");
            paramsList.add(jsonObject.get(content));
        }
        return sqlBuffer;
    }

    /**
     * in #[?,?,?...] 设置参数和占位符
     *
     * @param content
     * @param sqlBuffer
     * @param startStr
     * @param endStr
     * @param paramsList
     * @param jsonObject
     * @param busiConfig
     * @param allInParams
     * @return
     */
    public static StringBuffer setParamsForList(Action action, String content, StringBuffer sqlBuffer, String startStr, String endStr, List paramsList, Map<String, Object> jsonObject, XmlBusiConfig busiConfig, JSONObject allInParams) {
        String name;
        String prop;
        if (content.indexOf(".") != -1) {
            name = content.substring(0, content.indexOf("."));
            prop = content.substring(content.indexOf(".") + 1, content.length());
        } else {
            name = action.getParams();
            prop = content;
        }
        List<Map<String, Object>> listmap = TypeUtil.changeToListMap(busiConfig.getParamsFromInParams_CmdResult(name, allInParams));
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < listmap.size(); i++) {
            Map map = listmap.get(i);
            Object object = map.get(prop);
            stringBuffer.append("?,");
            paramsList.add(object);
        }
        stringBuffer = stringBuffer.replace(stringBuffer.lastIndexOf(","), stringBuffer.lastIndexOf(",") + 1, "");
        sqlBuffer = HtmlParserUtil.replace(sqlBuffer, startStr, endStr, "(" + stringBuffer + ")");
        return sqlBuffer;

    }

    /**
     * 表达式匹配
     *
     * @param patten
     * @param value
     * @return
     */
    public static boolean machExpression(String patten, String value) {
        Pattern pattern = Pattern.compile(patten);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

//    /**
//     * 从sql中文中解析出 条件表达式 所在的位置下标
//     *
//     * @param sql
//     * @param expression
//     * @return
//     */
//    public static List<Integer> getExpressionIndexArray(String sql, String expression) {
//        List<Integer> indexArray = new ArrayList<>();
//        StringBuffer stringBuffer = new StringBuffer(sql);
//        int num = 0;
//        while (stringBuffer.indexOf(expression) != -1) {
//            int index = stringBuffer.indexOf(expression);
//            if (num != 0) {
//                indexArray.add(index + indexArray.get(num - 1) + expression.length());
//            } else {
//                indexArray.add(index);
//            }
//            num++;
//            stringBuffer = stringBuffer.replace(0, stringBuffer.indexOf(expression) + expression.length(), "");
//        }
//        return indexArray;
//    }


    public static String getAllContent(String content) {
        if (content.contains("<![CDATA[")) {
            return content.substring(content.indexOf("<![CDATA[") + 9, content.lastIndexOf("]]>"));
        } else {
            int start = content.indexOf(">") + 1;
            int end = content.lastIndexOf("</");
            return content.substring(start, end);
        }
    }

    /**
     * 解析执行IF表达式
     *
     * @param content
     * @param action
     * @param inParams
     * @param busiConfig
     * @return
     * @throws Exception
     */
//    public static String execIf(String content, Action action, JSONObject inParams, XmlBusiConfig busiConfig, List paramsList) throws Exception {
//        String cmdStr = content;
//        //1.解析if条件标签
//        StringBuffer sqlBuffer = new StringBuffer(cmdStr);
//        StringBuffer res;
//        while (true) {
//            res = replaceIf(sqlBuffer, action, inParams, busiConfig, paramsList);
//            if (res == null)
//                break;
//            sqlBuffer = res;
//        }
//        //2.替换&gt;&lt;为> , <
//        String result = replaceSymbolInSql(sqlBuffer.toString());
//        return result;
//    }

    /**
     * 解析foreach 表达式
     *
     * @param action
     * @param inParams
     * @param busiConfig
     * @return
     * @throws Exception
     */
//    public static String execForeach(String content, Action action, JSONObject inParams, XmlBusiConfig busiConfig, List paramsList) throws Exception {
//        String cmdStr = content;
//        StringBuffer sqlBuffer = new StringBuffer(cmdStr);
//        StringBuffer res;
//        while (true) {
//            res = replaceForEach(sqlBuffer, action, inParams, busiConfig, paramsList);
//            if (res == null)
//                break;
//            sqlBuffer = res;
//        }
//        return sqlBuffer.toString();
//    }


    /**
     * 替换foreach 标签
     *
     * @param sqlbuffer
     * @param action
     * @param inParams
     * @param busiConfig
     * @return
     */
//    public static StringBuffer replaceForEach(StringBuffer sqlbuffer, Action action, JSONObject inParams, XmlBusiConfig busiConfig, List paramsList) {
//        if (sqlbuffer.indexOf("<foreach") == -1) {
//            return null;
//        }
//        String foreachSql = sqlbuffer.substring(sqlbuffer.indexOf("<foreach"), sqlbuffer.indexOf("</foreach>") + 10);
//        String result = foreachExpression(foreachSql, action, inParams, busiConfig, paramsList);
//        sqlbuffer = new StringBuffer(sqlbuffer.toString().replace(foreachSql, result));
//        return sqlbuffer;
//    }

    /**
     * 替换解析执行foreach表达式:
     * <foreach collection="#{addparams.users}" separator=";" item="user">
     * a.userid=#{user}
     * </foreach>
     *
     * @return
     */
//    private static String foreachExpression(String foreachContent, Action action, JSONObject inParams, XmlBusiConfig busiConfig, List paramsList) {
//        StringBuffer sqlResult = new StringBuffer("");
//        String params = action.getParams();
//        String collection = HtmlParserUtil.getValue(foreachContent, "#{", "}");
//        String separator = HtmlParserUtil.getValue(foreachContent, "separator=\"", "\"");
//        String item = HtmlParserUtil.getValue(foreachContent, "item=\"", "\"");
//        String key = "";
//        if (collection.indexOf(".") != -1) {
//            params = collection.substring(0, collection.indexOf("."));
//            key = collection.substring(collection.indexOf(".") + 1, collection.length());
//        } else {
//            key = collection;
//        }
//        /**
//         * 从入参和结果集中取出数据
//         */
//        List<Map<String, Object>> paramsFromInParams = busiConfig.getParamsFromInParams_CmdResult(params, inParams);
//        if (paramsFromInParams.size() != 1)
//            throw new UnsupportedOperationException(foreachContent + "的入参集合大小不为1,入参配置的不正确!");
//        /**
//         * 取出SQL正文
//         */
//        String sqlcontent = HtmlParserUtil.getValue(foreachContent, ">", "</foreach>");
//
//        Map<String, Object> map = paramsFromInParams.get(0);
//        /**
//         * 取出需要遍历的集合values,遍历构建出完整的sql,替换参数为?
//         */
//        Object values = map.get(key);
//        String[] args = values.toString().split(separator);
//        for (int i = 0; i < args.length; i++) {
//            String arg = args[i];
//            StringBuffer sqlcontentBuffer = new StringBuffer(sqlcontent);
//            // 每次循环替换一个参数为?,同时在参数列表中增加参数
//            while (true) {
//                if (sqlcontentBuffer.indexOf("#{" + item + "}") == -1)
//                    break;
//                if (sqlcontentBuffer.indexOf("%#{" + item + "}%") != -1 && sqlcontentBuffer.indexOf("%#{" + item + "}%") < sqlcontentBuffer.indexOf("#{" + item + "}")) {
//                    sqlcontentBuffer = sqlcontentBuffer.replace(sqlcontentBuffer.indexOf("%#{"), sqlcontentBuffer.indexOf("}%") + 2, "?");
//                    paramsList.add("%" + arg + "%");
//                } else if (sqlcontentBuffer.indexOf("#{" + item + "}%") != -1 && sqlcontentBuffer.indexOf("#{" + item + "}%") == sqlcontentBuffer.indexOf("#{" + item + "}")) {
//                    sqlcontentBuffer = sqlcontentBuffer.replace(sqlcontentBuffer.indexOf("#{"), sqlcontentBuffer.indexOf("}%") + 2, "?");
//                    paramsList.add(arg + "%");
//                } else if (sqlcontentBuffer.indexOf("%#{" + item + "}") != -1 && sqlcontentBuffer.indexOf("%#{" + item + "}") == sqlcontentBuffer.indexOf("#{" + item + "}") - 1) {
//                    sqlcontentBuffer = sqlcontentBuffer.replace(sqlcontentBuffer.indexOf("%#{"), sqlcontentBuffer.indexOf("}") + 1, "?");
//                    paramsList.add("%" + arg);
//                } else if (sqlcontentBuffer.indexOf("#{" + item + "}") != -1) {
//                    sqlcontentBuffer = sqlcontentBuffer.replace(sqlcontentBuffer.indexOf("#{"), sqlcontentBuffer.indexOf("}") + 1, "?");
//                    paramsList.add(arg);
//                } else {
//                    throw new UnsupportedOperationException("无法识别参数!" + action.getId());
//                }
//            }
//            sqlResult.append(sqlcontentBuffer);
//        }
//        return sqlResult.toString();
//    }

    /**
     * 替换单个if,1.取出完成的if 条件
     *
     * @param sqlbuffer
     * @param action     select * from reports where 1=1 <if test="#{creator}=='杨洋'"></if>
     * @param inParams
     * @param busiConfig
     * @return
     */
//    private static StringBuffer replaceIf(StringBuffer sqlbuffer, Action action, JSONObject inParams, XmlBusiConfig busiConfig, List paramsList) {
//        if (sqlbuffer.indexOf("<if") == -1) {
//            return null;
//        }
//        /**
//         * 取出if条件的中间语句
//         */
//        String ifSql = sqlbuffer.substring(sqlbuffer.indexOf("<if"), sqlbuffer.indexOf("</if>") + 5);
//        /**
//         * 执行if条件表达式
//         */
//        String result = ifExpression(ifSql, action, inParams, busiConfig, paramsList);
//        /**
//         * 用表达式执行的结果来替换if条件标签
//         */
//        sqlbuffer = new StringBuffer(sqlbuffer.toString().replace(ifSql, result));
//        return sqlbuffer;
//    }
//
//    /**
//     * 处理if 条件表达式,返回语句
//     *
//     * @param ifContent <if test="#{addparams.title}=='研报提交'">
//     *                  r.reportType=001 or r.reportcontent={addparams.content} or r.title={title}
//     *                  </if>
//     * @return
//     */
//    private static String ifExpression(String ifContent, Action action, JSONObject inParams, XmlBusiConfig busiConfig, List paramsList) {
//        String params = action.getParams();
//        String key = "";
//        /**
//         * 取出表达式
//         */
//        String expression = HtmlParserUtil.getValue(ifContent, "test=\"", "\">");
//        if (expression.indexOf("#{") != -1) {
//            if (expression.indexOf(".") != -1) {
//                params = HtmlParserUtil.getValue(expression, "#{", ".");
//            }
//            StringBuffer expressionBuffer = new StringBuffer(expression);
////            while (getParameter(expressionBuffer) != null) ;
////            expression = expressionBuffer.toString();
//        }
//        /**
//         * 取出表达中的参数对象
//         */
//        List<Map<String, Object>> paramsMapList = busiConfig.getParamsFromInParams_CmdResult(params, inParams);
//        if (paramsMapList.size() != 1)
//            throw new UnsupportedOperationException(action.getCmdType() + " " + action.getId() + " 表达式中使用的对象是列表,不正确!");
//        Map<String, Object> map = paramsMapList.get(0);
//        Calendar begin = Calendar.getInstance();
//        boolean flag = (boolean) AviatorEvaluator.execute(expression, map);
//        Calendar end = Calendar.getInstance();
//        logger.debug(ExpressionUtil.class.getName(), "执行" + expression + "耗时:" + (end.getTime().getTime() - begin.getTime().getTime()) + " ms");
//        /**
//         * 表达式执行返回true/false
//         */
//        if (flag) {
//            String ifcontent = HtmlParserUtil.getValue(ifContent, "\">", "</if>");
//            StringBuffer ifcontentBuffer = new StringBuffer(ifcontent);
//            while (true) {
//                if (ifcontentBuffer.indexOf("#{") == -1)
//                    break;
//                //1.取出参数
//                String paramStr = ifcontentBuffer.substring(ifcontentBuffer.indexOf("#{") + 2, ifcontentBuffer.indexOf("}"));
//                if (paramStr.indexOf(".") == -1) {
//                    key = paramStr;
//                } else {
//                    params = paramStr.substring(0, paramStr.indexOf("."));
//                    key = paramStr.substring(paramStr.indexOf(".") + 1, paramStr.length());
//                }
//                List<Map<String, Object>> realParams = busiConfig.getParamsFromInParams_CmdResult(params, inParams);
//                if (realParams.size() != 1)
//                    throw new UnsupportedOperationException(paramStr + " 中的 " + params + " 对应的参数大小不是1 ");
//                Map<String, Object> resultMap = realParams.get(0);
//                // 取到实际的值
//                Object object = resultMap.get(key);
//                //2.参数替换成?
//                if (ifcontentBuffer.indexOf("%#{") != -1 && ifcontentBuffer.indexOf("%#{") == ifcontentBuffer.indexOf("#{") - 1) {
//                    ifcontentBuffer = ifcontentBuffer.replace(ifcontentBuffer.indexOf("%#{"), ifcontentBuffer.indexOf("}%") + 2, "?");
//                    paramsList.add("%" + object + "%");
//                } else {
//                    ifcontentBuffer = ifcontentBuffer.replace(ifcontentBuffer.indexOf("#{"), ifcontentBuffer.indexOf("}") + 1, "?");
//                    //3.参数加入到列表中
//                    paramsList.add(object);
//                }
//            }
//            return ifcontentBuffer.toString();
//        } else {
//            return "";
//        }
//    }

    /**
     * 替换当前表达式中的参数标识符
     *
     * @param expression
     * @return
     */
    public static StringBuffer getParameter(StringBuffer expression, XmlBusiConfig busiConfig, JSONObject inAllParams) {
        int start = expression.indexOf("#{");
        int end = expression.indexOf("}");
        int point = expression.indexOf(".");
        if (start != end) {
            if (point > start && point < end)// 有. 引用其他的对象
            {
                logger.debug("表达式:" + expression);
                String params = expression.substring(start + 2, point);
                String attr = expression.substring(point + 1, end);
                List<Map<String, Object>> list = TypeUtil.changeToListMap(busiConfig.getParamsFromInParams_CmdResult(params, inAllParams));
                if (list.size() != 1)
                    throw new UnsupportedOperationException(expression + "引用的数据集大小不为1,实际值" + list.size());
                Map<String, Object> mapobj = list.get(0);
                Object value = mapobj.get(attr);
                value = value == null ? "\"\"" : value;
                value = "null".equals(value) ? "\"\"" : value;
                expression = expression.replace(start, end + 1, "\'" + value + "\'");
                logger.debug("表达式值:" + expression);
            } else {
                String sb = expression.substring(start + 2, end); //没有. 引用当前对象
                expression = expression.replace(start, end + 1, sb);
            }
        } else {
            return null;
        }
        return expression;
    }

    /**
     * 替换< ,>
     *
     * @param sql
     * @return
     */
    public static String replaceSymbolInSql(String sql) {
        sql = sql.replaceAll("&lt;", "<");
        sql = sql.replaceAll("&gt;", ">");
        return sql;
    }


    //---------------------------------------重构后---------------------------------------------


    public static void main(String args[]) throws Exception {
//        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject map = new JSONObject();
        map.put("title", "你好");
        map.put("content", "正文");
        Object object = AviatorEvaluator.exec("\'a\"b\'==''", "a");  // 5
        System.out.println(object);
//
//
//        String content = "<select>" +
//                "select * from users u <if test=\"#{userid.id}=='11'\"> where id=1 </if>" +
//                "left join report r on u.id=r.id" +
//                "</select>";
//        Document document = DocumentHelper.parseText(content);
//        Element rootElement = document.getRootElement();
//        String cc=getAllContent(content);
//        System.out.println(cc);
//        String sql="123<if test=>3333<if test=<if test=444</if></if></if>fdfadd";
//        int num=ExpressionUtil.getCloseExpressionIndex(sql,"<if","</if>");
//        System.out.println(num);
    }
}
