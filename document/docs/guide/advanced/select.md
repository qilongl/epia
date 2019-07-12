# SELECT 查询

## 无参查询

### 有返还值

```xml
<Actions>
    <select id="select1" isreturn="true">
        <![CDATA[
        SELECT  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time,sys_guid() as UUID  FROM dual
        ]]>
    </select>
</Actions>
```

### 无返还值

```xml
<Actions>
    <select id="select2" isreturn="false">
        <![CDATA[
        SELECT  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time,sys_guid() as UUID  FROM dual
        ]]>
    </select>
    <!--或者直接省略isreturn属性-->
    <select id="select3">
        <![CDATA[
        SELECT  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time,sys_guid() as UUID  FROM dual
        ]]>
    </select>
</Actions>
```

<br>

## 有参查询

### 不指定默认参数集

```xml
<!-- 入单定义 -->
<Parameters>
    <test>
        <test></test>
        <test2 default=""></test2>
    </test>
    <test2>
        <test></test>
        <test2 default="123"></test2>
    </test2>
</Parameters>

<Actions>
    <!--不指定默认参数集，直接引用 #{参数集.参数名}-->
    <select id="Retrieve4" isreturn="true">
        <![CDATA[
        SELECT #{test.test} as test,#{test2.test2} as test2 FROM dual
        ]]>
    </select>
</Actions>
```

### 指定默认参数集

```xml
<!-- 入单定义 -->
<Parameters>
    <test>
        <test></test>
        <test2 default=""></test2>
    </test>
    <test2>
        <test></test>
        <test2 default="123"></test2>
    </test2>
</Parameters>

<Actions>
    <!--指定默认参数集，可直接引用参数集中的参数名 #{test}、#{test2}-->
    <select id="Retrieve5" params="test" isreturn="true">
        <![CDATA[
        SELECT #{test} as NAME1,#{test2.test} as Name FROM dual
        ]]>
    </select>
</Actions>
```

### 数组类参数集引用

```xml
<!-- 入单定义 -->
<Parameters>
    <!--数组类参数必须设置必填参数，否则默认值无法确定数组大小-->
    <test4 islist="true">
        <name></name>
    </test4>
</Parameters>

<Actions>
    <!--默认参数集是数组时，取数组最后一个对象，数组参数集无法直接引用，必须指定为默认参数集-->
    <select id="Retrieve6" params="test4" isreturn="true">
        <![CDATA[
        SELECT 'Retrieve6' as ID, to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time  ,rawtohex(sys_guid()) as UUID ,#{name} as Name FROM dual
        ]]>
    </select>
</Actions>
```

### 引用其他标签结果集

::: tip 提示
`<select/>`标签引用其他标签结果集只会引用结果集最后一条数据对象
:::

```xml
<!-- 入单定义 -->
<Parameters>
    <!--数组类参数必须设置必填参数，否则默认值无法确定数组大小-->
    <test4 islist="true">
        <name></name>
    </test4>
</Parameters>

<Actions>
    <select id="Retrieve6" params="test4" isreturn="true">
        <![CDATA[
        SELECT 'Retrieve6' as ID, to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time  ,rawtohex(sys_guid()) as UUID ,#{name} as Name FROM dual
        ]]>
    </select>
    <!--引用其他结果集作为入参 指定默认参数集-->
    <select id="Retrieve7" params="Retrieve6" isreturn="true">
        ![CDATA[
        ELECT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time  ,rawtohex(sys_guid()) as UUID ,#{ID} as Name FROM dual
        ]>
    </select>
    <!--直接引用 #{参数集.参数名}-->
    <select id="Retrieve8" isreturn="true">
        <![CDATA[
        SELECT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time  ,rawtohex(sys_guid()) as UUID ,#{Retrieve7.NAME} as Name FROM dual
        ]]>
    </select>
</Actions>
```

## 期望结果集大小校验

::: tip 提示
实际结果集大小与预期结果集大小不符时异常，多用于判断
:::

