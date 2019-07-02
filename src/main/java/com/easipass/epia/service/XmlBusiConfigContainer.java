package com.easipass.epia.service;

import com.carrotsearch.sizeof.RamUsageEstimator;
import com.easipass.epia.beans.SysProperties;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.db.DBFactory;
import com.easipass.epia.db.DBService;
import com.easipass.epia.db.DataSourceConfig;
import com.easipass.epia.util.FileUtil;
import com.easipass.epia.util.StringHelper;
import org.dom4j.IllegalAddException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lql on 2018/12/26 10:46
 **/
@Component
@Service
public class XmlBusiConfigContainer {
    private static Logger logger = LoggerFactory.getLogger(XmlBusiConfigContainer.class);

    public static ApplicationContext ctx;

    /**
     * 存储业务配置文件的字典
     */
    public static Map<String, XmlBusiConfig> configDic = new HashMap<>();

    private Lock lock = new ReentrantLock();

    /**
     * 启动时加载所有配置文件
     *
     * @param ctx
     * @throws Exception
     */
    public void init(ApplicationContext ctx) throws Exception {
        logger.debug("\t准备初始化所有业务配置文件");
        this.ctx = ctx;
        DBService dbService = DBFactory.createDBService(ctx, DataSourceConfig.DEFAULT_DATASOURCE);
        try {
            dbService.startTransaction();
            /**
             * 1.读取磁盘所有业务文件
             */
            List<File> allFiles = new ArrayList<>();
            FileUtil.getAllFile(new File(SysProperties.getSysBaseDir()), ".xml", allFiles);
            FileUtil.getAllFile(new File(SysProperties.getUserBaseDir()), ".xml", allFiles);
            /**
             * 2.xml增量入库
             * (xml格式校验，基础属性校验)
             */
            XML2DB(allFiles, dbService);
            /**
             * 3.xml存放到内存中
             */
            XML2Che(allFiles, dbService);

            dbService.commit();
        } catch (Exception ex) {
            dbService.rollback();
            ex.printStackTrace();
            logger.error(ex.getMessage() + ",初始化业务文件失败,撤销操作!");
            configDic.clear();
        }
    }

    /**
     * 磁盘xml增量入库
     *
     * @param allFiles
     * @param dbService
     * @throws Exception
     */
    private void XML2DB(List<File> allFiles, DBService dbService) throws Exception {
        int sucNum = 0;
        int uptNum = 0;
        for (int i = 0; i < allFiles.size(); i++) {
            File file = allFiles.get(i);
            XmlBusiConfig config = new XmlBusiConfig(file);
            String id = config.getId();
            String moduleName = FileUtil.getNameKey(file.getParent() + File.separator + id, SysProperties.getSysbasedir(), SysProperties.getUserbasedir());
            config.setModuleName(moduleName);
            String reqUrl = moduleName + "-" + config.getId();
            /**
             * 业务文件写入库
             */
            List list = getFunctionByUrl(reqUrl, dbService);
            if (list.size() == 0) {
                String insertSql = "insert into sys_function(ID,OBJID,Fun_Name,Fun_Url,Crt_Dt,Crt_Psn,Upd_Dt,Upd_Psn,Is_Delete,FUN_SOURCE,IS_CONTRL_FUN,SOURCE) values(sys_id.nextval,sys_guid(),?,?,sysdate,'system',sysdate,'system',0,'XML',0,?)";
                String name = config.getName();
                FileInputStream fileInputStream = new FileInputStream(file);
                List params = new ArrayList<>();
                params.add(name);
                params.add(reqUrl);
                params.add(fileInputStream);
                dbService.updateByType(insertSql, params);
                logger.info(config.getName() + "," + reqUrl + ",增量入库 yes !");
                sucNum++;
            } else if (list.size() == 1) {
                List uptList = getUptFunctionByUrl(reqUrl, dbService);
                if (uptList.size() == 0) {
                    logger.info(config.getName() + "," + reqUrl + ",增量入库 no !");
                } else {
                    String uptSql = "update sys_function set source=?,Upd_Dt=sysdate,Upd_Psn='system' where fun_url=?";
                    FileInputStream fileInputStream = new FileInputStream(file);
                    List params = new ArrayList<>();
                    params.add(fileInputStream);
                    params.add(reqUrl);
                    dbService.updateByType(uptSql, params);
                    logger.info(config.getName() + "," + reqUrl + ",更新 yes !");
                    uptNum++;
                }
            } else {
                throw new UnsupportedOperationException("数据库中存在多个url为" + reqUrl + "的业务文件记录！");
            }
        }
        logger.info("所有业务文件增量更新入库完成..共 " + allFiles.size() + " 个业务配置文件，增量" + sucNum + " 个,更新" + uptNum + "个\n");
    }

