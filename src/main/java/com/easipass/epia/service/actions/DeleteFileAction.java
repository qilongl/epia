package com.easipass.epia.service.actions;

import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.util.StringHelper;
import com.easipass.epia.util.TypeUtil;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2019/1/16 11:12
 **/
public class DeleteFileAction implements Serializable {
    public static final String PATH = "PATH";
    public static final String FLAG = "FLAG";

    private XmlBusiConfig xmlBusiConfig;

    public DeleteFileAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }

    public List<Map<String, Object>> deleteFileCommand(Action cmd, JSONObject inParams) throws UserException {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Object data = xmlBusiConfig.getParamsFromInParams_CmdResult(cmd.getParams(), inParams);
            List<Map<String, Object>> list = TypeUtil.changeToListMap(data);
            for (int i = 0; i < list.size(); i++) {
                boolean flag;
                Map<String, Object> map = list.get(i);
                String filePath = StringHelper.toString(map.get(PATH));
                try {
                    File file = new File(filePath);
                    flag = file.delete();
                } catch (Exception e) {
                    flag = false;
                }
                Map<String, Object> result1 = new HashMap<>();
                result1.put(PATH, filePath);
                result1.put(FLAG, flag);
                result.add(result1);
            }

        } catch (Exception e) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), cmd.getMsg(), e);
        }
        return result;
    }
}
