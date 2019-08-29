package com.easipass.epia.converter;

import com.easipass.epia.beans.Action;
import com.easipass.epia.db.DBService;
import com.easipass.epia.interfaces.IConverter;
import com.easipass.epia.util.StringHelper;
import com.easipass.epia.util.TypeUtil;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by json on 2018/4/12.
 */
public class GetXmlFilePropertiesConverter implements IConverter {
    /***
     * 定义请求表示的属性名称
     */
    private final String reqUrl = "reqUrl";
    private final String fileName = "fileName";
    private final String savePath = "relativeSavePath";
    private final String postFix = ".xml";

    @Override
    public Object exec(Action cmd, JSONObject jsonObject, Map<String, List<Map<String, byte[]>>> fileMap, Object dataSet, DBService dpService) throws Exception {
        String relativeSavePath = "";
        List<Map<String, Object>> list = TypeUtil.changeToListMap(dataSet);
        Map<String, Object> objMap = list.get(0);
        String url = StringHelper.toString(objMap.get(reqUrl));
        //xml文件存储的相对路径
//        String relativeSavePath = url.substring(0, url.lastIndexOf("-")).replace("-", File.separator);
        int startIndex = url.indexOf("-", url.indexOf("-") + 1);
        int lastIndex = url.lastIndexOf("-");
        if (startIndex != lastIndex) {
            relativeSavePath = url.substring(startIndex + 1, lastIndex).replace("-", File.separator);
        }
        //xml文件的名称
        String xmlFileName = url.substring(lastIndex + 1, url.length()) + postFix;
        List result = new ArrayList<>();
        Map map = new HashMap<>();
        map.put(fileName, xmlFileName);
        map.put(savePath, relativeSavePath);
        result.add(map);
        return result;
    }
}
