package com.easipass.epia.service;

import com.carrotsearch.sizeof.RamUsageEstimator;
import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.SysProperties;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.db.DBFactory;
import com.easipass.epia.db.DBService;
import com.easipass.epia.db.DataSourceConfig;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.interfaces.IXmlBusiService;
import com.easipass.epia.util.ExceptionUtil;
import com.easipass.epia.util.FileUtil;
import com.easipass.epia.util.ResponseResult;
import com.easipass.epia.util.StringHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

/**
 * Created by lql on 2018/12/26 11:43
 **/
@Service
public class XmlBusiService implements IXmlBusiService {
    private static Logger logger = LoggerFactory.getLogger(XmlBusiService.class);

    @Autowired
    private XmlBusiConfigContainer xmlBusiConfigContainer;

    @Override
    public ResponseResult exec(String jsonparam, Map<String, List<Map<String, byte[]>>> fileMap) {
        /**
         * 返回对象
         */
        ResponseResult rs = new ResponseResult();
        /**
         * 返回对象的结果
         */
        Map<String, Map<String, Object>> resultObject = new HashMap();
        /**
         * 执行业务操作
         */
        // 解析出functions
        JSONObject paramsObject = JSONObject.fromObject(jsonparam);
        JSONArray functionArray = paramsObject.getJSONArray("Functions");
        // 是否多数据源
//        boolean ismutiDataSource = isMutiDataSource(functionArray);
        // 该次请求都是一个数据源
        String moduleName = null;
        String functionName = null;
        String dataSourceName = null;
//        DataSource dataSourceObject = null;
        DBService dpService = null;
        XmlBusiConfig bsconfig = null;
        Calendar beginTime = Calendar.getInstance();
        try {
            try {
                for (int i = 0; i < functionArray.size(); i++) {
                    JSONObject functionObject = functionArray.getJSONObject(i);
                    // 通过模块名和功能名从配置字典中取出业务配置类
                    moduleName = functionObject.getString("ModuleName");
                    functionName = functionObject.getString("FunctionName");
                    JSONObject parameters = functionObject.getJSONObject("Parameters");
                    String key = moduleName + "-" + functionName;

                    /**
                     * 开发环境重新加载xml业务配置文件
                     */
                    if (!"true".equalsIgnoreCase(SysProperties.getIsProduction()))
                        reloadFromDisk(key);
                    /**
                     * 生产环境从内存中加载
                     */
                    if ("true".equalsIgnoreCase(SysProperties.getIsProduction()) && !XmlBusiConfigContainer.configDic.containsKey(key)) {
                        throw new UnsupportedOperationException("请求不存在的服务接口" + key);
                    }
                    logger.debug("请求路径:" + moduleName + "-" + functionName + ".线程-" + Thread.currentThread().getId());

                    //拷贝业务配置文件对象
                    bsconfig = XmlBusiConfigContainer.configDic.get(key).myClone();

                    /**
                     * 第一次执行,初始化数据操作服务,启动事务
                     */
                    if (dpService == null) {
                        dataSourceName = bsconfig.getDataSource();
                        if (!StringHelper.isNotNull(dataSourceName))
                            throw new UnsupportedOperationException("接口" + key + "的dataSourceName配置为空！");
                        dpService = DBFactory.createDBService(XmlBusiConfigContainer.ctx, dataSourceName);
                        dpService.startTransaction();
                    }
                    /**
                     * 其次判断是否和上次的数据源相同,
                     *  如果相同则继续在之前的事务中执行,
                     *  如果不同则提交当前事务,重新创建事务管理器,开启事务,执行操作
                     */
                    else {
                        if (!bsconfig.getDataSource().equals(dataSourceName)) {
                            dpService.commit();
                            dataSourceName = bsconfig.getDataSource();
                            dpService = DBFactory.createDBService(XmlBusiConfigContainer.ctx, dataSourceName);
                            dpService.startTransaction();
                        }
                    }
                    /**
                     * 设置数据源操作类
                     */
                    bsconfig.setDBService(dpService);
                    /**
                     * 执行业务配置文件
                     */
                    Map<String, Object> result = bsconfig.exec(parameters, fileMap);
                    dpService = bsconfig.getDBService();

                    resultObject.put(key, result);
                    rs.setStatusCode(ResponseResult.RESULT_STATUS_CODE_SUCCESS);
                    rs.setMsg("业务执行成功！");
                    Calendar endTime = Calendar.getInstance();
                    logger.debug(key + "耗时：" + (endTime.getTimeInMillis() - beginTime.getTimeInMillis()) + "ms");
                }
                /**
                 * 整个业务操作集合执行完毕,提交事务
                 */
                if (dpService == null)
                    throw new UnsupportedOperationException(jsonparam + "传参错误，导致无法获取数据操作类！");
                dpService.commit();
            } catch (UserException e) {
                logger.error(moduleName + "-" + functionName + "的" + e.getId() + "业务执行异常，异常编码:" + e.getErrorId() + ",操作撤销！", e.getMessage());
                rs.setStatusCode(e.getErrorId());
                rs.setMsg(e.getMessage());
                if (null != bsconfig)
                    dpService = bsconfig.getDBService();
                if (null != dpService)
                    dpService.rollback();
            }
        } catch (Exception ex) {
            //执行失败，回滚事务
            ex.printStackTrace();
            logger.error(moduleName + "-" + functionName + "业务执行异常,操作撤销！", ExceptionUtil.getErrorInfoFromException(ex));
            rs.setStatusCode(ResponseResult.RESULT_STATUS_CODE_ERROR);
            rs.setMsg(moduleName + "-" + functionName + "业务执行时发生错误！" + ExceptionUtil.getErrorInfoFromException(ex));
            /**
             * 执行过程中出现异常,回滚事务
             */
            if (null != bsconfig)
                dpService = bsconfig.getDBService();
            if (null != dpService)
                dpService.rollback();
        }
        rs.setResult(resultObject);
        /**
         * 释放缓存资源
         */
        release(jsonparam, fileMap);
        return rs;
    }

