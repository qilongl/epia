package com.easipass.epia.service.actions;

import com.easipass.epia.beans.Action;
import com.easipass.epia.beans.SysProperties;
import com.easipass.epia.beans.XmlBusiConfig;
import com.easipass.epia.exception.UserException;
import com.easipass.epia.util.StringHelper;
import com.easipass.epia.util.TypeUtil;
import com.easipass.epia.util.WriteExcelUtil;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static com.easipass.epia.service.actions.DownLoadAction.ORINAME;

/**
 * Created by lql on 2019/1/8 16:58
 **/
public class CreateFileAction implements Serializable {
    public static final String ORINAME = "ORINAME";
    public static final String PATH = "PATH";

    public static final String TXT = ".TXT";//TXT
    public static final String XLS = ".XLS";//XLS
    public static final String XLSX = ".XLSX";//XLSX
    public static final String XML = ".XML";//XML
    private XmlBusiConfig xmlBusiConfig;

    public CreateFileAction(XmlBusiConfig xmlBusiConfig) {
        this.xmlBusiConfig = xmlBusiConfig;
    }

    public List<Map<String, Object>> createFileCommand(Action cmd, JSONObject inParams) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String fileName = cmd.getFilename();
            String params = cmd.getParams();
            if (!StringHelper.isNotNull(fileName))
//                throw new UnsupportedOperationException(cmd.getCmdType() + "标签" + cmd.getId() + " 需要给出明确的filename属性!");
                fileName = UUID.randomUUID()  + TXT.toLowerCase();

            String propertiesName = xmlBusiConfig.getValue(cmd, cmd.getPropertyname());
            String fileNameValue = xmlBusiConfig.getValue(cmd, fileName);
            String savePath = xmlBusiConfig.getValue(cmd, cmd.getSavepath());
            String baseDir = xmlBusiConfig.getValue(cmd, cmd.getBasesavepath());

