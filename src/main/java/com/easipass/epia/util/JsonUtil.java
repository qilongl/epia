package com.easipass.epia.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JsonUtil {

    private static Log log = LogFactory.getLog(JsonUtil.class);

    public static <T> T jsonToBean(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz, Feature.OrderedField);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> String beanToJson(T entity) {
        try {
            String jsonString = JSON.toJSONString(entity, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.MapSortField, SerializerFeature.WriteDateUseDateFormat);
            return jsonString.equals("null") ? null : jsonString;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * JsonValueFilter.changeNullToString() 空值过滤器     值 null --->""
     *
     * @param object
     * @return
     */
    public static String changeNullToString(Object object) {
        return JSON.toJSONString(object, JsonValueFilter.changeNullToString());
    }

}
