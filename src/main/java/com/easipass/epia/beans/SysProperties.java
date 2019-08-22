package com.easipass.epia.beans;

import com.easipass.epia.util.FileUtil;
import com.easipass.epia.util.PropertiesUtil;
import com.easipass.epia.util.StringHelper;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lql on 2018/12/25 9:26
 * 系统配置文件实体类
 **/
public class SysProperties {

    private static Logger logger = LoggerFactory.getLogger(SysProperties.class);
    private static String configFile = "sys.properties";
    /**
     * 字典存储所有的key value
     */
    private static Map<String, String> dic = new HashMap<>();

    /**
     * 系统业务文件存放的根目录
     */
    private static String sysbasedir;// 最近父级目录
    private static String sysBaseDir;// 绝对路径
    /**
     * 用户业务文件存放的根目录
     */
    private static String userbasedir;// 最近父级目录
    private static String userBaseDir;// 绝对路径

    private static String baseDic;//所有配置文件的根目录

    private static String isPrintErrorDetail;//是否输入异常详情

    private static String isProduction;//是否生产环境

    /**
     * 附件存储路径
     */
    private static String attachmentPath;

    public static String getIsProduction() {
        return isProduction;
    }

    public static String getSysbasedir() {
        return sysbasedir;
    }

    public static String getUserbasedir() {
        return userbasedir;
    }

    public static String getSysBaseDir() {
        return sysBaseDir;
    }

    public static String getUserBaseDir() {
        return userBaseDir;
    }

    public static String getAttachmentPath() {
        return attachmentPath;
    }

    public static String getIsPrintErrorDetail() {
        return isPrintErrorDetail;
    }

    /**
     * 初始化系统配置
     *
     * @throws Exception
     */
    public static void init() throws Exception {
        Configuration configuration = PropertiesUtil.getDirConfig(configFile);
        String sysbasedir = StringHelper.stringEncoding2UTF8(configuration.getString("sysbasedir"));
        String userbasedir = StringHelper.stringEncoding2UTF8(configuration.getString("userbasedir"));
        /**new get properties end**/
        sysBaseDir = sysbasedir;
        userBaseDir = userbasedir;
        attachmentPath = StringHelper.stringEncoding2UTF8(configuration.getString("attachmentPath"));
        isPrintErrorDetail = StringHelper.stringEncoding2UTF8(configuration.getString("isPrintErrorDetail"));
        isProduction = StringHelper.stringEncoding2UTF8(configuration.getString("isProduction"));
        SysProperties.sysbasedir = sysbasedir.substring(sysbasedir.lastIndexOf(File.separator) + 1, sysbasedir.length());
        SysProperties.userbasedir = userbasedir.substring(userbasedir.lastIndexOf(File.separator) + 1, userbasedir.length());
        FileUtil.mkdirs(attachmentPath);
        Iterator it = configuration.getKeys();
        dic.clear();
        while (it.hasNext()) {
            String key = it.next().toString();
            String value = configuration.getString(key);
            dic.put(key, value);
        }
        baseDic = sysbasedir.substring(0, sysbasedir.lastIndexOf(File.separator));
        dic.put("baseDir", baseDic);
    }

    /**
     * 通过key获取配置文件中的值
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        if (dic.containsKey(key))
            return dic.get(key);
        else
            return null;
//            throw new UnsupportedOperationException("您尝试获取未定义的系统属性" + key);
    }

    public static void set(String key, String value) {
        dic.put(key, value);
    }

}
