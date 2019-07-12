# IF 条件表达式

开发怎么能少了 if 表达式:smirk:  
`if`标签中 `test` 属性用来判断表达式
表达式求值引擎采用的是[Aviator](http://fnil.net/aviator/)
::: tip 提示
`test` 表达式引用的参数如果为数组，要保证数组大小为 1
:::
话不多说，直接看实例：

## 基础 `<if test="#{test.test}=='xxx'">`

```xml
<Parameters>
    <test>
        <test></test>
    </test>
</Parameters>
<Actions>
    <!-- 遍历参数键值对 -->
    <var id="paramsInfo" isreturn="true" params="test">
        <attriterator>
            <paramName value="{name}"></paramName>
            <paramValue value="{value}"></paramValue>
        </attriterator>
    </var>
    <if test="#{test.test}=='1'">
        <select id="check" isreturn="true" params="paramsInfo">
            select '参数'||#{paramName}||'的值为：'||#{paramValue} as RESULT from dual
        </select>
    </if>
    <if test="#{test.test}!='1'">
        <select id="check" isreturn="true" params="paramsInfo">
            select '校验未通过! 入参为：'||'参数'||#{paramName}||'的值为：'||#{paramValue} as RESULT from dual
        </select>
    </if>
</Actions>
```

## 多参数引用 `<if test="#{test.test}==#{test.name}">`

```xml
<Parameters>
    <test>
        <test></test>
        <name></name>
    </test>
</Parameters>
<Actions>
    <if test="#{test.test}==#{test.name}">
        <select id="check" isreturn="true">
            select #{test.test} as test, #{test.name} as name from dual
        </select>
    </if>
</Actions>
```

## 与`&&`

xml 中特殊字符需转义 `&`-->`&amp;`  
eg:`<if test="#{check.ID}=='1'&amp;&amp;#{check.name}=='刘奇龙'">`

```xml
<Parameters>
    <test>
        <test></test>
        <name></name>
    </test>
</Parameters>
<Actions>
    <select id="check" isreturn="true"  expect="1">
        select ID,NAME FROM sys_user where id=1
    </select>
    <!-- xml中特殊字符转义下 &->&amp; -->
    <if test="#{check.ID}=='1'&amp;&amp;#{check.name}=='刘奇龙'">
        <select id="result" isreturn="true">
            select #{check.ID} as  ID,#{check.name} as NAME FROM dual
        </select>
    </if>
</Actions>
```

## 或`||`

eg:`<if test="#{check.ID}=='1' || #{check.name}=='刘奇龙'">`

```xml
<Parameters>
    <test>
        <test></test>
        <name></name>
    </test>
</Parameters>
<Actions>
    <select id="check" isreturn="true"  expect="1">
        select ID,NAME FROM sys_user where id=1
    </select>
    <!-- xml中特殊字符转义下 &->&amp; -->
    <if test="#{check.ID}=='1'||#{check.name}=='刘奇龙'">
        <select id="result" isreturn="true">
            select #{check.ID} as  ID,#{check.name} as NAME FROM dual
        </select>
    </if>
</Actions>
```

`if`表达式标签主要做简单的判断，如需更多用法，请参考表达式引擎[Aviator](http://fnil.net/aviator/)

## Attributes

| 参数 | 说明          | 是否必填 |
| ---- | ------------- | -------- |
| test | if 条件表达式 | 是       |
