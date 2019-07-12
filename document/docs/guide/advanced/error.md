# ERROR 自定义异常标签

<p> 在阅读前面标签时你可能注意到了，我们可以在命令标签上自定义异常状态码、异常提示信息。异常状态码和异常提示信息会在命令标签异常时覆盖掉框架统一的异常状态码（500）,异常信息则为 自定义异常信息+程序抛出的异常信息</p>  
但有些情况下需要我们手动抛出异常，那么 `error` 标签就来了

## 基础

最简单的使用就是自定义一个异常，像之前其他标签一样`errorid`、`msg`  
但这样似乎没有任何意义，不加判断的直接抛异常，那这个接口永远是异常的，毫无意义。

```xml
<Actions>
    <error id="error" msg="没什么事，只是简单的抛出一个异常" errorid="111"></error>
</Actions>
```

为了在合适的地方抛异常，往往需要结合条件判断，结合`if`标签即可完成你想要的结果

## 结合`if`

```xml
<Parameters>
    <test>
        <userName></userName>
    </test>
</Parameters>
<Actions>
    <select id="check" params="test">
        SELECT COUNT(1) AS TOTAL FROM SYS_USER WHERE OBJNAME = #{userName}
    </select>
    <if test="#{check.TOTAL}=='0'">
        <error id="error" msg="#{test.userName} 用户不存在" errorid="201"></error>
    </if>
    <if test="#{check.TOTAL}>'1'">
        <error id="error" msg="#{test.userName} 用户不合法，请联系管理员" errorid="202"></error>
    </if>
</Actions>
```

::: tip 提示
在其他命令标签中可以定义异常状态码和异常信息，但异常信息是无法引用参数的。  
但`error`标签的`msg`属性可以引用参数
::: warning 提示
`msg` 为必填属性，自定义异常如果没有异常信息那将毫无意义！
:::

## Attributes

| 参数    | 说明                                      | 是否必填 |
| ------- | ----------------------------------------- | -------- |
| id      | 命令标签唯一标识                          | 是       |
| msg     | 自定义异常提示信息,error 标签中可引用参数 | 是       |
| errorid | 自定义错误状态码                          | 否       |