    /**
     * xml存入内存中
     *
     * @param allFiles
     * @param dpService
     */
    private void XML2Che(List<File> allFiles, DBService dpService) throws Exception {
        logger.info("============开始业务文件加载到内存中===============\n");
        //生产环境
        if ("true".equalsIgnoreCase(SysProperties.getIsProduction())) {
            logger.info("生产环境，从库中加载.....\n");
            String sql = "select * from sys_function f where f.is_delete<>1 and f.fun_source='XML' and f.source is not null";
            List<Map<String, Object>> result = dpService.select(sql);
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> objectMap = result.get(i);
                byte[] buf = (byte[]) objectMap.get("SOURCE");
                String reqUrl = StringHelper.toString(objectMap.get("FUN_URL"));
                BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(buf));
                String filepath = SysProperties.getAttachmentPath() + File.separator + StringHelper.getUUID();
                writeToLocal(filepath, inputStream);
                File file = new File(filepath);
                XmlBusiConfig busiConfig = new XmlBusiConfig(file);
                busiConfig.setModuleName(reqUrl.substring(0, reqUrl.lastIndexOf("-")));
                putInCache(reqUrl, busiConfig);
                removeFile(filepath);
            }
            logger.info("生产环境，加载完成,共" + result.size() + "个");
        }//开发环境
        else {
            logger.info("开发环境，从磁盘加载.....\n");
            for (int i = 0; i < allFiles.size(); i++) {
                File file = allFiles.get(i);
                XmlBusiConfig config = new XmlBusiConfig(file);
                String id = config.getId();
                String moduleName = FileUtil.getNameKey(file.getParent() + File.separator + id, SysProperties.getSysbasedir(), SysProperties.getUserbasedir());
                config.setModuleName(moduleName);
                String reqUrl = moduleName + "-" + config.getId();
                putInCache(reqUrl, config);
            }
            logger.info("开发环境，加载完成,共" + allFiles.size() + "个");
        }
        logger.info("xml所占内存大小：" + RamUsageEstimator.sizeOf(configDic) + " Byte");
        logger.info("============业务文件加载到内存中，完成===============\n");
    }


    /**
     * 将InputStream写入本地文件
     *
     * @param destination 写入本地目录
     * @param input       输入流
     * @throws IOException
     */
    public void writeToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        downloadFile.close();
        input.close();
        logger.info("写入文件" + destination);
    }

    /**
     * 立即删除文件
     *
     * @param path
     */
    public void removeFile(String path) {
        File file = new File(path);
        if (file.exists())
            file.delete();
        logger.info("删除文件" + path);
    }

    /**
     * 把取到的文件放置到内存中
     *
     * @param reqUrl
     * @param xmlBusiConfig
     * @throws Exception
     */
    private void putInCache(String reqUrl, XmlBusiConfig xmlBusiConfig) throws Exception {
        if (configDic.containsKey(reqUrl))
            throw new IllegalAddException("内存中已经存在同名的业务配置文件:" + reqUrl);
        configDic.put(reqUrl, xmlBusiConfig);
        logger.info(xmlBusiConfig.getName() + "," + reqUrl + ",加载成功");
    }

    /**
     * 动态发布(多线程并发时,同步)
     *
     * @param file 重新加载的业务配置文件
     * @return
     */
    public boolean resetXmlBusiConfig(String key, File file) throws Exception {
        XmlBusiConfig config = new XmlBusiConfig(file);
        String fileName = key.substring(key.lastIndexOf("-") + 1, key.length());
        if (!config.getId().equals(fileName)) {
            throw new UnsupportedOperationException("业务配置文件id与配置文件名不一致！id：" + config.getId() + "，  配置文件名：" + fileName);
        }
        String moduleName = key.substring(0, key.lastIndexOf("-"));
        config.setModuleName(moduleName);
        //重新发布
        lock.lock();
        try {
            configDic.put(key, config);
        } finally {
            lock.unlock();
        }
        logger.info("\t重新发布业务配置文件" + key + " 成功 ");
        return true;
    }

    public static void main(String[] args) {
        String key = "test-test1-test";
        System.out.println(key.substring(key.lastIndexOf("-") + 1, key.length()));
    }

    /**
     * 通过url获取功能
     *
     * @param url
     * @param dpService
     * @return
     */
    public List<Map<String, Object>> getFunctionByUrl(String url, DBService dpService) {
        String sql = "select * from sys_function f where f.fun_url=? and f.is_delete<>1";
        List params = new ArrayList<>();
        params.add(url);
        List<Map<String, Object>> list = dpService.select(sql, params);
        return list;
    }

    /**
     * 获取需要填入SOURCE的xml
     *
     * @param url
     * @param dpService
     * @return
     */
    public List<Map<String, Object>> getUptFunctionByUrl(String url, DBService dpService) {
        String sql = "select * from sys_function f where f.fun_url=? and f.source is null and f.is_delete<>1";
        List params = new ArrayList<>();
        params.add(url);
        List<Map<String, Object>> list = dpService.select(sql, params);
        return list;
    }
}
