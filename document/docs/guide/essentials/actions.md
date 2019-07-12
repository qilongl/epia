# 命令

框架内置了一些组件，用于处理业务逻辑与实现。目前框架包含了如下组件：

> [SELECT](../advanced/select.md) 查询

```xml
<Actions>
    <select id="Retrieve1" isreturn="true">
        <![CDATA[
        SELECT  1 as ID ,to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time  ,rawtohex(sys_guid()) as UUID  FROM dual
        ]]>
    </select>
</Actions>
```

<br>

> [INSERT/UPDATE/DELETE](../advanced/insert-update-delete.md) 新增、更改、删除

```xml
<Actions>
    <insert id="insert" params="test4" isreturn="true" errorid="101">
        <![CDATA[
        insert into test values(sys_id.nextval,sys_guid(),#{name},sysdate)
        ]]>
    </insert>
    <update id="update" params="test4" isreturn="true" errorid="102">
        <![CDATA[
        update test set name =#{name} where id=#{id}
        ]]>
    </update>
    <delete id="delete" params="test4" isreturn="true" errorid="103">
        <![CDATA[
        delete from test where id=#{id}
        ]]>
    </delete>
</Actions>
```

<br>

> [VAR](../advanced/var.md) 自定义变量

```xml
<Actions>
    <var id="vara" isreturn="true">
        <name value="张三"></name>
        <age value="18"></age>
    </var>
</Actions>
```

<br>

> [SERVICE](../advanced/service.md) WebService

```xml
<Actions>
    <service id="webServiceTest" url="www.baidu.com" method="post" params="webService" isreturn="true">
        <!--统一规定入参使用params属性下面的属性值,提前构建好参数集合-->
        <userId params="userId"></userId>
    </service>
</Actions>
```

::: tip 提示
需提前构建好参数集合，例：

```xml
<Parameters>
    <webService>
        <userId default="123"></userId>
    </webService>
</Parameters>
```

:::
<br>

> [REST](../advanced/rest.md) rest 请求

```xml
<Actions>
    <rest id="RestGet" method="get" errorid="cccc"
          url='http://localhost:8888/epia/easyComponentService?FunctionName=base-test-test16&amp;test-userName="刘奇龙"&amp;info-userName=lql'>
    </rest>
</Actions>
```

<br>

> [CONVERT](../advanced/convert.md) 转换器

```xml
<Actions>
    <convert id="RestGetResult" classpath="com.easipass.epia.converter.StringObjectToListMapConverter"
     params="RestGet" isreturn="true"></convert>
</Actions>
```

::: tip 提示
<convert/>是转换标签，不单独使用。如有需求，可根据具体业务拓展转换标签
:::
<br>

> [IMPORT](../advanced/import.md) 引入其他配置接口

```xml
<Actions>
    <import id="import2" source="base-test-test1" errorid="import1error" isreturn="true">
        <test params="buildPrams"></test>
    </import>
</Actions>
```

::: tip 提示

```xml
<Actions>
    <!--构建参数    var命令构建的参数是一个list-->
    <var id="buildPrams" isreturn="true">
        <test value="1"></test>
        <test2 value=""></test2>
    </var>
</Actions>
```

调用其它配置接口需先构建好该接口所需参数
::: warning 警告
被调用的接口参数均需接收 List 参数
:::
<br>

> [IF](../advanced/if.md) IF 条件判断

```xml
<Actions>
    <if test="#{test.test}=='1'">
        <select id="check" isreturn="true">
            select '校验通过！' as RESULT from dual
        </select>
    </if>
    <if test="#{test.test}!='1'">
        <select id="check" isreturn="true">
            select '校验未通过! as RESULT from dual
        </select>
        <insert id="insert">
            insert into log values(......)
        </insert>
    </if>
</Actions>
```

<br>

> [ERROR](../advanced/error.md) 自定义异常

```xml
<Actions>
    <select id="check" params="test">
        SELECT COUNT(1) AS TOTAL FROM SYS_USER WHERE OBJNAME = #{userName}
    </select>
    <if test="#{check.TOTAL}=='1'">
        <select id="userInfo" params="test" isreturn="true">
            select * from sys_user su where su.objname = #{userName}
        </select>
    </if>
    <if test="#{check.TOTAL}=='0'">
        <error id="error" msg="#{test.userName} 用户不存在" errorid="500"></error>
    </if>
    <if test="#{check.TOTAL}>'1'">
        <error id="error" msg="#{test.userName} 用户不合法，请联系管理员" errorid="500"></error>
    </if>
</Actions>
```

<br>

> [CREATEFILE](../advanced/createfile.md) 文件创建

```xml
<Actions>
    <select id="userInfo" params="test" isreturn="true">
            select * from sys_user su where su.objname = #{userName}
    </select>
    <!--创建xml文件时需指定propertyname属性 且参数列表中存在 propertyname 对应的属性-->
    <var id="fileName" isreturn="true">
        <fileName value="${guid}.txt"></fileName>
    </var>
    <createfile id="QWWQR"  params="userInfo"  filename="#{fileName.fileName}" isreturn="true"/>
</Actions>
```

<br>

> [DOWNLOAD](../advanced/download.md) 下载

```xml
<Actions>
    <!--一个接口中仅有一个下载任务，如需多文件下载，请组装参数，文件下载zip格式 ，请参考demo2.xml-->
    <!--下载查询的结果集-->
    <select id="functionList" isreturn="true">
        select * from sys_function
    </select>
    <if test="#{params.isDownload}!=''">
        <!--1、创建临时文件-->
        <createfile id="create1" params="functionList" filename="接口列表.xlsx"></createfile>
        <!--2、根据临时文件下载附件-->
        <download id="download" params="create1"></download>
        <!--3、删除临时文件-->
        <deletefile id="deleteFile4" params="create1"></deletefile>
    </if>
</Actions>
```

<br>

> [DELETEFILE](../advanced/deletefile.md) 文件删除

```xml
<Actions>
    <var id="file1" isreturn="true">
        <PATH value="F:\temp\1.txt"></PATH>
    </var>
    <var id="file2" isreturn="true">
        <PATH value="F:\\temp\\2.txt"></PATH>
    </var>
    <var id="file3" isreturn="true">
        <PATH value="F:/temp/3.txt"></PATH>
    </var>
    <var id="file4" isreturn="true">
        <PATH value="F://temp//4.txt"></PATH>
    </var>
    <deletefile id="deleteFile1" params="file1" isreturn="true"></deletefile>
    <deletefile id="deleteFile2" params="file2" isreturn="true"></deletefile>
    <deletefile id="deleteFile3" params="file3" isreturn="true"></deletefile>
    <deletefile id="deleteFile4" params="file4" isreturn="true"></deletefile>
</Actions>
```

<br>

## Attributes

目前框架所有命令标签属性，均以`string`类型参数接收，具体标签所对应的可用属性请到响应标签底部属性查看

| 参数       | 说明                 | 是否必填 | 可选值             |
| ---------- | -------------------- | -------- | ------------------ |
| id         | 命令标签唯一标识     | 是       |
| params     | 引用参数             | 否       |
| isreturn   | 是否返还结果集       | 否       | true/false         |
| expect     | 预期结果集大小       | 否       |
| errorid    | 自定义错误状态码     | 否       |
| msg        | 自定义异常提示信息   | 否       |
| islist     | 是否依据参数批量执行 | 否       | true/false         |
| range      | 循环次数             | 否       |
| returntype | 返回数据的方式       | 否       | code-tree/pid-tree |
| ispaging   | 是否分页             | 否       | true/false         |
