package com.easipass.epia.converter;

import com.easipass.epia.beans.Action;
import com.easipass.epia.db.DBService;
import com.easipass.epia.interfaces.IConverter;
import com.easipass.epia.service.XmlBusiConfigContainer;
import com.easipass.epia.service.XmlBusiService;
import com.easipass.epia.util.StringHelper;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by json on 2018/3/26.
 * 重新加载指定路径的业务文件到内存
 */
public class ReloadXmlAction implements IConverter {

    @Override
    public Object exec(Action cmd, JSONObject jsonObject, Map<String, List<Map<String, byte[]>>> fileMap, Object dataSet, DBService dpService) throws Exception {
        XmlBusiService busiService = (XmlBusiService) XmlBusiConfigContainer.ctx.getBean("xmlBusiService");
        List<Map> list = (List) dataSet;
        String reqUrl = StringHelper.toString(list.get(0).get("reqUrl"));
        busiService.reloadBusiConfig(reqUrl, dpService);
        return null;
    }
}
