package com.easipass.epia.beans;

import com.easipass.epia.util.StringHelper;
import org.dom4j.Element;

import java.io.Serializable;

/**
 * Created by lql on 2018/12/25.
 * XML中<Actions></Actions>指令标签
 **/
public class Action implements Serializable {

    public Action() {
    }

    /**
     * 命令标签映射
     *
     * @param element
     */
    public Action(Element element) {
        init(element);
    }

    /**
     * 初始化命令
     *
     * @param element
     */
    public void init(Element element) {
        String cmdType = element.getName();
        String id = element.attributeValue("id");
        String params = element.attributeValue("params");
        String islist = element.attributeValue("islist");
        String isreturn = element.attributeValue("isreturn");
        String propertyname = element.attributeValue("propertyname");
        String iszip = element.attributeValue("iszip");
        iszip = StringHelper.isNotNull(iszip) ? iszip : "";
        String isencrypt = element.attributeValue("isencrypt");
        isencrypt = StringHelper.isNotNull(isencrypt) ? isencrypt : "";
        String left = element.attributeValue("left");
        String right = element.attributeValue("right");
        String on = element.attributeValue("on");
        String source = element.attributeValue("source");
        String content = element.asXML().toString();
        String cmdStr = element.getText();
        String required = element.attributeValue("required");
        String expect = element.attributeValue("expect");
        String savepath = element.attributeValue("savepath");
        String classpath = element.attributeValue("classpath");
        String method = element.attributeValue("method");
        String value = element.attributeValue("value");
        String returntype = element.attributeValue("returntype");
        String dataset = element.attributeValue("dataset");
        String mixcol = element.attributeValue("mixcol");
        String isPaging = element.attributeValue("ispaging");
        String filename = element.attributeValue("filename");
        String test = element.attributeValue("test");
        String range = element.attributeValue("range");
        String errorId = element.attributeValue("errorid");
        String msg = element.attributeValue("msg");
        String basesavepath = element.attributeValue("basesavepath");
        String isdatedir = element.attributeValue("isdatedir");
        String usegenname = element.attributeValue("usegenname");
        String contenttype = element.attributeValue("contenttype");
        String abspath = element.attributeValue("abspath");
        returntype = returntype == null ? "" : returntype;
        returntype = "null".equalsIgnoreCase(returntype) ? "" : returntype;

        range = range == null ? "" : range;
        range = "null".equalsIgnoreCase(range) ? "" : range;

        filename = filename == null ? "" : filename;
        filename = "null".equalsIgnoreCase(filename) ? "" : filename;

        basesavepath = basesavepath == null ? "" : basesavepath;
        basesavepath = "null".equalsIgnoreCase(basesavepath) ? "" : basesavepath;

        dataset = dataset == null ? "" : dataset;
        dataset = "null".equalsIgnoreCase(dataset) ? "" : dataset;
        mixcol = mixcol == null ? "" : mixcol;
        mixcol = "null".equalsIgnoreCase(mixcol) ? "" : mixcol;
        this.msg = msg;
        this.cmdType = cmdType;
        this.id = id;
        this.params = params;
        this.isList = islist == null ? false : Boolean.parseBoolean(islist);
        this.isreturn = isreturn == null ? false : Boolean.parseBoolean(isreturn);
        this.isdatedir = isdatedir == null ? true : Boolean.parseBoolean(isdatedir);
        this.usegenname = usegenname == null ? true : Boolean.parseBoolean(usegenname);
        this.on = StringHelper.isNotNull(on) ? on : "";
        this.propertyname = propertyname;
        this.left = left;
        this.right = right;
        this.content = content;
        this.cmdStr = cmdStr;
        this.source = source;
        this.required = required == null ? false : Boolean.parseBoolean(required);
        this.ispaging = isPaging == null ? false : Boolean.parseBoolean(isPaging);
        this.expect = expect;
        this.savepath = savepath;
        this.classpath = classpath;
        this.method = method;
        this.value = value;
        this.returntype = returntype;
        this.dataset = dataset;
        this.mixcol = mixcol;
        this.iszip = iszip;
        this.isencrypt = isencrypt;
        this.filename = filename;
        this.test = test;
        this.range = range;
        this.errorid = errorId;
        this.basesavepath = basesavepath;
        this.contenttype = contenttype == null ? this.contenttype : contenttype;
        this.abspath = abspath == null ? this.abspath : abspath;
    }

    /**
     * ================================================定义标签类型======================================================
     */

    /**
     * 查询
     */
    public static final String SELECT = "SELECT";
    /**
     * 添加
     */
    public static final String INSERT = "INSERT";
    /**
     * 更新
     */
    public static final String UPDATE = "UPDATE";
    /**
     * 删除
     */
    public static final String DELETE = "DELETE";

