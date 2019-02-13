package com.easipass.epia.service.actions;

import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.util.StringHelper;
import com.easipass.epia.util.TypeUtil;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2019/1/8 15:03
 **/
public class ErrorAction implements Serializable {
    private XmlBusiConfig xmlBusiConfig;

    public ErrorAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }

    public Object errorCommand(Action cmd, JSONObject inParams) throws Exception {
        String msg = cmd.getMsg();
        try {
            if (msg.contains("#{")) {
                String tag = msg.substring(msg.indexOf("#{") + 2, msg.indexOf("}"));
                String obj = tag.substring(0, tag.indexOf("."));
                String prop = tag.substring(tag.indexOf(".") + 1);
                List<Map<String, Object>> result = TypeUtil.changeToListMap(xmlBusiConfig.getParamsFromInParams_CmdResult(obj, inParams));
                if (result.size() == 0) {
                    throw new UnsupportedOperationException("配置的参数来源结果集:" + obj + "内容为空！");
                }
                Map<String, Object> map = result.get(0);
                String content = StringHelper.toString(map.get(prop));
                msg = msg.replace("#{" + tag + "}", content);
            }
        } catch (Exception ex) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), "", ex);
        }
        throw new UserException(cmd.getId(), cmd.getErrorid(), msg, null);
    }
}