    /**
     * 释放资源
     *
     * @param jsonparams
     * @param fileMap
     */
    private void release(String jsonparams, Map<String, List<Map<String, byte[]>>> fileMap) {
        jsonparams = null;
        if (null != fileMap)
            fileMap.clear();
        fileMap = null;
    }

    /**
     * 查看当前请求是否包含多个数据源
     *
     * @param functionArray
     * @return
     */

    private boolean isMutiDataSource(JSONArray functionArray) {
        List<String> dataSourceList = new ArrayList<>();
        Map<String, Object> mutiDic = new HashMap();
        for (int i = 0; i < functionArray.size(); i++) {
            JSONObject functionObject = functionArray.getJSONObject(i);
            String moduleName = functionObject.getString("ModuleName");
            String functionName = functionObject.getString("FunctionName");
            String key = moduleName + "-" + functionName;
            XmlBusiConfig bsconfig = XmlBusiConfigContainer.configDic.get(key);
            /**
             * 取到所有的数据源
             */
            getAllDataSourceName(bsconfig, dataSourceList, mutiDic);
        }
        String firstDataSource = null;
        for (int i = 0; i < dataSourceList.size(); i++) {
            String dataSource = dataSourceList.get(i);
            if (firstDataSource == null)
                firstDataSource = dataSource;
            else if (!dataSource.equals(firstDataSource))
                return true;
        }
        return false;
    }

