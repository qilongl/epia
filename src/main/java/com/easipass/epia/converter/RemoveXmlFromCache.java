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
 * Created by StrangeDragon on 2019/8/27 11:08
 **/
public class RemoveXmlFromCache implements IConverter {

    @Override
    public Object exec(Action cmd, JSONObject jsonObject, Map<String, List<Map<String, byte[]>>> fileMap, Object dataSet, DBService dpService) throws Exception {
        XmlBusiService busiService = (XmlBusiService) XmlBusiConfigContainer.ctx.getBean("xmlBusiService");
        List<Map> list = (List) dataSet;
        busiService.removeXmlBusiConfigInCache(list, dpService);
        return null;
    }
}