            Object data = xmlBusiConfig.getParamsFromInParams_CmdResult(params, inParams);
            String dirPath = SysProperties.getAttachmentPath() + File.separator + "tmp";
            File dir = new File(dirPath);
            if (!dir.exists())
                dir.mkdirs();
            String filePath = "";
            if (StringHelper.isNotNull(baseDir)) {
                filePath = baseDir + File.separator + savePath + File.separator;
                dir = new File(filePath);
                if (!dir.exists())
                    dir.mkdirs();
                filePath = filePath + fileNameValue;
            } else {
//                filePath = dirPath + File.separator + UUID.randomUUID();
                filePath = dirPath + File.separator + fileNameValue;
            }
            String fileType = fileNameValue.substring(fileNameValue.lastIndexOf("."), fileNameValue.length());
            switch (fileType.toUpperCase()) {
                case TXT:
                    writeListMapToTxt(data, filePath);
                    break;
                case XLS:
                    writeExcel(data, filePath, fileNameValue, XLS);
                    break;
                case XLSX:
                    writeExcel(data, filePath, fileNameValue, XLSX);
                    break;
                case XML:
                    writeXml(data, filePath, params, propertiesName);
                    break;
                default:
                    throw new UnsupportedOperationException("不支持的创建文件的格式:" + fileType);

            }
            Map<String, Object> mapResult = new HashMap();
            mapResult.put(ORINAME, fileNameValue);
            mapResult.put(PATH, filePath);
            result.add(mapResult);

        } catch (Exception ex) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), "", ex);
        }
        return result;
    }

    /**
     * 写入txt文件
     *
     * @param data
     * @param filePath
     */
    private void writeListMapToTxt(Object data, String filePath) throws Exception {
        List<Map<String, Object>> allData;
        Map<String, Object> oneData = null;
        allData = TypeUtil.changeToListMap(data);
        if (allData.size() == 0) {
            oneData = new HashMap<>();
        } else {
            oneData = allData.get(0);
        }
        // 构建标题
        StringBuffer titleBuffer = new StringBuffer();
        List<String> title = new ArrayList<>();
        Iterator iterator = oneData.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            title.add(key);
            titleBuffer.append(key + "\t");
        }
        titleBuffer.append("\r\n");
        //构建正文
        StringBuffer contentBuffer = new StringBuffer();
        for (int i = 0; i < allData.size(); i++) {
            Map<String, Object> map = allData.get(i);
            for (int j = 0; j < title.size(); j++) {
                String name = title.get(j);
                String value = map.get(name) == null ? "" : map.get(name).toString();
                contentBuffer.append(value + "\t");
            }
            contentBuffer.append("\r\n");
        }
        contentBuffer = titleBuffer.append(contentBuffer);
        writeStringContentToFile(contentBuffer.toString(), filePath);
    }

    /**
     * 写入xls文件
     *
     * @param data
     * @param filePath
     */
    private void writeExcel(Object data, String filePath, String oriName, String fileType) {
        Map<String, Object> oneData;
        List<Map<String, Object>> allData = TypeUtil.changeToListMap(data);
        if (allData.size() == 0) {
            oneData = new HashMap<>();
        } else {
            oneData = allData.get(0);
        }
        // 构建标题
        List<String> title = new ArrayList<>();
        Iterator iterator = oneData.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            title.add(key);
        }
        //sheet 名
        String sheetName = oriName.substring(0, oriName.lastIndexOf("."));
        WriteExcelUtil.writerExcel(filePath, fileType, sheetName, title, allData);
    }

    /**
     * 写入xml文件
     *
     * @param data
     * @param filePath
     */
    private void writeXml(Object data, String filePath, String params, String propertiesName) throws Exception {
        if (StringHelper.isNotNull(propertiesName)) {
            List<Map<String, Object>> dataOne = TypeUtil.changeToListMap(data);
            Map<String, Object> dataObj = dataOne.get(0);
            if (!dataObj.containsKey(propertiesName))
                throw new UnsupportedOperationException(params + "的结果集中不含有属性" + propertiesName);
            Object object = dataObj.get(propertiesName);
            String content = StringHelper.toString(object);
            String xmlTitle = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            if (content.indexOf("<?xml") == -1)
                content = xmlTitle.concat(content);
            writeStringContentToFile(content, filePath);
        } else {
            throw new UnsupportedOperationException("需要给出明确的propertyname属性!");
        }
    }

    /**
     * 把字符串的正文写入文件
     *
     * @param content
     * @param filePath
     * @throws Exception
     */
    private void writeStringContentToFile(String content, String filePath) throws Exception {
        // 写入磁盘
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(filePath));
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (null != fileWriter) {
                fileWriter.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        StringBuffer titleBuffer = new StringBuffer();
        StringBuffer contentBuffer = new StringBuffer();
        List<Map<String, Object>> list = getVirtuData();
        List<String> title = new ArrayList<>();
        Map<String, Object> oneData = null;
        if (list.size() > 0) {
            oneData = list.get(0);
        }
        Iterator iterable = oneData.keySet().iterator();
        while (iterable.hasNext()) {
            String key = iterable.next().toString();
            title.add(key);
            titleBuffer.append(key).append("\t");
        }
        titleBuffer.append("\r\n");
        for (int i = 0; i < list.size(); i++) {
            Map map = list.get(i);
            for (int j = 0; j < title.size(); j++) {
                contentBuffer.append(map.get(title.get(j))).append("\t");
            }
            contentBuffer.append("\r\n");
        }
        contentBuffer = titleBuffer.append(contentBuffer);

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File("C:\\Users\\lql\\Desktop\\" + StringHelper.getUUID()) + ".txt");
            fileWriter.write(contentBuffer.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fileWriter)
                fileWriter.close();
        }
    }

    private static List<Map<String, Object>> getVirtuData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            Map map = new TreeMap();
            for (int i = 0; i < 10; i++) {
                map.put("field" + i, StringHelper.getUUID());
            }
            list.add(map);
        }
        return list;
    }

}
