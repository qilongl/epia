---
pageClass: getting-started
---

# 介绍

[xml-analysis-component](http://www.liuqilong.com/xa) 是一款持久层框架，但它不仅仅只是持久层框架。框架以 XML 文件作为接口模板，像搭建积木一样便可灵活配置出自己想要的接口。框架支持 `基础类SQL处理` 、 `存储过程` 、 `动态SQL` 、 `接口参数定义` 、 `内部逻辑实现` 、 `条件判断` 、 `自定义异常` 、 `结果集控制` 等。同时框架内部也封装了一些通用的功能标签，如 `上传` 、 `下载` 、 `导出excel` 、 `rest调用` 等。值得一提的是当项目业务拓展，需求变更时我们可以很方便的去应对这些问题，框架支持在线新增、修改接口，并能立马生效。

<!-- :::tip 提示
本项目目前仍处于开发迭代阶段，如有相关问题，欢迎联系<178072680@qq.com>
::: -->

## XML 基础模板
下面是接口最基础的模板节点，参数定义在 `<Parameters>` 标签中、命令使用在`<Actions>`标签中

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Function id="demo" datasource="dataSource" name="测试" desc="测试">
    <Parameters>
    </Parameters>
    <Actions>
    </Actions>
</Function>
```

### Attributes

| 参数         | 说明               | 是否必填 |
| ------------ | ------------------ | -------- |
| Function     | 根节点             | 是       |
| --id         | 接口文件名、接口名 | 是       |
| --datasource | 接口数据源名称     | 是       |
| --name       | 接口名称           | 否       |
| --desc       | 接口描述           | 否       |
| Parameters   | 入参集合节点       | 是       |
| Actions      | 命令集合节点       | 是       |

<br/>

## Parameters 参数
只需要在参数模板中自定义所需参数，即可在命令模板中的命令标签中引用。

```xml
<Parameters>
    <user>
        <userName/>
        <age/>
    </user>
</Parameters>
```
更多参数用法及解释请至[参数](./essentials/parameters.md)

## Actions 命令

```xml
<Actions>
    <insert id="insert" isreturn="true" params="user">
        <![CDATA[
          insert into user(name,age) values(#{userName},#{age})
        ]]>
    </insert>
    <delete id="delete" isreturn="true" params="user">
        <![CDATA[
          delete from user where userName =#{userName}
        ]]>
    </delete>
    <update id="update" isreturn="true" params="user">
        <![CDATA[
          update user set age =#{age} where userName =#{userName}
        ]]>
    </update>
    <select id="select" isreturn="true" params="user">
        <![CDATA[
          select * from user
        ]]>
    </select>
</Actions>
```

在命令模板中使用命令标签来完成相关业务逻辑实现，更多命令请至[命令](./essentials/actions.md)

### Attributes

| 参数     | 说明           | 是否必填 | 默认值  |
| -------- | -------------- | -------- | ------- |
| id       | 命令唯一 ID    | 是       | ——      |
| isreturn | 是否返还结果集 | 否       | "false" |
| params   | 参数对象       | 否       | ——      |

更多命令用法及解释请至[命令](./essentials/actions.md#Attributes)

[xml-analysis-component](http://www.liuqilong.com/xa) 还在持续迭代中，逐步沉淀和总结出更多功能和相应的实现代码，如果您有意向，本项目也十分期待你的参与和反馈<178072680@qq.com>。
