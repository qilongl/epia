package com.easipass.epia.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;

/**
 * Created by lql on 2018/12/25 9:29
 **/
public class PropertiesUtil {
    /**
     * 获取资源文件
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static Configuration getDirConfig(String fileName) throws Exception {
        Configuration configuration = null;
        //1.先从jar文件所在目录获取
        String path = PropertiesUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//        System.out.println(path);
        if (path.indexOf(".jar") != -1) {
            path = path.substring(0, path.indexOf(".jar"));
            path = path.substring(0, path.lastIndexOf(File.separator) + 1);
            File file = new File(path + fileName);
            if (file.exists()) {
                configuration = new PropertiesConfiguration(file);
//                System.out.println("文件存在");
            } else {
//                System.out.println("文件不存在");
                configuration = new PropertiesConfiguration(fileName);
            }
            //2.如果获取失败，才从jar包内部获取
        } else {
//            System.out.println("从jar包中加载");
            configuration = new PropertiesConfiguration(fileName);
        }
        return configuration;
    }

    public static void main(String[] args) {
        System.out.println(PropertiesUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
}