    /**
     * 定义变量
     */
    public static final String VAR = "VAR";

    /**
     * web服务
     */
    //TODO
    public static final String SERVICE = "SERVICE";

    /**
     * rest调用
     */
    public static final String REST = "REST";

    /**
     * 转换器
     */
    public static final String CONVERT = "CONVERT";

    /**
     * 引入其他的业务配置文件
     */
    public static final String IMPORT = "IMPORT";

    /**
     * if 判断
     */
    public static final String IF = "IF";

    /**
     * 自定义异常
     */
    public static final String ERROR = "ERROR";

    /**
     * 创建文件
     */
    public static final String CREATEFILE = "CREATEFILE";

    /**
     * 下载
     */
    public static final String DOWNLOAD = "DOWNLOAD";

    /**
     * 删除文件
     */
    public static final String DELETEFILE = "DELETEFILE";

    /**
     * ======================================================定义标签属性======================================================
     */
    private String id = "";//唯一标识
    private String params;// 参数ID
    private boolean isList;// 是否依据参数批量执行
    private boolean isreturn;//是否把执行结果加入到返回值列表中
    private String cmdStr;// 操作的正文语句
    private String cmdType;//操作的类型
    private String content;// 操作的字符串

    private String propertyname;//上传文件对应的属性名
    private String iszip;       //文件上传后是否压缩或文件下载时是否压缩,多个文件同时下载默认压缩,压缩格式为zip
    private String isencrypt;   //文件上传后是否加密

    private String size = "0";//结果集大小
    private String range;// 行整体循环次数
    private String left;//左测数据源
    private String right;//右侧数据源
    private String on;// 左连接的共有列

    private String source;// import 的属性，指向导入的业务id

    private boolean required;// upload 属性是否在前端是必选字段

    private String expect;// 预期结果总数量

    private String savepath;// 保存的相对目录

    private String classpath;// 工具类路径

    private String method;//工具类方法

    private String value;// 给 var 命令定义value 属性

    private String dataset;// 数据集
    private String mixcol;// 取交集的col

    private String returntype;// 返回数据的方式

    private boolean ispaging;// 是否分页

    private String filename;// 文件类型,用来做磁盘文件创建

    private String test;// if标签的test属性

    private String columnname;//convert 命令要转换的列名

    private String errorid = "";//错误编号

    private String msg;//异常提示消息

    private boolean usegenname = true;//使用自动产生的文件名

    private String basesavepath;//文件上传，自定义的存储跟目录，不填该属性，默认使用系统的附件存储目录attachment

    private String contenttype = "STRING";//读取文件的正文类型

    private String abspath;//文件的绝对路径

    private boolean isdatedir = true;//默认true,在basesavepath+savepath后面自动根据日期产生的目录结构

    /**
     * 命令执行的结果集
     */
    private Object result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public boolean isreturn() {
        return isreturn;
    }

    public void setIsreturn(boolean isreturn) {
        this.isreturn = isreturn;
    }

    public String getCmdStr() {
        return cmdStr;
    }

    public void setCmdStr(String cmdStr) {
        this.cmdStr = cmdStr;
    }

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPropertyname() {
        return propertyname;
    }

    public void setPropertyname(String propertyname) {
        this.propertyname = propertyname;
    }

    public String getIszip() {
        return iszip;
    }

    public void setIszip(String iszip) {
        this.iszip = iszip;
    }

    public String getIsencrypt() {
        return isencrypt;
    }

    public void setIsencrypt(String isencrypt) {
        this.isencrypt = isencrypt;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getExpect() {
        return expect;
    }

    public void setExpect(String expect) {
        this.expect = expect;
    }

    public String getSavepath() {
        return savepath;
    }

    public void setSavepath(String savepath) {
        this.savepath = savepath;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getMixcol() {
        return mixcol;
    }

    public void setMixcol(String mixcol) {
        this.mixcol = mixcol;
    }

    public String getReturntype() {
        return returntype;
    }

    public void setReturntype(String returntype) {
        this.returntype = returntype;
    }

    public boolean ispaging() {
        return ispaging;
    }

    public void setIspaging(boolean ispaging) {
        this.ispaging = ispaging;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getColumnname() {
        return columnname;
    }

    public void setColumnname(String columnname) {
        this.columnname = columnname;
    }

    public String getErrorid() {
        return errorid;
    }

    public void setErrorid(String errorid) {
        this.errorid = errorid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isUsegenname() {
        return usegenname;
    }

    public void setUsegenname(boolean usegenname) {
        this.usegenname = usegenname;
    }

    public String getBasesavepath() {
        return basesavepath;
    }

    public void setBasesavepath(String basesavepath) {
        this.basesavepath = basesavepath;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getAbspath() {
        return abspath;
    }

    public void setAbspath(String abspath) {
        this.abspath = abspath;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
