# VAR 自定义变量

接收的参数并不是每次都刚好是我们所要的结构，为了构建接口所需的参数，可以自定义变量来组装参数

## 基础类

### 常量

```xml
<Actions>
    <var id="const" isreturn="true">
        <name value="张三"></name>
        <age value="18"></age>
    </var>
</Actions>
```

### 变量

```xml
<Actions>
    <var id="bianliang" isreturn="true">
        <name value="张三"></name>
        <age value="18"></age>
    </var>
    <!-- 指定默认参数直接引用 -->
    <var id="bianliang2" params="bianliang">
        <name value="李四"></name>
        <age value="#{age}"></age>
    </var>
    <!-- 不指定默认参数 -->
    <var id="bianliang3" isreturn="false">
        <name value="#{bianliang.name}"></name>
        <name2 value="#{bianliang2.name}"></name2>
        <date value="北京时间：${date}"></date><!-- 系统参数 -->
    </var>
</Actions>
```

### 简易型

```xml
<Parameters>
    <test>
        <test></test>
    </test>
</Parameters>
<Actions>
    <!--注意 定义常量字符串时，将使用 “value”作为键名 -->
    <!-- "test1":[{"value":"一个常量"}] -->
    <var id='test1' value="一个常量" isreturn="true"></var>

    <!-- "test2":[{"date":"2019-05-30 13:32:52"}] -->
    <var id='test2' value="${date}" isreturn="true"></var>

    <!-- "test3":[{"test":"7"}] -->
    <var id='test3' value="#{test.test}" isreturn="true"></var>
</Actions>
```

如果想自定义键名，值引用其他参数，可以如下

```xml
<Actions>
    <var id='test2' params='test1' isreturn="true">
        <test value="引用参数后再自定义一个常量"></test>
    </var>
</Actions>
```

### 参数引用
var 命令标签中 value 的引用参数可以是：自定义字符串常量、入参、其他命令结果集参数

> 自定义字符串常量、入参、系统参数

```xml
<Parameters>
    <test>
        <test></test>
        <test2 default="${date}"></test2>
    </test>
</Parameters>
<Actions>
    <var id='test2' params='test' isreturn="true">
        <test value="自定义字符串常量"></test>
        <test2 value="#{test2}"></test2><!-- 引用参数集中的参数 -->
        <test3 value="${guid}"></test3><!-- 引用系统参数 -->
    </var>
</Actions>
```

> 其他命令结果集参数

```xml
<Actions>
    <select id="select1">
        select ID,NAME from table1
    </select>
    <!-- 改变 select1 标签结果集的键名  ID->idQQ NAME->nameQQ -->
    <var id='test3' params='select1' isreturn="true" errorid="123">
        <idQQ value="#{ID}"></idQQ>
        <nameQQ value="#{NAME}"></nameQQ>
    </var>
    <!-- 根据 select1 组装参数-->
    <var id='test3' params='select1' isreturn="true" errorid="123">
        <id value="#{ID}"></id>
        <createId value="#{ID}"></createId>
        <createDate value="${date}"></createDate>
    </var>
    <!-- 根据组装的参数做批量操作 -->
    <insert id="insert" params="test3" islist="true">
        insert into log(ID,CREATEID,CREATEDATE) values(#{id},#{createId},#{createDate})
    </insert>
</Actions>
```

<br>

## 构建类

> range 属性：循环次数

### 根据参数对象构建数组

```xml
<Parameters>
    <test>
        <test></test>
        <test2 default="${date}"></test2>
    </test>
</Parameters>
<Actions>
    <!--range="2" 循环次数-->
    <var id='testVar1'  range="2" isreturn="true" errorid="456">
        <tablename value='reports'></tablename>
        <creator value='${guid}'></creator>
        <intime value='${fulldate}'></intime>
    </var>
    <!--range="3" 循环次数-->
    <var id='testVar1' params='test' range="3" isreturn="true">
        <tablename value='reports'></tablename>
        <creator value='${guid}'></creator>
        <intime value='${fulldate}'></intime>
        <test value='#{test}'></test>
        <test2 value='#{test2}'></test2>
    </var>
</Actions>
```

