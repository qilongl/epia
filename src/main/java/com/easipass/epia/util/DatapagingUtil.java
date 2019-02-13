package com.easipass.epia.util;

import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2017/8/17.
 */
public class DatapagingUtil {
    /**
     * 分页的参数名称
     */
    public final static String pageSize = "PAGESIZE";
    public final static String pageNum = "PAGENUM";
    public final static String total = "TOTAL";

    /**
     * 构建分页
     *
     * @param sql             语句
     * @param paramsList      参数列表
     * @param dbName          数据库类型 oracle/mysql/sqlserver
     * @param paramJsonObject 参数的json对象
     * @return
     * @throws Exception
     */
    public static String buildPagingSql(String sql, List paramsList, String dbName, Map<String, Object> paramJsonObject) throws Exception {
        StringBuffer stringBuffer = new StringBuffer(sql);
        switch (dbName.toUpperCase()) {
            case "ORACLE":
                stringBuffer = buildoracleSql(stringBuffer, paramJsonObject, paramsList);
                break;
            case "MYSQL":
                stringBuffer = buildmysqlSql(stringBuffer, paramJsonObject, paramsList);
                break;
            case "SQLSERVER":
                stringBuffer = buildSqlServerSql(stringBuffer, paramJsonObject, paramsList);
                break;
            default:
                break;
        }
        return stringBuffer.toString();
    }

    /**
     * 构建查询总数语句
     *
     * @param sql
     * @return
     */
    public static String buildTotalSql(String sql) {
        StringBuffer totalPreFix = new StringBuffer("select count(*) as " + total + " from (");
        StringBuffer totalPostFix = new StringBuffer(")");
        StringBuffer contentBuffer = new StringBuffer(sql);
        return totalPreFix.append(contentBuffer).append(totalPostFix).toString();
    }

    /**
     * oracle的查询分页语句
     *
     * @param sql
     * @param jsonObject
     * @param paramsList
     * @return
     */
    public static StringBuffer buildoracleSql(StringBuffer sql, Map<String, Object> jsonObject, List paramsList) {
        StringBuffer prefix = new StringBuffer("select * from\n" +
                "            (\n" +
                "                select A.*,ROWNUM RN FROM\n" +
                "                (");
        StringBuffer postfix = new StringBuffer(")A WHERE ROWNUM <= ?*?\n" +
                "             )\n" +
                "             where RN >?*(?-1)");
        sql = prefix.append(sql).append(postfix);
        paramsList.add(jsonObject.get(pageSize));
        paramsList.add(jsonObject.get(pageNum));
        paramsList.add(jsonObject.get(pageSize));
        paramsList.add(jsonObject.get(pageNum));
        return sql;
    }

    /**
     * 构建mysql分页语句
     *
     * @param sql
     * @param jsonObject
     * @param paramsList
     * @return
     */
    public static StringBuffer buildmysqlSql(StringBuffer sql, Map<String, Object> jsonObject, List paramsList) {
        StringBuffer postfix = new StringBuffer(" limit ?,?");
        sql = sql.append(postfix);
        int firstParameter = (Integer.parseInt(jsonObject.get(pageNum).toString()) - 1) * Integer.parseInt(jsonObject.get(pageSize).toString());
        int secondParameter = Integer.parseInt(jsonObject.get(pageNum).toString()) * Integer.parseInt(jsonObject.get(pageSize).toString());
        paramsList.add(firstParameter);
        paramsList.add(secondParameter);
        return sql;
    }

    /**
     * SqlServer的查询分页语句
     *
     * @param sql
     * @param jsonObject
     * @param paramsList
     * @return
     */
    public static StringBuffer buildSqlServerSql(StringBuffer sql, Map<String, Object> jsonObject, List paramsList) {
        StringBuffer prefix = new StringBuffer();
        if ("".equals(StringHelper.toString(jsonObject.get("PROP")))) {
            if ("A".equals(StringHelper.toString(jsonObject.get("SORT")))) {
                prefix = new StringBuffer("SELECT *\n" +
                        "  FROM (SELECT *, ROW_NUMBER() OVER(ORDER BY XGRQ ASC " + ") AS RN\n" +
                        "          FROM (");
            } else {
                prefix = new StringBuffer("SELECT *\n" +
                        "  FROM (SELECT *, ROW_NUMBER() OVER(ORDER BY XGRQ DESC" + ") AS RN\n" +
                        "          FROM (");
            }

        } else {
            String str = "";
            if ("A".equals(StringHelper.toString(jsonObject.get("SORT")))) {
                str = "SELECT *\n FROM (SELECT *, ROW_NUMBER() OVER( ORDER BY " + StringHelper.toString(jsonObject.get("PROP")) +
                        " ASC ) AS RN\n FROM (";
            } else {
                str = "SELECT *\n FROM (SELECT *, ROW_NUMBER() OVER( ORDER BY " + StringHelper.toString(jsonObject.get("PROP")) +
                        " DESC ) AS RN\n FROM (";
            }
            prefix.append(str);
        }

        StringBuffer postfix = new StringBuffer(" ) AS TB) AS T\n" +
                " WHERE T.RN BETWEEN ?*(?-1)+1 AND ?*?");
        sql = prefix.append(sql).append(postfix);
        paramsList.add(Integer.parseInt(jsonObject.get(pageSize).toString()));
        paramsList.add(Integer.parseInt(jsonObject.get(pageNum).toString()));
        paramsList.add(Integer.parseInt(jsonObject.get(pageSize).toString()));
        paramsList.add(Integer.parseInt(jsonObject.get(pageNum).toString()));
        return sql;
    }


}
