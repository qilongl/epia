package com.easipass.epia.converter;

import com.easipass.epia.beans.Action;
import com.easipass.epia.db.DBService;
import com.easipass.epia.interfaces.IConverter;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by json on 2018/4/10.
 * stream 类型的二进制流转成string类型
 * 返回：List<Map<String,Object>>
 */
public class StreamToStringConverter implements IConverter {
    private final String content = "CONTENT";
    private final String source = "SOURCE";

    @Override
    public Object exec(Action cmd, JSONObject jsonObject, Map<String, List<Map<String, byte[]>>> fileMap, Object dataSet, DBService dpService) throws Exception {
        //取出stream
        List<Map> result = (List) dataSet;
        Map<String, Object> objectMap = result.get(0);
        byte[] buf = (byte[]) objectMap.get(source);
        //转成string
        SAXReader reader = new SAXReader();
        Document document = reader.read(new ByteArrayInputStream(buf));
        String xmlContent = document.getRootElement().asXML();
        List<Map<String, Object>> list = new ArrayList<>();
        Map map = new HashMap();
        map.put(content, xmlContent);
        list.add(map);
        return list;
    }

}
