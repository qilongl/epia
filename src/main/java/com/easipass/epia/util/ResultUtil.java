package com.easipass.epia.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;

import java.util.*;

/**
 * Created by lql on 2017/7/28.
 */
public class ResultUtil {
    public static final String ID = "ID";
    public static final String CODE = "CODE";
    public static final String PID = "PID";
    public static final String CHILDREN = "children";

    /**
     * 把List<Map<String,String>> 格式的数据:
     * --------------------------------------------------
     * ID-------NAME----------PID
     * --------------------------------------------------
     * 1        jack          ''
     * 2        richard        1
     * 3        jackson        2
     * 4        nook           1
     * --------------------------------------------------
     * 转成 tree格式
     * --------------------------------------------------
     * [
     * {
     * id:1
     * value:1
     * label:jack
     * children:[
     * {
     * id:2,
     * value:2,
     * label:richard,
     * children:[{id:3,value:3,label:jackson,children:[]}]
     * },
     * {
     * id:4,value:4,label:nook,children:[]
     * }
     * ]
     * }
     * ]
     *
     * @param list
     */
    public static JSONArray change2TreeFromPid(List<Map<String, Object>> list) {
        JSONArray treeArray = new JSONArray();
        /**
         * 通过递归方式查找上级节点
         */
//        getChildren("",list,treeArray);
        /**
         * 通过建立索引方式组建整个tree
         */
        getChildrenBySetKey(list, treeArray);
        //TODO 两种构建方式,性能待测试

        return treeArray;
    }

    /**
     * 通过建立索引来实现父节点和子节点关联
     *
     * @param list
     * @param treeArray
     * @return
     */
    public static JSONArray getChildrenBySetKey(List<Map<String, Object>> list, JSONArray treeArray) {
        //1.建立索引
        Map<String, JSONObject> dic = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
//            String pid = map.get("PID").toString();
//            changeMapKeyToLowerCase(map);
            if (!map.containsKey(PID))
                throw new UnsupportedOperationException("缺少必须的列" + PID);
            if (!map.containsKey(ID))
                throw new UnsupportedOperationException("缺少必须的列" + ID);
            String id = StringHelper.toString(map.get(ID));
//            String name = map.get("NAME").toString();
//            String value = map.containsKey("VALUE") ? map.get("VALUE").toString() : "";
//            String flag = map.containsKey("FLAG") ? map.get("FLAG").toString() : "";
//            String order = map.containsKey("ORDER") ? map.get("ORDER").toString() : "";
//            String storage = map.containsKey("STORAGE") ? map.get("STORAGE").toString() : "";
            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("id", id);
            jsonObject.putAll(map);
//            jsonObject.put("value", value);
//            jsonObject.put("label", name);
//            jsonObject.put("flag", flag);
//            jsonObject.put("order", order);
//            jsonObject.put("storage", storage);
            jsonObject.put(CHILDREN, new JSONArray());
            dic.put(id, jsonObject);
        }
        //2.构建整个树形
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            String pid = StringHelper.toString(map.get(PID));
            String id = StringHelper.toString(map.get(ID));
            JSONObject obj = dic.get(id);
            if ("".equals(pid) || !dic.containsKey(pid)) {
                treeArray.add(dic.get(id));
            } else {
                JSONObject pobj = dic.get(pid);
                pobj.getJSONArray(CHILDREN).add(obj);
            }
        }
        return treeArray;
    }

    /**
     * 把List<Map<String,String>> 格式的数据:
     * --------------------------------------------------
     * ID-------NAME----------CODE
     * --------------------------------------------------
     * 1        jack          10
     * 2        richard       1010
     * 3        jackson       1011
     * 4        nook          20
     * --------------------------------------------------
     * 转成 tree格式
     * --------------------------------------------------
     * [
     * {
     * id:1
     * value:10
     * label:jack
     * children:[
     * {
     * id:2,
     * value:1010,
     * label:richard,
     * children:[]
     * },
     * {
     * id:3,value:1011,label:jackson,children:[]
     * }
     * ]
     * },
     * {
     * id:4,
     * value:20,
     * label:nook,
     * children:[]
     * }
     * ]
     *
     * @param list
     */
    public static JSONArray change2TreeFromCode(List<Map<String, Object>> list) {
        JSONArray treeArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
//            String id = StringHelper.toString(map.get("ID"));
//            String name = map.get("NAME") + "";
//            changeMapKeyToLowerCase(map);
            if (!map.containsKey(CODE))
                throw new UnsupportedOperationException("缺少必须的列" + CODE);
            String code = StringHelper.toString(map.get(CODE));
//            String flag = map.containsKey("FLAG") ? map.get("FLAG").toString() : "";
//            String order = map.containsKey("ORDER") ? map.get("ORDER").toString() : "";
//            String storage = map.containsKey("STORAGE") ? map.get("STORAGE").toString() : "";
            JSONObject distantNode = getDistantNode(treeArray, code);
            /**
             * 自己就是根节点
             */
            if (distantNode == null) {
                JSONObject selfNode = new JSONObject();
                selfNode.putAll(map);
//                selfNode.put("id", id);
//                selfNode.put("value", code);
//                selfNode.put("label", name);
//                selfNode.put("flag", flag);
//                selfNode.put("order", order);
//                selfNode.put("storage", storage);
                selfNode.put(CHILDREN, new JSONArray());
                treeArray.add(selfNode);
                continue;
            }
            /**
             * 自己不是根节点
             */
            else {
                JSONObject closeNode = getCloserNodeCode(code, distantNode);
                JSONArray jsonArray = closeNode.getJSONArray(CHILDREN);
                JSONObject selfNode = new JSONObject();
                selfNode.putAll(map);
//                selfNode.put("id", id);
//                selfNode.put("value", code);
//                selfNode.put("label", name);
//                selfNode.put("flag", flag);
//                selfNode.put("order", order);
//                selfNode.put("storage", storage);
                selfNode.put(CHILDREN, new JSONArray());
                jsonArray.add(selfNode);
            }
            list.remove(i);
            i--;
        }
        return treeArray;
    }


    /**
     * 找到自己的根
     *
     * @param jsonArray
     * @param code
     * @return
     */
    private static JSONObject getDistantNode(JSONArray jsonArray, String code) {
        JSONObject rootNode = null;
        String distantPCode = "";
        for (int i = 1; i < code.length(); i++) {
            distantPCode = code.substring(0, i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                if (jsonObject.getString(CODE).equals(distantPCode)) {
                    rootNode = jsonObject;
                    break;
                }
            }
        }
        return rootNode;
    }

    /**
     * 找到根上最近的父节点
     *
     * @param distantNode
     * @return
     */
    private static JSONObject getCloserNodeCode(String code, JSONObject distantNode) {
        JSONObject lastNode = distantNode;
        int startIndex = distantNode.getString(CODE).length();
        for (int i = startIndex; i < code.length(); i++) {
            String genCode = code.substring(0, i);
            JSONArray jsonArray = lastNode.getJSONArray(CHILDREN);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                if (jsonObject.getString(CODE).equals(genCode)) {
                    lastNode = jsonObject;
                }
            }
        }
        return lastNode;
    }

