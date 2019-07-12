# INSERT-UPDATE-DELETE 增、改、删

## 对象类

### insert

```xml
<Parameters>
    <test>
        <name/>
    </test>
</Parameters>
<Actions>
    <insert id="insert" params="test" isreturn="true">
        <![CDATA[
            insert into test values(sys_id.nextval,sys_guid(),#{name},sysdate)
        ]]>
    </insert>
</Actions>
```

### update

```xml
<Parameters>
    <test>
        <id/>
        <name/>
    </test>
</Parameters>
<Actions>
    <update id="update" params="test" isreturn="true" errorid="123">
        <![CDATA[
            update test set name =#{name} where id =#{id}
        ]]>
    </update>
</Actions>
```

### delete

```xml
<Parameters>
    <test>
        <id/>
    </test>
</Parameters>
<Actions>
    <delete id="delete" params="test" isreturn="true" errorid="123" msg="删除时异常！">
        <![CDATA[
            delete from  test where id =#{id}
        ]]>
    </delete>
</Actions>
```

## 数组类（批量操作）

如果参数为数组，则根据数组大小做批量操作

```xml
<Parameters>
    <test4 islist="true">
        <name/>
        <id/>
    </test4>
</Parameters>
<Actions>
    <insert id="insert" params="test" isreturn="true">
        <![CDATA[
            insert into test values(sys_id.nextval,sys_guid(),#{name},sysdate)
        ]]>
    </insert>
    <update id="Retrieve6" params="test4" isreturn="true" errorid="3123">
        <![CDATA[
            insert into test values(sys_id.nextval,sys_guid(),#{name},sysdate)
        ]]>
    </update>
    <delete id="delete" params="test" isreturn="true" errorid="123" msg="删除时异常！">
        <![CDATA[
            delete from  test where id =#{id}
        ]]>
    </delete>
</Actions>
```

## 参数集为其他命令

如果参数为为其他命令结果集时，`islist`属性决定是否执行批量操作  
[var 命令](./var.md)

```xml
<Parameters>
    <test4 islist="true">
        <name/>
        <id/>
    </test4>
</Parameters>
<Actions>
    <select id="select1" isreturn="true">
            select ID,NAME from table1
    </select>
    <var id='test3' params='select1' isreturn="true" errorid="123">
        <id value="#{ID}"></id>
        <createId value="#{ID}"></createId>
        <createDate value="${date}"></createDate>
    </var>
    <!-- 只会插入一条 -->
     <insert id="insert" params="test3">
        insert into log(ID,CREATEID,CREATEDATE) values(#{id},#{createId},#{createDate})
    </insert>
    <!-- 根据参数集批量操作 -->
    <insert id="insert" params="test3" islist="true">
        insert into log(ID,CREATEID,CREATEDATE) values(#{id},#{createId},#{createDate})
    </insert>
</Actions>
```

::: tip 提示
`<insert/>` `<update/>` `<delete/>` 命令标签内部实现是一样的，你可以在这几个标签中执行更新操作（即这三个标签都可执行增删改 SQL）,但为了规范以及后期接口维护，建议合理使用标签名
:::
<br>

## Attributes

| 参数     | 说明                                                                                       | 是否必填 |
| -------- | ------------------------------------------------------------------------------------------ | -------- |
| id       | 命令标签唯一标识                                                                           | 是       |
| params   | 引用参数                                                                                   | 否       |
| isreturn | 是否返还结果集                                                                             | 否       |
| errorid  | 自定义错误状态码                                                                           | 否       |
| msg      | 自定义异常提示信息                                                                         | 否       |
| islist   | 是否依据参数批量执行（当参数集为其他命令结果集时，是否批量操作取决于标签的 `islist` 属性） | 否       |

---
