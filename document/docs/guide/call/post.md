# post 请求

## url

> `url:` http://localhost:8888/epia/componentService

## 样例

### xml 配置文件（接口）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Epia id="test2" datasource="dataSource" name="测试开发环境新增xml配置文件自动加载" desc="测试2">
    <Parameters>
        <test>
            <PAGENUM/>
            <PAGESIZE/>
            <PROP default=""/>
            <SORT default="D"/>
        </test>
    </Parameters>
    <Actions>
        <select id="abc" isreturn="true" errorid="1" params="test" ispaging="true">
            <![CDATA[
            select OBJID,FUN_NAME,FUN_URL,IS_CONTRL_FUN from sys_function
            <if test="#{PROP}==''">
                ORDER BY CRT_DT
            </if>
            <if test="#{PROP}=='OBJID'">
                ORDER BY OBJID
            </if>
            <if test="#{SORT}=='D'">
                DESC
            </if>
            <if test="#{SORT}=='A'">
                ASC
            </if>
            ]]>
        </select>
        <select id="def" isreturn="true" errorid="1">
            <![CDATA[
            select sys_guid() as uuid from dual
            ]]>
        </select>
    </Actions>
</Epia>
```

### 请求参数

```json
{
  "data": {
    "Functions": [
      {
        "FunctionName": "test2",
        "ModuleName": "demo-select",
        "Parameters": {
          "test": {
            "PAGENUM": "1",
            "PAGESIZE": "2",
            "PROP": "OBJID",
            "SORT": "D"
          }
        }
      }
    ]
  },
  "token": {
    "account": "easipass",
    "ciphertext": "4dc205d957661b9b687fbab207fb7f7f",
    "datetime": "2019-05-20 16:17:32"
  }
}
```

| 字段             | 字段描述                     | 备注               |
| ---------------- | ---------------------------- | ------------------ |
| data             | 接口数据                     |
| --Functions      | 接口数据                     |
| ----FunctionName | 接口名（xml 配置文件 id）    |
| ----ModuleName   | 接口目录                     |
| ----Parameters   | 接口入参                     | 接口中的自定义入参 | 
| token            | 加密参数                     |
| --account        | 密钥                         |
| --datetime       | 接口请求时间                 |
| --ciphertext     | MD5 加密串（参数+密钥+时间） |

### 返还结果集

```json
{
  "msg": "业务执行成功！",
  "result": {
    "demo-select-test2": {
      "abc": [
        {
          "FUN_NAME": "压力测试样例",
          "FUN_URL": "base-test-test17",
          "IS_CONTRL_FUN": 0,
          "OBJID": "ED163C834760465B86FBF4FA32084C40",
          "RN": 1
        },
        {
          "FUN_NAME": "Download样例",
          "FUN_URL": "demo-download-demo3",
          "IS_CONTRL_FUN": 0,
          "OBJID": "DF95E1C2C59A49308CEFCC65FD4BA31E",
          "RN": 2
        }
      ],
      "def": [
        {
          "UUID": "PaP7d5edTH2KvBgzQfsLeA=="
        }
      ]
    }
  },
  "statusCode": "200"
}
```

| 字段                | 字段描述             | 备注 |
| ------------------- | -------------------- | ---- |
| statusCode          | 状态码               |
| msg                 | 信息                 |
| result              | 结果集               |
| --demo-select-test2 | 接口 url             |
| ----abc             | id=“abc”的命令结果集 |
| ----def             | id=“def”的命令结果集 |
