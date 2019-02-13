package com.easipass.epia.interfaces;

import com.easipass.epia.beans.Action;
import com.easipass.epia.db.DBService;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 转换器接口
 * Created by lql on 2019/1/4 16:01
 * 可根据具体业务需求，对结果集进行处理，实现转换器接口
 */
public interface IConverter {

    /**
     * @param cmd        操作类实体
     * @param jsonObject 请求中入参json
     * @param fileMap    请求中的附件字节流
     * @param dataSet    入参数据集(convert转换类方法的入参)
     * @param dpService  数据源服务
     * @return
     * @throws Exception
     */
    Object exec(Action cmd, JSONObject jsonObject, Map<String, List<Map<String, byte[]>>> fileMap, Object dataSet, DBService dpService) throws Exception;
}
