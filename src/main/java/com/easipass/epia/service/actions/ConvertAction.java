package com.easipass.epia.service.actions;

import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.db.DBService;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.util.StringHelper;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2019/1/4 16:22
 **/
public class ConvertAction implements Serializable {
    private XmlBusiConfig xmlBusiConfig;
    private String METHOD = "exec";

    public ConvertAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }

    public Object convertCommand(Action cmd, JSONObject inParams, Map<String, List<Map<String, byte[]>>> fileMap, DBService dpService) throws Exception {
        Object object;
        try {
            //创建类加载器
            Class<?> cls = Class.forName(cmd.getClasspath());//加载指定类，注意一定要带上类的包名
            Object obj = cls.newInstance();//初始化一个实例
            //不指定方法时，使用默认接口方法
            if (StringHelper.isNotNull(cmd.getMethod())) {
                METHOD = cmd.getMethod();
            }
            String params = "";
            if (StringHelper.isNotNull(cmd.getParams())) {
                params = cmd.getParams();
                Object result = xmlBusiConfig.getParamsFromInParams_CmdResult(params, inParams);
                Method method = cls.getMethod(METHOD, Action.class, JSONObject.class, Map.class, Object.class, DBService.class);
                object = method.invoke(obj, cmd, inParams, fileMap, result, dpService);
            } else {
                Method method = cls.getMethod(METHOD, Action.class, JSONObject.class, Map.class, Object.class, DBService.class);
                object = method.invoke(obj, cmd, inParams, fileMap, null, dpService);
            }
        } catch (Exception ex) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), "", ex);
        }
        return object;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Class<?> cls = Class.forName("com.easipass.epia.test.Test1");
            Object obj = cls.newInstance();
            for (Method method : cls.getMethods()) {
                System.out.println(method.getName());
            }
            Method method = cls.getMethod("test", String.class);
            Object result = method.invoke(obj, "刘奇龙");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