    /**
     * 从busiconfig 中取得所有的数据源
     *
     * @param xmlBusiConfig
     * @return
     */
    public void getAllDataSourceName(XmlBusiConfig xmlBusiConfig, List<String> dataSourceList, Map<String, Object> mutiDic) {
        dataSourceList.add(xmlBusiConfig.getDataSource());
        if (mutiDic.containsKey(xmlBusiConfig.getId()))
            throw new IndexOutOfBoundsException("存在循环调用，请检查业务配置文件的引用关系!");
        mutiDic.put(xmlBusiConfig.getId(), xmlBusiConfig);
        List<Action> actionList = xmlBusiConfig.getCmdList();
        for (int i = 0; i < actionList.size(); i++) {
            Action action = actionList.get(i);
//            if (action.getCmdType().equals(Action.IMPORT)) {
//                String target = action.getSource();
//                XmlBusiConfig bsconfig = XmlBusiConfigContainer.configDic.get(target);
//                getAllDataSourceName(bsconfig, dataSourceList, mutiDic);
//            }
        }
    }

    /**
     * 重新加载业务配置文件
     */
    public void reloadBusiConfig(String key, DBService dpService) throws Exception {
        /**
         * 开发环境从磁盘加载该业务文件到内存
         */
        if (!"true".equalsIgnoreCase(SysProperties.getIsProduction())) {
            reloadFromDisk(key);
        } else {
            /**
             * 生产环境从数据库中加载业务文件到内存
             */
            reloadFromDb(key, dpService);
        }
    }

    /**
     * 自身数据源加载业务文件
     *
     * @param key
     * @throws Exception
     */
    public void reloadFunction(String key) throws Exception {
        DBService dpService = DBFactory.createDBService(XmlBusiConfigContainer.ctx, DataSourceConfig.DEFAULT_DATASOURCE);
        try {
            dpService.startTransaction();
            reloadBusiConfig(key, dpService);
        } catch (Exception ex) {
            dpService.rollback();
            throw ex;
        }
    }

    /**
     * 从磁盘加载
     *
     * @param key
     * @throws Exception
     */
    public void reloadFromDisk(String key) throws Exception {
        String path = key.replace("-", File.separator);
        String sysBaseDir = SysProperties.getSysBaseDir();
        String root = sysBaseDir.substring(0, sysBaseDir.lastIndexOf(File.separator));
        path = root + File.separator + path + ".xml";
        File file = new File(path);
        if (file.exists())
            xmlBusiConfigContainer.resetXmlBusiConfig(key, file);
        else
            throw new UnsupportedOperationException("目录" + root + "下不存在请求的服务接口" + key);
    }

    /**
     * 根据接口key 获取xml配置文件路径
     *
     * @return
     */
    public static File getXmlConfigFileByKey(String key) {
        String path = key.replace("-", File.separator);
        String sysBaseDir = SysProperties.getSysBaseDir();
        String root = sysBaseDir.substring(0, sysBaseDir.lastIndexOf(File.separator));
        path = root + File.separator + path + ".xml";
        File file = new File(path);
        if (file.exists())
            return file;
        else
            throw new UnsupportedOperationException("目录" + root + "下不存在请求的服务接口" + key);
    }

    /**
     * 从库中加载(将二进制转换为文件，通过文件加载)
     * 临时文件-->文件加载-->移除临时文件
     *
     * @param key
     * @param dpService
     * @throws Exception
     */
    private void reloadFromDb(String key, DBService dpService) throws Exception {
        List<Map<String, Object>> list = xmlBusiConfigContainer.getFunctionByUrl(key, dpService);
        if (list.size() == 1) {
            Map<String, Object> objectMap = list.get(0);
            byte[] buf = (byte[]) objectMap.get("SOURCE");
            BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(buf));
            String filepath = SysProperties.getAttachmentPath() + File.separator + StringHelper.getUUID();
            xmlBusiConfigContainer.writeToLocal(filepath, inputStream);
            File file = new File(filepath);
            xmlBusiConfigContainer.resetXmlBusiConfig(key, file);
            xmlBusiConfigContainer.removeFile(filepath);
        } else if (list.size() == 0) {
            throw new UnsupportedOperationException("请求不存在的接口:" + key);
        } else {
            throw new UnsupportedOperationException("数据库中找到" + key + "冗余记录");
        }
    }
}
