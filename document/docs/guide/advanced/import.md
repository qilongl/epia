# IMPORT 引用其他业务配置接口

## 无参数

`被调用接口`

```xml
<Function id="test1" datasource="dataSource" name="测试接口1" desc="测试接口">
    <Parameters>
    </Parameters>
    <Actions>
        <select id="userInfo" isreturn="true" errorid="123" msg="出错啦!!!">
            select 1 as test from dual
        </select>
    </Actions>
</Function>
```

调用上面配置接口

```xml
<Function id="test2" datasource="dataSource" name="引用测试接口" desc="引用测试接口">
    <Parameters>
    </Parameters>
    <Actions>
        <!-- source ： 引用的配置接口目录 -->
        <import id="import2" source="base-test-test1" errorid="456"></import>
    </Actions>
</Function>
```

## 有参数

`被调用接口`

```xml
<Function id="test1" datasource="dataSource" name="测试接口1" desc="测试接口">
    <Parameters>
        <!-- 注意：被调用的接口接受的参数需为数组类型 -->
        <test islist="true">
            <name></name>
        </test>
    </Parameters>
    <Actions>
        <select id="userInfo" isreturn="true" params="test" errorid="123">
            select * from sys_user where name = #{name}
        </select>
    </Actions>
</Function>
```

调用上面配置接口，被调用接口参数作为`<import>`子元素传入

```xml
<Function id="test2" datasource="dataSource" name="引用测试接口" desc="引用测试接口">
    <Parameters>
        <test>
            <name></name>
        </test>
    </Parameters>
    <Actions>
        <!--构建参数 var命令构建的参数是一个list-->
        <var id="buildParams">
            <name value="#{test.name}"></name>
        </var>
        <!-- source ： 引用的配置接口目录 -->
        <import id="import2" source="base-test-test1" errorid="456">
            <test params="buildPrams"></test>
        </import>
    </Actions>
</Function>
```

<hr>

多参数

```xml
<Function id="test1" datasource="dataSource" name="测试接口1" desc="测试接口">
    <Parameters>
        <!-- 注意：被调用的接口接受的参数需为数组类型 -->
        <test islist="true">
            <name></name>
        </test>
        <test1 islist="true">
            <id></id>
        </test1>
    </Parameters>
    <Actions>
        <select id="userInfo" isreturn="true" params="test" errorid="123">
            select * from sys_user where name = #{name} and id =#{test1.id}
        </select>
    </Actions>
</Function>
```

调用上面配置接口，被调用接口参数作为`<import>`子元素传入

```xml
<Function id="test2" datasource="dataSource" name="引用测试接口" desc="引用测试接口">
    <Parameters>
        <test>
            <name></name>
        </test>
    </Parameters>
    <Actions>
        <select id="getIdByName" isreturn="true" params="test" errorid="123">
            select ID from sys_user where name = #{name}
        </select>
        <!--构建参数 var命令构建的参数是一个list-->
        <var id="buildParams">
            <name value="#{test.name}"></name>
        </var>
        <var id="buildPrams1" isreturn="true">
            <id value="#{getIdByName.ID}"></id>
        </var>
        <!-- source ： 引用的配置接口目录 -->
        <import id="import2" source="base-test-test1" errorid="456">
            <test params="buildPrams"></test>
            <test1 params="buildPrams1"></test1>
            <!-- ... 等等 -->
        </import>
    </Actions>
</Function>
```

::: warning 警告
被调用的配置接口中不要将命令的 id 与调用者接口中命令 id 发生重复
:::

## Attributes

| 参数    | 说明               | 是否必填 |
| ------- | ------------------ | -------- |
| id      | 命令标签唯一标识   | 是       |
| source  | 引用的配置接口目录   | 是       |
| errorid | 自定义错误状态码   | 否       |
| msg     | 自定义异常提示信息 | 否       |
