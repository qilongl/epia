package com.easipass.epia.service.actions;

import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.SysProperties;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.util.FileUtil;
import com.easipass.epia.util.TypeUtil;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by lql on 2019/1/8 16:02
 **/
public class DownLoadAction implements Serializable {
    public static final String ORINAME = "ORINAME";
    public static final String PATH = "PATH";

    private XmlBusiConfig xmlBusiConfig;

    public DownLoadAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }

    /**
     * 附件下载
     *
     * @param cmd
     * @param inParams
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> downLoadCommand(Action cmd, JSONObject inParams) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String params = cmd.getParams();
            cmd.setIsreturn(true);
            List<String> pathList = new ArrayList<>();
            List<Map<String, Object>> configResult = TypeUtil.changeToListMap(xmlBusiConfig.getParamsFromInParams_CmdResult(params, inParams));
            if (configResult.size() > 1) // 多个附件
            {
                zipDownload(configResult, pathList, result);
//                /**
//                 *  写入zip文件到磁盘
//                 */
//                byte[] buf = new byte[1024];
//                int len;
//                // 定义压缩文件名称
//                StringBuffer zipFileName = new StringBuffer();
//                String dirPath = SysProperties.getAttachmentPath() + File.separator + "tmp";
//                File dir = new File(dirPath);
//                if (!dir.exists())
//                    dir.mkdirs();
//                //创建临时压缩文件
//                String zipFilePath = dir.getAbsolutePath() + File.separator + UUID.randomUUID();
//                ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(new File(zipFilePath)));
//                for (int i = 0; i < configResult.size(); i++) {
//                    Map<String, Object> map = configResult.get(i);
//                    String fileName = map.get(ORINAME).toString();
//                    pathList.add(fileName);
//                    // 取第一个文件名称
//                    if (i == 0) {
//                        zipFileName.append(fileName.substring(0, fileName.lastIndexOf(".")));
//                    }
//                    String path = map.get(PATH).toString();
//                    FileInputStream in = new FileInputStream(new File(path));
//                    zout.putNextEntry(new ZipEntry(fileName));
//                    while ((len = in.read(buf)) > 0) {
//                        zout.write(buf, 0, len);
//                    }
//                    zout.closeEntry();
//                    in.close();
//                }
//                zout.close();
//                // 取文件总数加入文件名称中
//                zipFileName = zipFileName.append("+" + configResult.size()).append(".zip");
//                // 文件名中加上标记
//                zipFileName = zipFileName.append("@download");
//                /***
//                 *  读取zip文件到缓存中
//                 */
//                Map<String, Object> rr = new HashedMap();
//                byte[] bytes = readAttachment(zipFilePath);
//                rr.put(zipFileName.toString(), new BASE64Encoder().encode(bytes));
//                result.add(rr);
            } else // 单个附件
            {
                Map<String, Object> param = configResult.get(0);
                String fileName = param.get(ORINAME).toString();
                String path = param.get(PATH).toString();
                pathList.add(fileName);
                Map<String, Object> rr = new HashedMap();
                List<File> allFiles = new ArrayList<>();
                fileName = fileName + "@download";
                // 取出附件内容
                File file = new File(path);
                if (file.isDirectory()) {
                    FileUtil.getAllFile(file, allFiles);
                }
                byte[] bytes = readAttachment(path);
                // 加密
                rr.put(fileName, new BASE64Encoder().encode(bytes));
                result.add(rr);
            }
        } catch (Exception ex) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), "", ex);
        }
        return result;
    }

    /**
     * 多附件下载，zip格式
     *
     * @param configResult
     * @param pathList
     * @param result
     * @throws Exception
     */
    public void zipDownload(List<Map<String, Object>> configResult, List<String> pathList, List<Map<String, Object>> result) throws Exception {
        /**
         *  写入zip文件到磁盘
         */
        byte[] buf = new byte[1024];
        int len;
        // 定义压缩文件名称
        StringBuffer zipFileName = new StringBuffer();
        String dirPath = SysProperties.getAttachmentPath() + File.separator + "tmp";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        //创建临时压缩文件
        String zipFilePath = dir.getAbsolutePath() + File.separator + UUID.randomUUID();
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(new File(zipFilePath)));
        for (int i = 0; i < configResult.size(); i++) {
            Map<String, Object> map = configResult.get(i);
            String fileName = map.get(ORINAME).toString();
            pathList.add(fileName);
            // 取第一个文件名称
            if (i == 0) {
                zipFileName.append(fileName.substring(0, fileName.lastIndexOf(".")));
            }
            String path = map.get(PATH).toString();
            FileInputStream in = new FileInputStream(new File(path));
            zout.putNextEntry(new ZipEntry(fileName));
            while ((len = in.read(buf)) > 0) {
                zout.write(buf, 0, len);
            }
            zout.closeEntry();
            in.close();
        }
        zout.close();
        // 取文件总数加入文件名称中
        zipFileName = zipFileName.append("+" + configResult.size()).append(".zip");
        // 文件名中加上标记
        zipFileName = zipFileName.append("@download");
        /***
         *  读取zip文件到缓存中
         */
        Map<String, Object> rr = new HashedMap();
        byte[] bytes = readAttachment(zipFilePath);
        rr.put(zipFileName.toString(), new BASE64Encoder().encode(bytes));
        result.add(rr);

    }

    /**
     * 读取一个附件到缓存
     *
     * @param path
     * @return
     */
    private byte[] readAttachment(String path) throws Exception {
        File file = new File(path);
        if (file.length() > Integer.MAX_VALUE) {
            // 文件太大，无法读取
            throw new IOException("文件太大,无法读取 " + file.getName());
        }
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = new FileInputStream(file);
            // 创建一个数据来保存文件数据
            bytes = new byte[(int) file.length()];
            // 读取数据到byte数组中
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset < bytes.length) {
                throw new IOException("没有完整的读取文件" + file.getName());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //关闭流
            is.close();
        }
        return bytes;
    }
}
