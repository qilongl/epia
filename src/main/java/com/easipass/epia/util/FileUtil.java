package com.easipass.epia.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lql on 2017/6/29.
 * 文件处理的工具类
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);


    /**
     * 获取相对目录下特定后缀名的文件集合
     *
     * @param dirs
     * @param stuffix
     * @return
     */
    public static List<File> getFiles(String dirs, String stuffix) {
        logger.debug("\t读取" + dirs + "目录下业务文件");
        List<File> allFiles = new ArrayList<>();
        String basePath = ClassUtils.getDefaultClassLoader().getResource(dirs).getPath();
        File dir = new File(basePath);
        getAllFile(dir, stuffix, allFiles);
        return allFiles;
    }

    /**
     * 读取该dir目录下面的所有的stuffix 结尾的文件到allFiles 中
     *
     * @param dir
     * @param stuffix
     * @param allFiles
     */
    public static void getAllFile(File dir, String stuffix, List<File> allFiles) {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new UnsupportedOperationException(dir + "目录不存在!");
        }
        for (File a : files) {
            if (a.isFile() && a.getName().toLowerCase().endsWith(stuffix)) {
                allFiles.add(a);
            } else if (a.isDirectory()) {
                getAllFile(a, stuffix, allFiles);
            }
        }
    }

    /**
     * 读取该dir目录下面的所有的文件
     *
     * @param dir
     * @param allFiles
     * @return
     */
    public static void getAllFile(File dir, List<File> allFiles) {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new UnsupportedOperationException(dir + "目录不存在!");
        }
        for (File a : files) {
            if (a.isFile()) {
                allFiles.add(a);
            } else if (a.isDirectory()) {
                getAllFile(a, allFiles);
            }
        }
    }


    /**
     * 获取到当前业务文件的请求路径
     *
     * @param absPath
     * @param sysbase
     * @param userbase
     * @return
     */
    public static String getNameKey(String absPath, String sysbase, String userbase) {
        String moduleUrl = "";
        absPath = absPath.replaceAll("/", File.separator);
        if (absPath.contains(sysbase)) {
            moduleUrl = absPath.substring(absPath.indexOf(sysbase), absPath.length());
            moduleUrl = moduleUrl.replace(File.separator, "-");
            moduleUrl = moduleUrl.substring(0, moduleUrl.lastIndexOf("-"));
        } else {
            moduleUrl = absPath.substring(absPath.indexOf(userbase), absPath.length());
            moduleUrl = moduleUrl.replace(File.separator, "-");
            moduleUrl = moduleUrl.substring(0, moduleUrl.lastIndexOf("-"));
        }
        return moduleUrl;
    }

    public static void mkdirs(String path) {
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
    }

}
