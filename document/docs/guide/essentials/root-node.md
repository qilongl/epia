# 根节点

接口以XML文件为单位，XML是一种树结构，它从"根部"开始，然后扩展到"枝叶"。为了让接口更好的分类与识别，您可以根据项目名或者自定义根节点名称。根节点包含了 id、datasource、name、desc 四个属性

```xml
<Function id="test1" datasource="dataSource" name="测试" desc="测试">
```
::: tip 提示
根节点名称可自定义命名
:::
### Attributes

参数 | 说明  | 是否必填
--|--|--
id          | 接口文件名、接口名    |是
datasource  | 接口数据源名称        |是
name        | 接口名称             |否
desc        | 接口描述             |否
<br>

