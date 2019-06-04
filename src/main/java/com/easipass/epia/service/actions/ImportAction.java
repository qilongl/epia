package com.easipass.epia.service.actions;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.SysProperties;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.db.DBService;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.service.XmlBusiConfigContainer;
import com.easipass.epia.service.XmlBusiService;
import com.easipass.epia.util.TypeUtil;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2019/1/4 17:34
 **/
public class ImportAction implements Serializable {
    private XmlBusiConfig xmlBusiConfig;
    private Logger logger = LoggerFactory.getLogger(ImportAction.class);

    public ImportAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }

//    public XmlBusiService xmlBusiService = new XmlBusiService();

    /**
     * 引入其他的业务配置文件
     *
     * @param cmd
     * @param inParam
     * @return
     * @throws Exception
     */
    public void importCommand(Action cmd, JSONObject inParam, Map<String, List<Map<String, byte[]>>> fileMap, Map<String, Object> result, DBService dpService) throws Exception {
        try {
            String source = "";
            String content = cmd.getContent();
            Document document = DocumentHelper.parseText(content);
            Element rootElement = document.getRootElement();
            source = rootElement.attributeValue("source");//引用的目标

            /**
             * 开发环境重新加载被引用xml业务配置文件
             */
            if (!"true".equalsIgnoreCase(SysProperties.getIsProduction()))
                new XmlBusiConfigContainer().resetXmlBusiConfig(source, XmlBusiService.getXmlConfigFileByKey(source));
            /**
             * 生产环境从内存中加载
             */
            if ("true".equalsIgnoreCase(SysProperties.getIsProduction()) && !XmlBusiConfigContainer.configDic.containsKey(source)) {
                throw new UnsupportedOperationException("请求不存在的服务接口" + source);
            }

//            if (!XmlBusiConfigContainer.configDic.containsKey(source))
//                throw new UnsupportedOperationException("内存中不存在要应用的" + source + ".xml业务文件!");
            XmlBusiConfig sourceBusiConfig = XmlBusiConfigContainer.configDic.get(source).myClone();
            JSONObject jsonObject = new JSONObject();// 构建输入参数
            List<Element> elementList = rootElement.elements();
            for (int i = 0; i < elementList.size(); i++) {
                Element element = elementList.get(i);
                String targetParamName = element.getName();// 引用对象的入参名称
                String inParamName = element.attributeValue("params");// 传入的参数名称
                // 取到要传入的参数集合,构建入参
                List<Map<String, Object>> params = TypeUtil.changeToListMap(xmlBusiConfig.getParamsFromInParams_CmdResult(inParamName, inParam));
                com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(JSON.toJSONString(params));
                jsonObject.put(targetParamName, jsonArray);
            }
            String sourceBusiconfigDataSourceName = sourceBusiConfig.getDataSource();
            if (!sourceBusiconfigDataSourceName.equals(((DruidDataSource) dpService.getDataSource()).getName())) {
                throw new UnsupportedOperationException("import标签不支持引入不同数据源的业务文件!");
            }
            /**
             * 设置数据操作类
             */
            sourceBusiConfig.setDBService(dpService);
            Map<String, Object> resultMap = sourceBusiConfig.exec(jsonObject, fileMap);// 调用
            String repeatedKey = hasRepeatedKey(xmlBusiConfig.getCmdList(), resultMap);
            if (repeatedKey != null)
                throw new UnsupportedOperationException("被import的业务文件" + cmd.getSource() + ".xml中包含有与主" + "-" + xmlBusiConfig.getId() + ".xml中重复的id定义:" + repeatedKey);
            result.putAll(resultMap);
        } catch (UserException ex) {
            throw new UserException(ex.getId(), ex.getErrorId(), "", ex);
        }
    }

    /**
     * 检查两个result 中是否包含重复的key
     *
     * @param cmdList
     * @param map2
     * @return
     */
    private String hasRepeatedKey(List<Action> cmdList, Map map2) {
        for (int i = 0; i < cmdList.size(); i++) {
            Action cmd = cmdList.get(i);
            if (map2.containsKey(cmd.getId()))
                return cmd.getId();
        }
        return null;
    }
}
