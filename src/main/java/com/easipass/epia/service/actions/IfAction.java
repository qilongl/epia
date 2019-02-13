package com.easipass.epia.service.actions;

import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.util.ExpressionUtil;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lql on 2019/1/8 13:14
 **/
public class IfAction implements Serializable {
    private XmlBusiConfig xmlBusiConfig;

    public IfAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }

    public boolean ifCommand(Action cmd, JSONObject inParams) throws Exception {
        try {
            String expression = cmd.getTest();
            boolean flag = ExpressionUtil.calExpression(expression, xmlBusiConfig, inParams, null);
            //表达式成立,剔除IF标签命令（添加if内部标签命令到命令列表）
            if (flag) {
                String cmdStr = cmd.getContent();
                Document document = DocumentHelper.parseText(cmdStr);
                Element rootElement = document.getRootElement();
                List<Element> elementList = rootElement.elements();
                int index = xmlBusiConfig.getCmdList().indexOf(cmd);
                xmlBusiConfig.getCmdList().remove(index);
                for (int i = 0; i < elementList.size(); i++) {
                    Element element = elementList.get(i);
                    xmlBusiConfig.getCmdList().add(index + i, new Action(element));
//
                }
                if (elementList.size() > 0)
                    return true;
            }
        } catch (Exception ex) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), "", ex);
        }
        return false;
    }
}
