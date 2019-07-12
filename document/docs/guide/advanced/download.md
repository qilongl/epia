# DOWNLOAD 文件下载

附件下载，excel 导出。。。

## 根据`<createfile/>`下载

```xml
<Parameters>
    <params>
        <isDownload default=""></isDownload>
    </params>
</Parameters>
<Actions>
    <!--下载查询的结果集-->
    <select id="functionList" isreturn="true">
        select * from sys_function
    </select>
    <if test="#{params.isDownload}=='1'">
        <!--1、创建临时文件-->
        <createfile id="create1" params="functionList" filename="接口列表.xlsx"></createfile>
        <!--2、根据临时文件下载附件-->
        <download id="download" params="create1"></download>
        <!--3、删除临时文件-->
        <deletefile id="deleteFile4" params="create1"></deletefile>
    </if>
</Actions>
```

## 指定文件路径

> `PATH`、 `ORINAME` 均为必填参数

### 默认名称

```xml
<Actions>
    <!--从指定目录下载-->
    <var id="file1" isreturn="true">
        <!-- 文件路径 -->
        <PATH value="f:\\temp\\测试2019-06-03.TXT"></PATH>
        <!-- 下载的文件名 -->
        <ORINAME value="${guid}.txt"></ORINAME>
    </var>
    <download id="download" params="file1" errorid="404"></download>
</Actions>
```

### 自定义名称

如果想确定下载文件的名称，可使用`filename` 属性

```xml
<Actions>
    <!--从指定目录下载-->
    <var id="file1" isreturn="true">
        <!-- 文件路径 -->
        <PATH value="f:\\temp\\测试2019-06-03.TXT"></PATH>
        <!-- 下载的文件名 -->
        <ORINAME value="${guid}.txt"></ORINAME>
    </var>
    <download id="download" filename="123.txt" params="file1" errorid="404"></download>
</Actions>
```

## 多文件下载

### 默认名称

```xml
<Actions>
    <!--从指定目录下载多文件，下载文件名 ："第一个文件名+文件数.zip"   demo+2.zip -->
    <select id="file1" isreturn="true">
        select 'f:\\temp\\demo.xlsx' as PATH,'demo.xlsx' as ORINAME from dual
        union
        select 'f:\\temp\\接口列表.xlsx' as PATH,'接口列表.xlsx' as ORINAME from dual
    </select>
    <download id="download" params="file1"></download>
</Actions>
```

### 自定义名称格式

```xml
<Actions>
    <select id="file1" isreturn="true">
        select 'f:\\temp\\demo.xlsx' as PATH,'demo.xlsx' as ORINAME from dual
        union
        select 'f:\\temp\\接口列表.xlsx' as PATH,'接口列表.xlsx' as ORINAME from dual
    </select>
    <download id="download" filename="test.rar" params="file1"></download>
</Actions>
```

## Attributes

| 参数     | 说明               | 是否必填 |
| -------- | ------------------ | -------- |
| id       | 命令标签唯一标识   | 是       |
| params   | 引用参数           | 是       |
| msg      | 自定义异常提示信息 | 否       |
| errorid  | 自定义错误状态码   | 否       |
| filename | 文件名             | 否       |
