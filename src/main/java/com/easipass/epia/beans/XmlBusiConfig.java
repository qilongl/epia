package com.easipass.epia.beans;

import com.alibaba.fastjson.JSONArray;
import com.easipass.epia.db.DBService;
import com.easipass.epia.exception.UserException;
//import com.easipass.epia.service.actions.ServiceAction;
import com.easipass.epia.service.actions.*;
import com.easipass.epia.util.*;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lql on 2018/12/25 14:18
 * XML业务文件对应的实体对象
 **/
public class XmlBusiConfig implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(XmlBusiConfig.class);

    /**
     * ------------------------------初始化所有操作工具类------------------------------
     * <p>
     * (Action中定义的所有标签类型，标签对应的实体)
     */
    private SqlAction sqlAction = new SqlAction(this);
    private VarAction varAction = new VarAction(this);
    private RestAction restAction = new RestAction(this);
    private ConvertAction convertAction = new ConvertAction(this);
    private ImportAction importAction = new ImportAction(this);
    private IfAction ifAction = new IfAction(this);
    private ErrorAction errorAction = new ErrorAction(this);
    private DownLoadAction downLoadAction = new DownLoadAction(this);
    private CreateFileAction createFileAction = new CreateFileAction(this);
    private DeleteFileAction deleteFileAction = new DeleteFileAction(this);

    public XmlBusiConfig() {
    }

    // 以流的形式初始化的构造方法
    public XmlBusiConfig(InputStream inputStream) throws Exception {
        init(inputStream);
    }

    // 以文件的形式初始化的构造方法
    public XmlBusiConfig(File file) throws Exception {
        /**
         * 配置初始化
         */
        init(file);
    }

    //入参原始值
    private JSONObject inParams;

    // 参数集合
    private Map<String, Parameters> parametersMap;
    // 命令集合
    private List<Action> cmdList;
    // 预处理 TODO
    private List<Action> preList;

    // 系统参数
    /**
     * 业务唯一ID
     */
    private String id;
    /**
     * 业务使用的数据源名称
     */
    private String dataSource;
    /**
     * 功能名称
     */
    private String name;
    /**
     * 功能描述
     */
    private String desc;
    /**
     * 业务所属模块
     */
    private String moduleName;

    private DBService dbService;

    /**
     * 结果集
     */
    private Map<String, Object> result = new HashMap();

    public Map<String, Parameters> getParametersMap() {
        return parametersMap;
    }

    public List<Action> getCmdList() {
        return cmdList;
    }

    public void setCmdList(List<Action> cmdList) {
        this.cmdList = cmdList;
    }

    public void setParametersMap(Map<String, Parameters> parametersMap) {
        this.parametersMap = parametersMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public DBService getDBService() {
        return dbService;
    }

    public void setDBService(DBService dbService) {
        this.dbService = dbService;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }


    /**
     * 初始化业务配置文件到对象
     * 通过文件的方式加载
     *
     * @param file
     * @throws Exception
     */
    private void init(File file) throws Exception {
        Document dom = null;
        StringBuffer content = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (null != br)
                br.close();
        }
        try {
            //XML格式校验
            dom = DocumentHelper.parseText(content.toString());
        } catch (Exception ex) {
            throw new UnsupportedEncodingException("正文XML格式检查错误！\n" + content.toString());
        }
        initContent(dom);
    }

    /**
     * 初始化业务配置文件到对象
     * 通过流的方式加载
     *
     * @param inputStream
     * @throws Exception
     */
    private void init(InputStream inputStream) throws Exception {
        Document dom = null;
        BufferedReader br = null;
        StringBuffer content = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (null != br)
                br.close();
        }
        try {
            dom = DocumentHelper.parseText(content.toString());
        } catch (Exception ex) {
            throw new UnsupportedEncodingException("正文XML格式检查错误！\n" + content.toString());
        }
        initContent(dom);
    }

    /**
     * 将XML文档映射为实体对象
     *
     * @param document
     */
    private void initContent(Document document) {
        try {
            Element rootElement = document.getRootElement();
            String id = rootElement.attributeValue("id");
            String dataSource = rootElement.attributeValue("datasource");
            String desc = rootElement.attributeValue("desc");
            String name = rootElement.attributeValue("name");
            // 设置ID和数据源
            this.id = id;
            this.dataSource = dataSource;
            this.desc = desc;
            this.name = name;
            /**
             * 遍历根节点
             */
            parametersMap = new HashMap();// 参数模型
            cmdList = new ArrayList<>();// 命令集合
            preList = new ArrayList<>();//预处理集合
            Iterator it = rootElement.elementIterator();
            while (it.hasNext()) {
                Element element = (Element) it.next();
                // 遍历参数
                if (element.getName().equalsIgnoreCase("Parameters")) {
                    List<Element> parameters = element.elements();
                    for (int i = 0; i < parameters.size(); i++) {
                        Element element1 = parameters.get(i);
                        parametersMap.put(element1.getName(), new Parameters(element1));
                    }
                }
                // 遍历命令（一级元素标签）
                else if ((element.getName().equalsIgnoreCase("Actions"))) {
                    List<Element> parameters = element.elements();
                    for (int i = 0; i < parameters.size(); i++) {
                        Element element1 = parameters.get(i);
                        cmdList.add(new Action(element1));
                    }

                }//预处理
                else if ((element.getName().equalsIgnoreCase("Pre"))) {
                    List<Element> parameters = element.elements();
                    for (int i = 0; i < parameters.size(); i++) {
                        Element element1 = parameters.get(i);
                        preList.add(new Action(element1));
                    }

                } else {
                    throw new UnsupportedOperationException("暂不支持该标签:" + element.getName());
                }
            }

        } catch (Exception ex) {
            throw new UnsupportedOperationException("加载配置文件错误！", ex);
        }
    }

    /**
     * 执行命令
     *
     * @param inParameters 为前端传递的json字符串
     * @param fileMap      xml中包含文件流时
     * @return
     * @throws Exception
     */
    public Map<String, Object> exec(JSONObject inParameters, Map<String, List<Map<String, byte[]>>> fileMap) throws Exception {
        //0.初始化入参原始值
        this.inParams = inParameters;
        //1.参数预处理
        execPre(inParameters, fileMap);
        //2.过滤参数:设置默认值,检查是否必填
        JSONObject filteredParam = filterParameters(inParameters);
        //3.执行命令集
        execCmd(filteredParam, fileMap);
        return result;
    }

    /**
     * ------------------------------1、参数预处理------------------------------
     *
     * @param filteredParam
     * @param fileMap
     * @throws Exception
     */
    private void execPre(JSONObject filteredParam, Map<String, List<Map<String, byte[]>>> fileMap) throws Exception {
        for (int i = 0; i < preList.size(); i++) {
            Action cmd = preList.get(i);
            switch (cmd.getCmdType().toUpperCase()) {
//                case Action.CONVERT:
//                    convertAction.convertCommand(cmd, filteredParam, fileMap, dpService);
//                    break;
                default:
                    throw new UnsupportedOperationException("预处理暂时不支持" + cmd.getCmdType());
            }
        }
    }

    /**
     * ------------------------------2、过滤参数--------------------------------
     *
     * @param inParameters
     * @return
     */
    public JSONObject filterParameters(JSONObject inParameters) throws Exception {
        try {
            /**
             * 取出参数模型,得到每个参数的规则
             */
            Map<String, Parameters> parmameterModel = getParametersMap();
            Iterator iterator = parmameterModel.keySet().iterator();
            while (iterator.hasNext()) {
                //xml中定义的参数名
                String paramsModel_key = iterator.next().toString();
                /**
                 * 遍历入参模型和入参(外层是模型,内层是入参)
                 */
                Parameters p_Model = parmameterModel.get(paramsModel_key);
                //构建完整参数列表
                /**
                 * 实际参数中不包含模型中定义的参数
                 */
                if (!inParameters.containsKey(paramsModel_key)) {
                    /**
                     * 如果是必填,则抛出异常提示缺少必填参数
                     */
                    if (isMustModel(p_Model)) {
                        throw new UnsupportedOperationException(paramsModel_key + "参数对象必须包含有其他必填属性参数!");
                    } else {
                        /**
                         * 不是必填模型,即
                         */
                        inParameters = buildNullJsonObject(p_Model, inParameters);
                    }
                } else {
                    /**
                     * 实际参数中包含模型中定义的参数
                     */
                    //检查出冗余属性
                    checkInParamsTooMuch(p_Model, inParameters);
                    //检查出过少属性和必填及格式
                    checkInParamsTooLittle(p_Model, inParameters);
                }
            }
        } catch (Exception ex) {
            throw new UserException("入参校验", ResponseResult.RESULT_STATUS_CODE_ERROR, "校验入参时异常!", ex);
        }
        return inParameters;
    }

    /**
     * ------------------------------3、执行命令集------------------------------
     *
     * @param filteredParam 过滤后的参数
     * @return
     */
    private Object execCmd(JSONObject filteredParam, Map<String, List<Map<String, byte[]>>> fileMap) throws Exception {
        for (int i = 0; i < cmdList.size(); i++) {
            Action cmd = cmdList.get(i);
            Object result = new Object();
            switch (cmd.getCmdType().toUpperCase()) {
                case Action.INSERT:
                case Action.DELETE:
                case Action.UPDATE:
                case Action.SELECT:
                    result = sqlAction.sqlCommand(cmd, filteredParam);// 返回List<Map>类型,，转成JSONArray
                    cmd.setResult(result);
                    break;
                case Action.VAR:
                    result = varAction.varCommand(cmd, filteredParam);//定义变量
                    cmd.setResult(result);
                    break;
                case Action.REST:
                    result = restAction.restCommand(cmd);//逻辑判断
                    cmd.setResult(result);
                    break;
                case Action.CONVERT:
                    result = convertAction.convertCommand(cmd, filteredParam, fileMap, dbService);//数据转换
                    cmd.setResult(result);
                    break;
                case Action.IMPORT:
                    importAction.importCommand(cmd, filteredParam, fileMap, this.result, dbService); //如果需要返回值,会被添加到result中
                    break;
                case Action.IF:
                    boolean flag = ifAction.ifCommand(cmd, filteredParam);//逻辑判断
                    if (flag)
                        i--;//执行if标签体中的命令
                    break;
                case Action.ERROR:
                    result = errorAction.errorCommand(cmd, filteredParam);//抛出自定义异常
                    cmd.setResult(result);
                    break;
//                case Action.UPLOAD:
//                    result = uploadAction.uploadCommand(cmd, fileMap, filteredParam);// 上传默认返回List<Map>
//                    cmd.setResult(result);
//                    break;
                case Action.CREATEFILE:
                    result = createFileAction.createFileCommand(cmd, filteredParam);//创建文件
                    cmd.setResult(result);
                    break;
                case Action.DOWNLOAD:
                    result = downLoadAction.downLoadCommand(cmd, filteredParam);// 上传默认返回List<Map>,转成JsonArray
                    cmd.setResult(result);
                    break;
                case Action.DELETEFILE:
                    result = deleteFileAction.deleteFileCommand(cmd, filteredParam);//删除文件
                    cmd.setResult(result);
                    break;
//                case Action.LEFTJOIN:
//                    result = leftJoinAction.leftjoinCommand(cmd, filteredParam); //左连接
//                    cmd.setResult(result);
//                    break;
//                case Action.TOOLCLASS:
//                    result = toolClassAction.toolClassCommand(cmd, filteredParam);//工具类
//                    cmd.setResult(result);
//                    break;
//                case Action.UNION:
//                    result = unioneAction.unionCommand(cmd);//多结果集取合集
//                    cmd.setResult(result);
//                    break;
//                case Action.MIX:
//                    result = mixAction.mixCommand(cmd);//多结果集取交集
//                    cmd.setResult(result);
//                    break;
//                case Action.CROSS:
//                    result = crossAction.crossCommand(cmd);//多结果集交叉,类似全连接
//                    cmd.setResult(result);
//                    break;
//                case Action.READFILE:
//                    result = readFileAction.readFileCommand(cmd, filteredParam);//读取文件
//                    cmd.setResult(result);
//                    break;
//                case Action.ONE_TO_ONE_COMBIN:
//                    result = oneToOneCombineAction.oneToOneCombineCommand(cmd, filteredParam);//逻辑判断
//                    cmd.setResult(result);
//                    break;
//                case Action.COMMIT:
//                    dpService = commitAction.commitCommand(cmd, dpService);//提交现有事务,开启新的事务
//                    break;
                default:
                    throw new UnsupportedOperationException(cmd.getCmdType() + "未定义！");
            }
            logger.debug("-------------------结果集输出------------------------", OutFormater.print(cmd.getId(), result));
            checkExpect(cmd, result);//期望值校验
            treeConvert(cmd, result);//结果集返还及组装树形结构
            setSize(cmd, result);//结果集大小
        }
        return this.result;
    }

    /**
     * 是否是必填的参数对象: 参数中只要有一个属性没有设置default属性值,则认为该参数模型是必填的
     *
     * @param parameters
     * @return
     */
    private boolean isMustModel(Parameters parameters)// 入参是参数模型
    {
        Iterator it = parameters.getParameterMap().keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            Parameter parameter = parameters.getParameterMap().get(key);
            if (null == parameter.getDefaultValue() || "null".equals(parameter.getDefaultValue()))//如果返回值为"",则说明已经设置了默认值属性,故通过null判断
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据模型构建入参对象
     *
     * @param parameters
     * @param inParams
     * @return
     */
    private JSONObject buildNullJsonObject(Parameters parameters, JSONObject inParams) {
        String name = parameters.getName();
        // 参数是列表
        if (parameters.islist()) {
            JSONArray jsonArray = new JSONArray();
            inParams.put(name, jsonArray);
            return inParams;
        }
        // 参数是jsonobject
        JSONObject jsonObject = new JSONObject();
        Iterator iterator = parameters.getParameterMap().keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            Parameter parameter = parameters.getParameterMap().get(key);
            jsonObject.put(parameter.getName(), buildDefaultValueExpression(parameter.getDefaultValue()));
        }
        inParams.put(name, jsonObject);
        return inParams;
    }

    /**
     * 把默认值中的表达式替换成实际值
     *
     * @param defaultValue
     * @return
     */
    private String buildDefaultValueExpression(String defaultValue) {
        if (null == defaultValue) return null;
        if (defaultValue.indexOf("${") != -1) {
            defaultValue = defaultValue.substring(defaultValue.indexOf("${") + 2, defaultValue.lastIndexOf("}"));
            defaultValue = SystemFunction.get(defaultValue);
        }
        return defaultValue;
    }

    /**
     * 检查输入参数是否包含有未定义入参
     *
     * @param parameters
     * @param inParameters
     */
    private void checkInParamsTooMuch(Parameters parameters, JSONObject inParameters) {
        String key = parameters.getName();
        if (parameters.islist()) {
            net.sf.json.JSONArray jsonArray = inParameters.getJSONArray(key);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String errorKey = checkInParamsPropertiesIsLegal(jsonObject, parameters);
                if (null != errorKey)
//                    throw new UnsupportedOperationException("参数" + key + "中包含未定义属性" + errorKey);
                    logger.warn("参数" + key + "中包含未定义属性" + errorKey);
            }
        } else {
            JSONObject jsonObject = inParameters.getJSONObject(key);
            String errorKey = checkInParamsPropertiesIsLegal(jsonObject, parameters);
            if (null != errorKey)
//                throw new UnsupportedOperationException("参数" + key + "中包含未定义属性" + errorKey);
                logger.warn("参数" + key + "中包含未定义属性" + errorKey);
        }
    }

    /**
     * 检查入参中的对象是否包含模型中没有定义的参数
     *
     * @param paramsObject
     * @param models
     * @return
     */
    private String checkInParamsPropertiesIsLegal(JSONObject paramsObject, Parameters models) {
        Iterator iterator = paramsObject.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (!models.getParameterMap().containsKey(key))
                return key;
        }
        return null;
    }

    /**
     * 入参中参数过少
     *
     * @param parameters_Model
     * @param inParameters
     */
    private void checkInParamsTooLittle(Parameters parameters_Model, JSONObject inParameters) {
        String key = parameters_Model.getName();
        Iterator pIterator = parameters_Model.getParameterMap().keySet().iterator();
        while (pIterator.hasNext()) {
            String key_Model = pIterator.next().toString();
            Parameter parameter = parameters_Model.getParameterMap().get(key_Model);
            /**
             * 入参jsonArray
             */
            if (parameters_Model.islist()) {// 遍历每一个入参对象
                net.sf.json.JSONArray jsonArray = inParameters.getJSONArray(key);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    checkParameter(jsonObject, parameter, key);
                }
            }
            /**
             * 入参是jsonObject
             */
            else {
                JSONObject jsonObject = inParameters.getJSONObject(key);
                checkParameter(jsonObject, parameter, key);
            }
        }
    }

    /**
     * 单个参数进行有效性校验
     *
     * @param jsonObject
     * @param parameter
     * @param key
     */
    private void checkParameter(JSONObject jsonObject, Parameter parameter, String key) {
        if (!jsonObject.containsKey(parameter.getName())) {
            //1.设定了默认值,填入默认值
            if (null != parameter.getDefaultValue() && !"null".equals(parameter.getDefaultValue())) {
                jsonObject.put(parameter.getName(), buildDefaultValueExpression(parameter.getDefaultValue()));
            }
            //2.没有指定默认值则抛出异常
            else {
                throw new UnknownFormatConversionException(key + "." + parameter.getName() + "参数不允许为空!");
            }
        } else {
            //1.检查默认值
            checkDefaultValue(jsonObject, parameter);
            //2.检查必填
            checkRequired(jsonObject, parameter);
            //3.正则表达式验证
            String realValue = jsonObject.getString(parameter.getName());
            if (!checkExpression(parameter.getExpression(), realValue))
                throw new IllegalArgumentException("参数 : " + realValue + " 不合规!" + parameter.getDesc());
        }
    }

    /**
     * 检查默认值的有效性
     *
     * @param jsonObject
     * @param parameter
     */
    private void checkDefaultValue(JSONObject jsonObject, Parameter parameter) {
        if (null != parameter.getDefaultValue() && !"null".equals(parameter.getDefaultValue())) {
            if (jsonObject.get(parameter.getName()) == null || "null".equals(jsonObject.get(parameter.getName()))) {
                jsonObject.put(parameter.getName(), buildDefaultValueExpression(parameter.getDefaultValue()));
            }
        }
    }

    /**
     * reqired校验
     *
     * @param jsonObject
     * @param parameter
     */
    public void checkRequired(JSONObject jsonObject, Parameter parameter) {
        if (!StringHelper.isNotNull(jsonObject.get(parameter.getName()))) {
            if (parameter.isRequired())
                throw new IllegalArgumentException("入参中" + parameter.getName() + "属性值不可以为空!");
        }
    }

    /**
     * 校验给定的值,是否满足正则表达式
     *
     * @param expression
     * @param value
     * @return
     */
    private boolean checkExpression(String expression, String value) {
        if ("".equalsIgnoreCase(expression))
            return true;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * 从结果集中取出集合
     *
     * @param paramName
     * @return
     */
    public Object getParamFromCmdResult(String paramName) {
        for (int i = 0; i < getCmdList().size(); i++) {
            Action action = getCmdList().get(i);
            if (paramName.equals(action.getId())) {
                logger.debug(paramName + "来源于结果集.");
                return action.getResult();
            }
        }
        /**
         * 在命令列表中没有找到结果,再去返回结果中中,可能存在import标签导入的结果集中
         */
        Iterator iterator = this.result.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (paramName.equals(key))
                return this.result.get(key);
        }
        return null;
    }

    /**
     * 预期结果校验
     *
     * @param cmd
     * @param result
     * @throws Exception
     */
    private void checkExpect(Action cmd, Object result) throws Exception {
        try {
            String expect = cmd.getExpect();
            if (!StringHelper.isNotNull(expect)) return;
            if (!TypeUtil.isListMap(result)) return;
            List list = TypeUtil.changeToListMap(result);
            int size = Integer.parseInt(expect);
            if (list.size() != size) {
                throw new UnsupportedOperationException(cmd.getId() + "期望的结果集是" + size + "，而实际的结果集是" + list.size());
            } else if (list.size() == size) {
                return;
            } else {
                throw new UnknownFormatConversionException(cmd.getId() + "," + cmd.getExpect() + "格式不正确!");
            }
        } catch (Exception ex) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), "校验expert属性异常;", ex);
        }
    }

    /**
     * tree 数据格式转换
     *
     * @param cmd
     * @param result
     */
    private void treeConvert(Action cmd, Object result) throws Exception {
        try {
            if (cmd.isreturn() && cmd.getResult() != null) {
                if (cmd.getReturntype().contains("tree")) {
                    ArrayList<Map<String, Object>> li = (ArrayList) result;
                    List<Map<String, Object>> list = (ArrayList<Map<String, Object>>) li.clone();
                    com.alibaba.fastjson.JSONArray jsonArray = null;
                    if (cmd.getReturntype().equalsIgnoreCase("pid-tree"))
                        jsonArray = ResultUtil.change2TreeFromPid(list);
                    else if (cmd.getReturntype().equalsIgnoreCase("code-tree"))
                        jsonArray = ResultUtil.change2TreeFromCode(list);
                    else
                        throw new UnsupportedOperationException(cmd.getId() + "的returntype 指定的转换工具" + cmd.getReturntype() + "不存在!");
                    List<Map<String, Object>> re = new ArrayList<>();
                    Map<String, Object> reMap = new HashMap();
                    reMap.put("tree", jsonArray);
                    re.add(reMap);
                    result = re;
                }
                this.result.put(cmd.getId(), result);
            }
        } catch (Exception ex) {
            throw new UserException(cmd.getId(), cmd.getErrorid(), "tree数据格式转换异常;", ex);
        }
    }

    private void setSize(Action cmd, Object result) throws Exception {
        if (result instanceof List) {
            cmd.setSize(String.valueOf(TypeUtil.changeToListMap(result).size()));
        } else if (result instanceof JSONArray) {
            net.sf.json.JSONArray resultArray = net.sf.json.JSONArray.fromObject(result);
            cmd.setSize(String.valueOf(resultArray.size()));
        } else {
            cmd.setSize(String.valueOf(0));
        }
    }

    /**
     * 获取指定ID的参数集合
     *
     * @param paramName
     * @param inParams
     * @return
     */
    public Object getParamsFromInParams_CmdResult(String paramName, net.sf.json.JSONObject inParams) {
        /**
         * 从系统参数中如果取到则构建返回
         */
        //从入参中取得当前的结果集
        Object inparameter = getParamFromInParams(paramName, inParams);
        //如果没有从入参中取到，则上面执行的结果集中取出
        if (null == inparameter)
            inparameter = getParamFromCmdResult(paramName);
        // 如果也没有取到则说明指定的结果集不存在
        if (null == inparameter)
            throw new UnsupportedOperationException("参数集合" + paramName + " 未定义！");
        return inparameter;
    }

    /**
     * 从参数中取出集合
     *
     * @param paramName
     * @param inParams
     * @return
     */
    public Object getParamFromInParams(String paramName, net.sf.json.JSONObject inParams) {
        List<Map<String, Object>> list = null;
        if (getParametersMap().containsKey(paramName)) {
            Parameters parameters = getParametersMap().get(paramName);
            if (parameters.islist()) {
                net.sf.json.JSONArray inParamArray = inParams.getJSONArray(paramName);
                list = (List<Map<String, Object>>) inParamArray;
            } else {
                net.sf.json.JSONObject inParamObject = inParams.getJSONObject(paramName);
                Map map = (Map) inParamObject;
                list = new ArrayList<>();
                list.add(map);
            }
        }
        if (null != list)
            logger.debug(paramName + "来源于参数.");
        return list;
    }

    /**
     * 复制当前对象
     *
     * @return
     */
    public XmlBusiConfig myClone() throws Exception {
        XmlBusiConfig xmlBusiConfig = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        // 将流序列化成对象
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        xmlBusiConfig = (XmlBusiConfig) ois.readObject();
        return xmlBusiConfig;
    }

    /**
     * 获取属性的实际值
     *
     * @param cmd
     * @param expression
     * @return
     */
    public String getValue(Action cmd, String expression) {
        String result = "";
        if (!StringHelper.isNotNull(expression)) return result;
        boolean normalParams = expression.startsWith("#{");
        boolean systemParams = expression.startsWith("${");
        boolean pointParams = expression.indexOf(".") == -1 ? false : true;
        if (normalParams) {
            String expName = expression.substring(expression.indexOf("{") + 1, expression.lastIndexOf("}"));
            //常规变量
            if (pointParams) {
                //作用域外变量
                String[] args = expName.split("\\.");
                String firstName = args[0];
                String secondName = args[1];
                List<Map<String, Object>> list = TypeUtil.changeToListMap(getParamsFromInParams_CmdResult(firstName, this.inParams));
                if (null == list)
                    throw new UnsupportedOperationException(cmd.getId() + "的参数" + firstName + "未定义!");
                if (list.size() != 1)
                    throw new UnsupportedOperationException(cmd.getId() + "的参数" + firstName + "大小不为1,实际值" + list.size());
                Map<String, Object> map = list.get(0);
                if (!map.containsKey(secondName))
                    throw new UnsupportedOperationException(cmd.getId() + "的入参" + firstName + "不包含" + secondName + "属性");
                result = StringHelper.toString(map.get(secondName));
            } else {
                //作用域内变量
                String firstName = cmd.getParams();
                String secondName = expName;
                List<Map<String, Object>> list = TypeUtil.changeToListMap(getParamsFromInParams_CmdResult(firstName, this.inParams));
                if (null == list)
                    throw new UnsupportedOperationException(cmd.getId() + "的参数" + firstName + "未定义!");
                if (list.size() != 1)
                    throw new UnsupportedOperationException(cmd.getId() + "的参数" + firstName + "大小不为1,实际值" + list.size());
                Map<String, Object> map = list.get(0);
                if (!map.containsKey(secondName))
                    throw new UnsupportedOperationException(cmd.getId() + "的入参" + firstName + "不包含" + secondName + "属性");
                result = map.get(secondName) + "";
            }
        } else if (systemParams) {
            //系统变量
            String expName = expression.substring(expression.indexOf("{") + 1, expression.lastIndexOf("}"));
            result = SystemFunction.get(expName);
        } else {
            result = expression;
        }
        return result;
    }
}
