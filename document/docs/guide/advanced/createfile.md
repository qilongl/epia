# CREATEFILE 文件创建

## 基础

### 不指定目录

一般不指定目录主要用于生成临时文件,不需要指定各种属性，只需要指定入参集合就可以了

```xml
<Actions>
    <select id="userInfo" isreturn="false">
        select * from sys_function
    </select>
    <!--不指定目录时，存储路径使用系统默认附件目录 文件名为32位随机字符串（uuid）,临时文件默认为 .txt类型-->
    <createfile id="writeTxt1" params="userInfo"  errorid="222"/>
    <!--如果想指定文件名，使用 （filename） 属性-->
    <createfile id="writeTxt1" filename="测试.txt" params="userInfo"  errorid="222"/>
</Actions>
```

### 指定目录

指定文件存储目录，`basesavepath`，`savepath`

::: tip 提示

> 指定目录时

| 属性         | 说明               | 是否必填 |
| ------------ | ------------------ | -------- |
| basesavepath | 文件存储根目录     | 是       |
| savepath     | 文件存储的相对目录 | 否       |

:::

```xml
<Actions>
    <select id="userInfo" isreturn="false">
        select * from sys_function
    </select>
    <createfile id="writeTxt2" filename="测试20190115.txt" params="userInfo"  basesavepath="f:" savepath="temp" errorid="111"  isreturn="true"/>
</Actions>
```

### 参数引用

有时我们需要根据日期来生成文件名、存储路径从库中动态读取时就不能写静态属性了，而是去引用参数
`filename`、 `basesavepath` 、`savepath` 均可从参数中引用

```xml
<Actions>
    <select id="userInfo" isreturn="false">
        select * from sys_function
    </select>
    <var id="fileName" isreturn="true">
        <fileName value="测试${date}.TXT"></fileName>
    </var>
    <createfile id="writeTxt2" filename="#{fileName.fileName}" params="userInfo"  basesavepath="f:" savepath="temp" errorid="111"  isreturn="true"/>
</Actions>
```

<hr/>

::: tip 提示
`<createfile/>` 标签的返还结果集中包含生成文件的路径和文件名

```json
"writeTxt2":[{"PATH":"f:\\temp\\测试2019-06-03.TXT","ORINAME":"测试2019-06-03.TXT"}]
```

该结果集可直接作为`<deletefile/>`的入参，请参考 [deletefile 标签](./deletefile.md)
:::

## Attributes

| 参数         | 说明                                             | 是否必填 |
| ------------ | ------------------------------------------------ | -------- |
| id           | 命令标签唯一标识                                 | 是       |
| params       | 引用参数                                         | 是       |
| basesavepath | 文件存储根目录                                   | 否       |
| savepath     | 文件存储的相对目录                               | 否       |
| filename     | 文件名 (目前支持的文件类型：.txt / .xls / .xlsx) | 否       |
| msg          | 自定义异常提示信息                               | 否       |
| errorid      | 自定义错误状态码                                 | 否       |