```xml
<Actions>
    <!-- 异常 -->
    <select id="Retrieve9" isreturn="true" expect="1">
        <![CDATA[
        SELECT 1 AS ID FROM DUAL
        UNION
        SELECT 2 AS ID FROM DUAL
        ]]>
    </select>
    <select id="Retrieve9" isreturn="true" expect="2">
        <![CDATA[
        SELECT 1 AS ID FROM DUAL
        UNION
        SELECT 2 AS ID FROM DUAL
        ]]>
    </select>
</Actions>
```

## 自定义错误状态码、异常信息

为了更加准确的标注、定位异常，可在命令标签中定义错误状态码、异常信息

> `errorid`、`msg` 属性命令标签中均可使用

```xml
<Actions>
    <!--自定义错误状态码  errorid="0": 当该标签异常时将返还该标签定义的errorid,可快速定位异常-->
    <select id="Retrieve2" isreturn="true" errorid="0">
        <![CDATA[
            SELECT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time  ,rawtohex(sys_guid()) as UUID FROM  dual
        ]]>
    </select>
    <!--自定义异常提示信息 msg="异常" :标签异常时返还自定义异常提示+异常栈信息-->
    <select id="Retrieve22" isreturn="true" errorid="0" msg="小伙子你的sql有问题哦">
        <![CDATA[
            SELECT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time  rawtohex(sys_guid()) as UUID FROM  dual
        ]]>
    </select>
</Actions>
```

## 直接引用系统参数

```xml
<Actions>
    <select id="test" params="test" isreturn="true">
        <![CDATA[
            SELECT 
            ${sysbasedir} as SYSTEM_PARAMETER ,
            ${guid} as uuid ,
            ${date} as date1,--date 是关键字，别名时请注意
            ${simpledate} as simpledate
            FROM DUAL
        ]]>
    </select>
</Actions>
```

## 结果集返还类型

`code-tree`、`pid-tree`两种返还类型

> `code-tree` 根据 code 构建父子树形结构

> `pid-tree` 根据 id、pid 构建父子树形结构

...todo

## 分页

分页需构建好参数 `PAGENUM`、`PAGESIZE`。必填参数，均为大写

```xml
<Parameters>
    <test>
        <PAGENUM/><!-- 页码 -->
        <PAGESIZE/><!-- 每页数据大小 -->
    </test>
</Parameters>
<Actions>
    <select id="test" params="test" isreturn="true">
        <![CDATA[
            select * from sys_function
        ]]>
    </select>
</Actions>
```

## 排序

```xml
<Parameters>
    <test>
        <PAGENUM/><!-- 页码 -->
        <PAGESIZE/><!-- 每页数据大小 -->
        <PROP/><!-- 排序字段 -->
        <SORT/><!-- 顺序 -->
    </test>
</Parameters>
<Actions>
    <select id="abc" isreturn="true" errorid="1" params="test" ispaging="true">
        <![CDATA[
        select * from sys_function
        <if test="#{PROP}==''">
            ORDER BY CRT_DT
        </if>
        <if test="#{PROP}=='OBJID'">
            ORDER BY OBJID
        </if>
        <if test="#{PROP}=='FUN_NAME'">
            ORDER BY FUN_NAME
        </if>
        <if test="#{SORT}=='D'">
            DESC
        </if>
        <if test="#{SORT}=='A'">
            ASC
        </if>
        ]]>
    </select>
</Actions>
```

对于以上你可能会有疑问，为什么#{PROP}要判断每个字段，不能直接像

```xml
<if test="#{PROP}==''">
    ORDER BY #{PROP}
</if>
```

这样吗？ 其实接收的参数是字符串，后台解析后为

> select \* from sys_function ORDER BY 'OBJID'

`'OBJID'`是个常量，所以这样不管正序还是倒叙都不会有变化，所以排序还是要把排序的字段都判断下。

<br>

## Attributes

| 参数       | 说明               | 是否必填 |
| ---------- | ------------------ | -------- |
| id         | 命令标签唯一标识   | 是       |
| params     | 引用参数           | 否       |
| isreturn   | 是否返还结果集     | 否       |
| errorid    | 自定义错误状态码   | 否       |
| msg        | 自定义异常提示信息 | 否       |
| expect     | 预期结果集大小     | 否       |
| returntype | 返回数据的方式     | 否       |
| ispaging   | 是否分页           | 否       |

---
