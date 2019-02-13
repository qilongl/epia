package com.easipass.epia.converter;

import com.alibaba.fastjson.JSON;
import com.easipass.epia.beans.Action;
import com.easipass.epia.db.DBService;
import com.easipass.epia.interfaces.IConverter;
import com.easipass.epia.util.JsonValueFilter;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2019/1/4 16:06
 * 字符串对象转换为List<Map>
 **/
public class StringObjectToListMapConverter implements IConverter {
    private static Logger logger = LoggerFactory.getLogger(StringObjectToListMapConverter.class);

    @Override
    public Object exec(Action cmd, JSONObject jsonObject, Map<String, List<Map<String, byte[]>>> fileMap, Object dataSet, DBService dpService) {
        logger.debug("可用参数为:" + JSON.toJSONString(jsonObject));
        //取到入参数据集
        String content = (String) dataSet;
        /**
         * 取到结果集字符串
         */
        com.alibaba.fastjson.JSONObject jsonObj;
        com.alibaba.fastjson.JSONArray jsonArray;
        List<Map> list = new ArrayList<>();
        int jsonType = JsonValueFilter.getJsonType(content);
        /**
         * 转换成json对象,然后存放到List<map>集合中
         */
        if (jsonType == JsonValueFilter.JSON_OBJECT) {
            jsonObj = JSON.parseObject(content);
            list.add(jsonObj);
        } else if (jsonType == JsonValueFilter.JSON_ARRAY) {
            jsonArray = JSON.parseArray(content);
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                list.add(jsonObj);
            }
        } else {
            throw new UnsupportedOperationException(content + "json格式错误!");
        }
        return list;
    }

    public Object test(Action cmd, JSONObject jsonObject, Map<String, List<Map<String, byte[]>>> fileMap, Object dataSet, DBService dpService) {
        String content = cmd.getContent();
        List<Map> list = new ArrayList<>();
        Map map = new HashMap();
        map.put(cmd.getCmdType(), content);
        list.add(map);
        return list;
    }
}
