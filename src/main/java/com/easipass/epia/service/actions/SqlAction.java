package com.easipass.epia.service.actions;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.Parameters;
import com.easipass.epia.beans.SysProperties;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.util.DatapagingUtil;
import com.easipass.epia.util.ExpressionUtil;
import com.easipass.epia.util.TypeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Created by lql on 2017/5/25.
 * SQL语句的操作
 */
public class SqlAction implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(SqlAction.class);

    private XmlBusiConfig xmlBusiConfig;

    private JSONObject inParams;// 请求的入参(过滤后的参数)

    public SqlAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }


    /**
     * SQL命令解析
     *
     * @param action
     * @param inParams 入参
     * @return
     */
    public List<Map<String, Object>> sqlCommand(Action action, JSONObject inParams) throws Exception {
        this.inParams = inParams;
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            List<Map<String, Object>> cmdResultParam = null;
            // 没有指定入参,纯粹的SQL
            if ("".equals(action.getParams()) || null == action.getParams()) {
                result = executeSql(null, action);
                return result;
            }
            // 参数来源是： 入参
            if (xmlBusiConfig.getParametersMap().containsKey(action.getParams())) {
                // 入参模型
                Parameters inParametersModel = xmlBusiConfig.getParametersMap().get(action.getParams());
                // 批量：由传入参islist标记决定
                if (inParametersModel.isIslist()) {
                    JSONArray inParamArray = inParams.getJSONArray(action.getParams());
                    for (int i = 0; i < inParamArray.size(); i++) {
                        // 单个入参赋默认值
                        JSONObject inParam = inParamArray.getJSONObject(i);
                        result = executeSql(inParam, action);
                    }
                }
                // 单条数据
                else {
                    JSONObject inParam = inParams.getJSONObject(action.getParams());
                    result = executeSql(inParam, action);
                }
            } // 参数来源： 命令结果集
            else if (null != (cmdResultParam = TypeUtil.changeToListMap(xmlBusiConfig.getParamFromCmdResult(action.getParams())))) {
                if (action.isList())   // 批量：由cmd的islist决定
                {
                    for (int i = 0; i < cmdResultParam.size(); i++) {
                        Map map = cmdResultParam.get(i);
                        result = executeSql(map, action);
                    }
                } else {               // 首条数据
                    if (cmdResultParam.size() > 0) {
                        Map map = cmdResultParam.get(0);
                        result = executeSql(map, action);
                    } else {
                        logger.info("警告:" + action.getId() + "的入参为NULL,跳过执行!");
                    }
                }
            } else {
                throw new NoSuchElementException("没有找到" + action.getId() + " 的params 所配置的数据源");
            }
        } catch (Exception ex) {
            Exception myException = new Exception("SQL配置模块执行异常！");
            if ("false".equalsIgnoreCase(SysProperties.getIsPrintErrorDetail()))
                ex = myException;
//            throw new UserException(action.getId(), action.getErrorid(), "", ex);
            throw new UserException(action.getId(), action.getErrorid(), action.getMsg(), ex);
        }
        return result;
    }

    /**
     * 执行SQL语句
     *
     * @param inParam
     * @param action
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> executeSql(Map inParam, Action action) throws Exception {
        int num = 0;
        List<Map<String, Object>> result = null;
        List paramsList = new ArrayList<>();
        Map paramMap = new HashMap<>();
        //解析条件表达式
        String sql = ExpressionUtil.calExpression(action, xmlBusiConfig, this.inParams, inParam, paramMap);
        //设置占位符,同时构建参数列表
        String executeSql = ExpressionUtil.setParams(action, sql, inParam, paramsList, paramMap, xmlBusiConfig, this.inParams);
        /**
         * 构建分页语句
         */
        if (action.getCmdType().equalsIgnoreCase(Action.SELECT) && action.ispaging())
            executeSql = DatapagingUtil.buildPagingSql(executeSql, paramsList, ((DruidDataSource) xmlBusiConfig.getDBService().getDataSource()).getDbType(), inParam);

        action.setCmdStr(executeSql);
        /**
         * 还原执行sql
         */
        String outSql = refactorSqlByParams(executeSql, paramsList);
        logger.debug("参数列表:" + JSON.toJSONString(paramsList).toString());
        logger.debug("还原后语句:" + outSql);

        if (action.getCmdType().equalsIgnoreCase(Action.INSERT)) {
            num = xmlBusiConfig.getDBService().updateByType(executeSql, paramsList);
        } else if (action.getCmdType().equalsIgnoreCase(Action.DELETE)) {
            num = xmlBusiConfig.getDBService().updateByType(executeSql, paramsList);
        } else if (action.getCmdType().equalsIgnoreCase(Action.UPDATE)) {
            num = xmlBusiConfig.getDBService().updateByType(executeSql, paramsList);
        } else if (action.getCmdType().equalsIgnoreCase(Action.SELECT)) {
            result = xmlBusiConfig.getDBService().select(executeSql, paramsList);
        } else {
            throw new NoSuchMethodException("无法识别的命令:" + action.getCmdType());
        }
        logger.debug(executeSql);
        logger.debug(num + "-" + result);
        if (null == result) {
            result = new ArrayList<>();
            Map map = new HashMap();
            map.put("total", num);
            result.add(map);
        }
        action.setResult(result);
        return result;
    }

    /**
     * 把参数恢复到sql语句中,组建完整的sql
     *
     * @param sql
     * @param params
     * @return
     */
    private String refactorSqlByParams(String sql, List params) {
        StringBuffer sqlBuffer = new StringBuffer(sql);
        for (int i = 0; i < params.size(); i++) {
            String param = String.valueOf(params.get(i));
            sqlBuffer = sqlBuffer.replace(sqlBuffer.indexOf("?"), sqlBuffer.indexOf("?") + 1, "'" + param + "'");
        }
        return sqlBuffer.toString();
    }
}
