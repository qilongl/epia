# 参数

自定义接口参数
<br>
调用传递-----> "Parameters":{...//自定义参数}

## 对象类

参数为对象，字段必填。若参数无 required、default 属性时默认必填  
::: tip 说明
调用传递--->"Parameters":{"params":{"flag":"xxx"}}}  
接口接收--->"Parameters":{"params":{"flag":"xxx"}}}  
:::

```xml
<Parameters>
    <params islist="false">
       <flag required="true"></flag>
    </params>
</Parameters>
```
```xml
<Parameters>
    <params>
       <flag></flag>
    </params>
</Parameters>
```

---

参数为对象，字段非必填，即有默认值  
::: tip 说明
调用传递--->"Parameters":{}} //不传递时使用默认值  
调用传递--->"Parameters":{"params":{"flag":"456"}}}//传递时使用传递的值  
接口接收--->"Parameters":{"params":{"flag":"xxx"}}}  
:::

```xml
<Parameters>
    <params islist="false">
       <flag default="123"></flag>
    </params>
</Parameters>
```
```xml
<Parameters>
    <params>
       <flag default="123"></flag>
    </params>
</Parameters>
```

::: tip 提示
required 、default 属性互斥
:::

---

## 数组类

参数为数组，字段必填。数组大小由调用者控制
::: tip 说明
调用传递--->"Parameters":{"params":[{"flag":"xxx"},...]}  
接口接收--->"Parameters":{"params":[{"flag":"xxx"},...]}  
:::

```xml
<Parameters>
    <params islist="true">
       <flag></flag>
    </params>
</Parameters>
```

---

::: tip 说明
调用传递--->"Parameters":{"params":[{"flag":"xxx","name":"xxx","age":"xxx"},...]}  
接口接收--->"Parameters":{"params":[{"flag":"xxx","name":"xxx","age":"xxx"},...]}  
:::

```xml
<Parameters>
    <params islist="true">
       <flag></flag>
       <name></name>
       <age></age>
    </params>
</Parameters>
```

---

参数为数组，字段非必填，即有默认值
::: tip 说明
调用传递--->"Parameters":{"params":[{"flag":"xxx"},...]} //不传递时使用默认值  
调用传递--->"Parameters":{"params":[{"flag":"xxx","age":"xxx"},...]}//传递时使用传递的值  
接口接收--->"Parameters":{"params":[{"flag":"xxx","age":"xxx"},...]}  
:::

```xml
<Parameters>
    <params islist="true">
       <flag></flag>
       <age default="123"></flag>
    </params>
</Parameters>
```

::: tip 提示
数组参数对象中必须包含有必填参数字段，否则无法定位数组大小，无法给出默认值
:::

---

## 参数正则表达式

参数字段高级属性，正则表达式
::: tip 说明
调用传递--->"Parameters":{"test":{"test":"0"}}  
调用传递--->"Parameters":{"test":{"test":"1"}}  
:::

```xml
<Parameters>
    <test>
        <test expression="[0-1]" desc="参数test只接受0，1"></test>
    </test>
</Parameters>
```

---

::: tip 说明
调用传递--->"Parameters":{"test":[{"test":"0"},{"test":"1"}...]}  
调用传递--->"Parameters":{"test":[{"test":"0"},...]}  
:::

```xml
<Parameters>
    <test islist="true">
        <test expression="[0-1]" desc="参数test只接受0，1"></test>
    </test>
</Parameters>
```

::: tip 提示
expression:正则表达式  
desc:当正则表达式匹配是失败时的描述  
:::

---

## 混合

真实场景参数远不止上面那么简单
::: tip 说明
调用传递--->"Parameters":{
"test":{"test":"xxx","test1":"xxx","test2":"xxx","test3":"xxx"},
"test3":[{"test1":"xxx","test2":"xxx"},...],
"test4":{"test":"x"}
}  
调用传递--->"Parameters":{
"test":{"test":"xxx","test1":"xxx","test2":"xxx","test3":"xxx"},
"test2":{"test":"xxx","test1":"xxx","test2":"xxx","test3":"xxx"},
"test3":[{"test1":"xxx","test2":"xxx"},...],
"test4":{"test":"x"}
}  
接口接收--->"Parameters":{
"test":{"test":"xxx","test1":"xxx","test2":"xxx","test3":"xxx"},
"test2":{"test":"xxx","test1":"xxx","test2":"xxx","test3":"xxx"},
"test3":[{"test1":"xxx","test2":"xxx"},...],
"test4":{"test":"x"}
}  
:::

```xml
<Parameters>
    <test>
        <test/><!-- 必填  -->
        <test1/><!-- 必填  -->
        <test2/><!-- 必填  -->
        <test3/><!-- 必填  -->
    </test>
    <test2>
        <test default=""/><!-- 默认值  -->
        <test1 default="123"/><!-- 默认值  -->
        <test2 default="Tom"/><!-- 默认值  -->
        <test3 default="qawef"/><!-- 默认值  -->
    </test2>
    <test3 islist="true"><!-- 必须要有必填字段  -->
        <test1/><!-- 必填  -->
        <test2/><!-- 必填  -->
        <test default=""/><!-- 默认值  -->
        <test2 default=""/><!-- 默认值  -->
    </test3>
    <test4>
        <test expression="[0-1]" desc="参数test只接受0，1"></test><!--正则表达式  -->
    </test4>
    ...
</Parameters>
```

::: tip 提示
参数深度目前为 2 层
:::

## 系统参数

参数默认值可以直接引用系统参数、在命令中也可以直接引用，参考[命令中引用系统参数](../advanced/select#直接引用系统参数)
::: tip 说明
调用传递--->"Parameters":{"test":{"test":"xxx","test2":"xxxx-xx-xx xx:xx:xx"}}  
调用传递--->"Parameters":{"test":{"test":"xxx"}}  
:::

```xml
<Parameters>
    <test>
        <test></test>
        <test2 default="${date}"></test2>
    </test>
</Parameters>
```

::: tip 系统参数

| 参数        | 说明                    |
| ----------- | ----------------------- |
| guid        | 32 位随机字符串         |
| date        | yyyy-MM-dd HH:mm:ss     |
| simpledate  | yyyy-MM-dd              |
| millisecond | 时间戳                  |
| fulldate    | yyyy-MM-dd HH:mm:ss:SSS |
| xxx         | 项目配置文件中的配置项  |

::: tip 提示
系统参数请使用 ${},如 ${guid}, ${date}, ${simpledate} ...
:::

## Attributes

| 参数       | 说明                           | 是否必填 | 默认值 |
| ---------- | ------------------------------ | -------- | ------ |
| islist     | 是否为数组                     | 否       | false  |
| default    | 默认值                         | 否       | ——     |
| required   | 是否必填                       | 否       | true   |
| expression | 正则表达式                     | 否       | ——     |
| desc       | 当正则表达式匹配是失败时的描述 | 否       | ——     |
