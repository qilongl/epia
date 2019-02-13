package com.easipass.epia.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * Created by lql on 2017/9/22.
 */
public class TypeUtil {
    /**
     * 转换成List<Map<String, Object>> 返回
     *
     * @param object
     * @return
     */
    public static List<Map<String, Object>> changeToListMap(Object object) {
        if (null == object)
            return null;
        List<Map<String, Object>> list = new ArrayList<>();
        if (object instanceof JSONArray) {
            /**
             * jsonArray 转成 ArrayList
             */
            JSONArray jsonArray = (JSONArray) object;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                Iterator iterator = jsonObject.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    String value = jsonObject.getString(key);
                    map.put(key, value);
                }
                list.add(map);
            }
            return list;
        }
//        else if (object instanceof net.sf.json.JSONArray) {
//            net.sf.json.JSONArray jsonArray = (net.sf.json.JSONArray) object;
//            for (int i = 0; i < jsonArray.size(); i++) {
//                net.sf.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
//                Map<String, Object> map = new HashMap<>();
//                Iterator iterator = jsonObject.keySet().iterator();
//                while (iterator.hasNext()) {
//                    String key = iterator.next().toString();
//                    String value = jsonObject.getString(key);
//                    map.put(key, value);
//                }
//                list.add(map);
//            }
//            return list;
//        }
        else if (object instanceof JSONObject) {
            /**
             * JSONobject 转成List<Map>
             */
            JSONObject jsonObject = (JSONObject) object;
            Map<String, Object> map = new HashMap<>();
            Iterator iterator = jsonObject.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = jsonObject.getString(key);
                map.put(key, value);
            }
            list.add(map);
            return list;
        }
//        else if (object instanceof net.sf.json.JSONObject) {
//            net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) object;
//            Map<String, Object> map = new HashMap<>();
//            Iterator iterator = jsonObject.keySet().iterator();
//            while (iterator.hasNext()) {
//                String key = iterator.next().toString();
//                String value = jsonObject.getString(key);
//                map.put(key, value);
//            }
//            list.add(map);
//            return list;
//        }
        else if (object instanceof List) {
            List<Map<String, Object>> listArray = (List<Map<String, Object>>) object;
            return listArray;
        } else {
            throw new UnsupportedOperationException("当前对象" + JSON.toJSONString(object) + "不是List<Map<String,Object>>类型,无法取得期望的值!");
        }
    }

    /**
     * 判断当前对象是否是List<Map<String,Object>>类型
     *
     * @param object
     * @return
     */
    public static boolean isListMap(Object object) {
        if (null == object)
            return false;
        if (object instanceof List)
            return true;
        else
            return false;
    }


    public static void main(String[] args) throws Exception {
        JSONArray jsonArray = new JSONArray();
//        net.sf.json.JSONArray jsonArray1 = new net.sf.json.JSONArray();
        List listArray = new ArrayList<>();
        List<Map<String, Object>> list = changeToListMap(jsonArray);
//        List<Map<String, Object>> list1 = changeToListMap(jsonArray1);
        List<Map<String, Object>> list2 = changeToListMap(listArray);
        System.out.println("end");
    }
}
