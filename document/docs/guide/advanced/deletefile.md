# DELETEFILE 文件删除

主要用于清除文件下载过程中生成的临时附件，也可根据指定文件路径删除

## 根据`<createfile/>`删除文件

`<createfile/>` 标签结果集中会返还生成文件的路径和文件名，`<deletefile/>` 标签可直接将`<createfile/>` 标签结果集作为入参，即可删除`<createfile/>` 标签生成的文件

```xml
<Actions>
    <select id="userInfo" isreturn="false">
        select * from sys_function
    </select>
    <!--生成临时文件-->
    <createfile id="writeTxt1"  params="userInfo"  errorid="222" isreturn="true"/>
    <!--删除临时文件-->
    <deletefile id="deleteFile1" params="writeTxt1" isreturn="true"></deletefile>
</Actions>
```

## 指定文件路径

构建参数来删除指定文件路径的文件
参数格式：数组，对象中包含“PATH”:"XXX"，遍历数组删除

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

<hr/>

::: tip 提示
`<deletefile/>` 标签的返还结果集中包含删除文件的路径（`PATH`）和是否成功删除的标识（`FLAG`）

```json
"deleteFile1":[{"PATH":"E:\\Epia\\temp\\tmp\\测试.txt","FLAG":true}]
```

:::

> 出于安全性问题，只会删除指定文件，不会删除目录

## Attributes

| 参数    | 说明               | 是否必填 |
| ------- | ------------------ | -------- |
| id      | 命令标签唯一标识   | 是       |
| params  | 引用参数           | 是       |
| msg     | 自定义异常提示信息 | 否       |
| errorid | 自定义错误状态码   | 否       |
