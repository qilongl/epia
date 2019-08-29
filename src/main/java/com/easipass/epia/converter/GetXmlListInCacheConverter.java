package com.easipass.epia.converter;

import com.easipass.epia.beans.Action;
import com.easipass.epia.db.DBService;
import com.easipass.epia.interfaces.IConverter;
import com.easipass.epia.service.XmlBusiConfigContainer;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by StrangeDragon on 2019/8/28 16:50
 * 获取内存中的xml列表
 **/
public class GetXmlListInCacheConverter implements IConverter {
    private final String CACHE_XML = "cacheXml";
    private final String TOTAL = "total";

    @Override
    public Object exec(Action cmd, JSONObject jsonObject, Map<String, List<Map<String, byte[]>>> fileMap, Object dataSet, DBService dpService) throws Exception {
        List list = new ArrayList();
        Map map = new HashMap();
        map.put(CACHE_XML, XmlBusiConfigContainer.configDic);
        map.put(TOTAL, XmlBusiConfigContainer.configDic.size());
        list.add(map);
        return list;
    }
}