//    /**
//     * 把map中所有的key转成小写
//     *
//     * @param map
//     */
//    private static void changeMapKeyToLowerCase(Map<String, Object> map) {
//        List<Object> keys = new ArrayList<>();
//        Iterator iterator = map.keySet().iterator();
//        while (iterator.hasNext()) {
//            String key = iterator.next().toString();
//            String value = StringHelper.toString(map.get(key));
//            keys.add(key);
//            map.put(key.toLowerCase(), value);
//        }
//        for (int i = 0; i < keys.size(); i++) {
//            map.remove(keys.get(i));
//        }
//    }
//
//    /**
//     * 取出给定的节点下所有的子节点
//     *
//     * @param pid
//     * @param list
//     * @param jsonArray
//     */
//    private static void getChildren(String pid, List<Map<String, Object>> list, com.alibaba.fastjson.JSONArray jsonArray) {
//        /**
//         * 取出当前层所有的子节点存入jsonArray 中
//         */
//        for (int i = 0; i < list.size(); i++) {
//            Map<String, Object> map = list.get(i);
//            String mpid = map.get("PID").toString();
//            if (pid.equalsIgnoreCase(mpid)) {
//                String id = map.get("ID").toString();
//                String name = map.get("NAME").toString();
//                String flag = map.containsKey("FLAG") ? map.get("FLAG").toString() : "";
//
//                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
//                jsonObject.put("id", id);
//                jsonObject.put("value", id);
//                jsonObject.put("label", name);
//                jsonObject.put("flag", flag);
//
//                jsonObject.put("children", new com.alibaba.fastjson.JSONArray());
//                jsonArray.add(jsonObject);
//                list.remove(i);
//                i--;
//            }
//        }
//        /**
//         * 取出jsonArray 中所有节点的子节点
//         */
//        for (int i = 0; i < jsonArray.size(); i++) {
//            com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
//            String jpid = jsonObject.getString("id");
//            com.alibaba.fastjson.JSONArray jarray = jsonObject.getJSONArray("children");
//            getChildren(jpid, list, jsonArray);
//        }
//    }

    public static void main(String args[]) {
        /**
         * code-tree 测试
         */
//        List<Map<String, Object>> list = new ArrayList<>();
//        Map<String, Object> map = new HashedMap();
//        map.put("ID", "2");
//        map.put("NAME", "jack");
//        map.put("CODE", "50003");
//        map.put("FLAG", "1");
//        list.add(map);
//        Map<String, Object> map1 = new HashedMap();
//        map1.put("ID", "1");
//        map1.put("NAME", "nook");
//        map1.put("CODE", "50005");
//        map1.put("FLAG", "1");
//        list.add(map1);
//        Map<String, Object> map2 = new HashedMap();
//        map2.put("ID", "3");
//        map2.put("NAME", "ruk");
//        map2.put("CODE", "5005");
//        map2.put("FLAG", "1");
//        list.add(map2);
//        Map<String, Object> map3 = new HashedMap();
//        map3.put("ID", "4");
//        map3.put("NAME", "hike");
//        map3.put("CODE", "2020");
//        list.add(map3);
//        Map<String, Object> map4 = new HashedMap();
//        map4.put("ID", "5");
//        map4.put("NAME", "richard");
//        map4.put("CODE", "2030");
//        list.add(map4);
//        Map<String, Object> map5 = new HashedMap();
//        map5.put("ID", "6");
//        map5.put("NAME", "lin");
//        map5.put("CODE", "3010");
//        list.add(map5);
//        Map<String, Object> map6 = new HashedMap();
//        map6.put("ID", "7");
//        map6.put("NAME", "lin");
//        map6.put("CODE", "202020");
//        list.add(map6);
//        Map<String, Object> map7 = new HashedMap();
//        map7.put("ID", "8");
//        map7.put("NAME", "lin");
//        map7.put("CODE", "202021");
//        list.add(map7);
//        Map<String, Object> map8 = new HashedMap();
//        map8.put("ID", "9");
//        map8.put("NAME", "lin");
//        map8.put("CODE", "301010");
//        list.add(map8);
//        Map<String, Object> map9 = new HashedMap();
//        map9.put("ID", "10");
//        map9.put("NAME", "lin");
//        map9.put("CODE", "30101110");
//        list.add(map9);
//        Map<String, Object> map10 = new HashedMap();
//        map10.put("ID", "11");
//        map10.put("NAME", "lin");
//        map10.put("CODE", "301011101");
//        list.add(map10);
//        Collections.sort(list, new Comparator<Map<String, Object>>() {
//            @Override
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                double d1 = Double.parseDouble(o1.get("CODE").toString());
//                double d2 = Double.parseDouble(o2.get("CODE").toString());
//                if (d1 - d2 > 0)
//                    return 1;
//                else if (d1 - d2 == 0)
//                    return 0;
//                else
//                    return -1;
//            }
//        });
//        System.out.println(Calendar.getInstance().getTime().getTime());
//        change2TreeFromCode(list);
//        System.out.println(Calendar.getInstance().getTime().getTime());
//        System.out.println(list.size());
        /**
         * pid-tree 测试
         */
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashedMap();
        map.put("ID", "1");
        map.put("NAME", "jack");
        map.put("PID", "");
        list.add(map);
        Map<String, Object> map1 = new HashedMap();
        map1.put("ID", "2");
        map1.put("NAME", "nook");
        map1.put("PID", "1");
        list.add(map1);
        Map<String, Object> map2 = new HashedMap();
        map2.put("ID", "3");
        map2.put("NAME", "ruk");
        map2.put("PID", "2");
        list.add(map2);
        Map<String, Object> map3 = new HashedMap();
        map3.put("ID", "4");
        map3.put("NAME", "hike");
        map3.put("PID", "1");
        list.add(map3);
        Map<String, Object> map4 = new HashedMap();
        map4.put("ID", "5");
        map4.put("NAME", "richard");
        map4.put("PID", "3");
        list.add(map4);
        Map<String, Object> map5 = new HashedMap();
        map5.put("ID", "6");
        map5.put("NAME", "lin");
        map5.put("PID", "");
        list.add(map5);
        Map<String, Object> map6 = new HashedMap();
        map6.put("ID", "7");
        map6.put("NAME", "lin");
        map6.put("PID", "6");
        list.add(map6);
        Map<String, Object> map7 = new HashedMap();
        map7.put("ID", "8");
        map7.put("NAME", "lin");
        map7.put("PID", "7");
        list.add(map7);
        Map<String, Object> map8 = new HashedMap();
        map8.put("ID", "9");
        map8.put("NAME", "lin");
        map8.put("PID", "3");
        list.add(map8);
        Map<String, Object> map9 = new HashedMap();
        map9.put("ID", "9");
        map9.put("NAME", "lin");
        map9.put("PID", "3");
        list.add(map9);
        Map<String, Object> map10 = new HashedMap();
        map10.put("ID", "10");
        map10.put("NAME", "lin");
        map10.put("PID", "3");
        list.add(map10);
        Map<String, Object> map11 = new HashedMap();
        map11.put("ID", "111");
        map11.put("NAME", "lin");
        map11.put("PID", "3");
        list.add(map11);
        Map<String, Object> map12 = new HashedMap();
        map12.put("ID", "12");
        map12.put("NAME", "lin");
        map12.put("PID", "3");
        list.add(map12);
        Map<String, Object> map13 = new HashedMap();
        map13.put("ID", "13");
        map13.put("NAME", "lin");
        map13.put("PID", "3");
        list.add(map13);
        Map<String, Object> map14 = new HashedMap();
        map14.put("ID", "14");
        map14.put("NAME", "lin");
        map14.put("PID", "3");
        list.add(map14);
        Map<String, Object> map15 = new HashedMap();
        map15.put("ID", "15");
        map15.put("NAME", "lin");
        map15.put("PID", "3");
        list.add(map15);
        Map<String, Object> map16 = new HashedMap();
        map16.put("ID", "16");
        map16.put("NAME", "lin");
        map16.put("PID", "3");
        list.add(map16);
        Map<String, Object> map17 = new HashedMap();
        map17.put("ID", "17");
        map17.put("NAME", "lin");
        map17.put("PID", "3");
        list.add(map17);

        Map<String, Object> map18 = new HashedMap();
        map18.put("ID", "18");
        map18.put("NAME", "lin");
        map18.put("PID", "4");
        list.add(map18);
        Map<String, Object> map19 = new HashedMap();
        map19.put("ID", "19");
        map19.put("NAME", "lin");
        map19.put("PID", "5");
        list.add(map19);
        Map<String, Object> map20 = new HashedMap();
        map20.put("ID", "20");
        map20.put("NAME", "lin");
        map20.put("PID", "6");
        list.add(map17);
        Map<String, Object> map21 = new HashedMap();
        map21.put("ID", "21");
        map21.put("NAME", "lin");
        map21.put("PID", "7");
        list.add(map17);
        Map<String, Object> map22 = new HashedMap();
        map22.put("ID", "22");
        map22.put("NAME", "lin");
        map22.put("PID", "8");
        list.add(map17);
        Map<String, Object> map23 = new HashedMap();
        map23.put("ID", "23");
        map23.put("NAME", "lin");
        map23.put("PID", "9");
        list.add(map17);
        Map<String, Object> map24 = new HashedMap();
        map24.put("ID", "24");
        map24.put("NAME", "lin");
        map24.put("PID", "10");
        list.add(map17);
        Map<String, Object> map25 = new HashedMap();
        map25.put("ID", "25");
        map25.put("NAME", "lin");
        map25.put("PID", "11");
        list.add(map17);
        Map<String, Object> map26 = new HashedMap();
        map26.put("ID", "26");
        map26.put("NAME", "lin");
        map26.put("PID", "12");
        list.add(map17);
        Map<String, Object> map27 = new HashedMap();
        map27.put("ID", "27");
        map27.put("NAME", "lin");
        map27.put("PID", "13");
        list.add(map17);


        System.out.println(Calendar.getInstance().getTime().getTime());
        System.out.println(JSON.toJSONString(change2TreeFromPid(list)));
        System.out.println(Calendar.getInstance().getTime().getTime());
        System.out.println(list.size());

    }
}