### 根据参数数组构建数组

```xml
<Parameters>
    <addparams islist="true">
        <test></test>
    </addparams>
</Parameters>
<Actions>
    <!--当参数为数组时，结果集大小为 “数组大小” x “range”-->
    <var id='testVar1' params='addparams' range="3" isreturn="true">
        <tablename value='reports'></tablename>
        <creator value='${guid}'></creator>
        <intime value='${fulldate}'></intime>
        <test value='#{test}'></test>
    </var>
</Actions>
```

<br>

## 遍历参数集标签`<attriterator>`

::: tip 特别说明
如需遍历参数对象中的键值对，请使用`<attriterator>` 标签，对象键使用 “{name}”,值使用“{value}”
:::

### 循环获取对象参数集中的参数标签

`<attriterator>` 遍历对象中的 key,value

```xml
<Parameters>
    <test>
        <test></test>
        <test2 default="${date}"></test2>
    </test>
</Parameters>
<Actions>
    <!-- 返还的结果集大小为参数 test对象的大小 -->
    <var id='testVar' params='test' isreturn="true">
        <attriterator><!--循环参数集中的每个参数标签-->
            <tablename value='reports'></tablename>
            <colsName value='{name}'></colsName><!--参数标签属性名-->
            <value value='{value}'></value><!--参数属性对应的值-->
            <creator value='${guid}'></creator>
            <intime value='${fulldate}'></intime>
        </attriterator>
    </var>
    <!-- 返还的结果集大小为参数 test对象的大小 x “range” -->
    <var id='testVar' params='test' range="2" isreturn="true">
        <attriterator><!--循环参数集中的每个参数标签-->
            <tablename value='reports'></tablename>
            <colsName value='{name}'></colsName><!--参数标签属性名-->
            <value value='{value}'></value><!--参数属性对应的值-->
            <creator value='${guid}'></creator>
            <intime value='${fulldate}'></intime>
        </attriterator>
    </var>
</Actions>
```

### 循环获取数组参数集中的参数标签

```xml
<Parameters>
    <addparams islist="true">
        <test></test>
    </addparams>
</Parameters>
<Actions>
    <!-- 返还的结果集大小为参数 addparams数组的大小 x "数组中对象的大小" -->
    <var id='testVar2' params='addparams' isreturn="true">
        <attriterator><!--循环参数集中的每个参数标签-->
            <tablename value='reports'></tablename>
            <colsName value='{name}'></colsName><!--参数标签属性名-->
            <value value='{value}'></value><!--参数属性对应的值-->
            <creator value='${guid}'></creator>
            <intime value='${fulldate}'></intime>
        </attriterator>
    </var>
    <!-- 返还的结果集大小为参数 addparams数组的大小 x "数组中对象的大小" x “range” -->
    <var id='testVar' params='addparams' range="2" isreturn="true">
        <attriterator><!--循环参数集中的每个参数标签-->
            <tablename value='reports'></tablename>
            <colsName value='{name}'></colsName><!--参数标签属性名-->
            <value value='{value}'></value><!--参数属性对应的值-->
            <creator value='${guid}'></creator>
            <intime value='${fulldate}'></intime>
        </attriterator>
    </var>
</Actions>
```

<br>

## Attributes

| 参数     | 说明                                         | 是否必填 |
| -------- | -------------------------------------------- | -------- |
| id       | 命令标签唯一标识                             | 是       |
| params   | 引用参数                                     | 否       |
| isreturn | 是否返还结果集                               | 否       |
| errorid  | 自定义错误状态码                             | 否       |
| msg      | 自定义异常提示信息                           | 否       |
| expect   | 期望结果集校验                               | 否       |
| range    | 循环次数                                     | 否       |
| {name}   | 对象键 （只在 `<attriterator/>` 标签中生效） | 否       |
| {value}  | 对象值 （只在 `<attriterator/>` 标签中生效） | 否       |

---
