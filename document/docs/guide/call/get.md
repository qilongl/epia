# get 请求

## url

> `url:` http://localhost:8888/epia/easyComponentService

## 样例

### xml 配置文件（接口）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Epia id="test1" datasource="dataSource" name="样例1" desc="基础增删改查">
    <Parameters>
        <test>
            <test2 default=""></test2>
        </test>
    </Parameters>
    <Actions>
        <select id="Retrieve22" isreturn="true" errorid="0" msg="啊呜,有问题哦">
            <![CDATA[
            SELECT #{test.test2} as param, to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') time from  dual
            ]]>
        </select>
    </Actions>
</Epia>
```

### 请求参数

`FunctionName：接口名称`  
`自定义参数(对象名-键值)`  
`+“&&” +加密串（密钥+“_”+时间+“_”+MD5 加密串（参数+密钥+时间））`

> http://localhost:8888/epia/easyComponentService?FunctionName=demo-select-test1&test-test2=111&&easipass_2019-03-16%2016:07:234_dadc49abe8323efb9fc3ecfa749abdcd

### 返还结果集

```json
{
  "msg": "业务执行成功！",
  "result": {
    "demo-select-test1": {
      "Retrieve22": [{ "TIME": "2019-06-04 16:37:49", "PARAM": "111" }]
    }
  },
  "statusCode": "200"
}
```

| 字段                | 字段描述                    | 备注 |
| ------------------- | --------------------------- | ---- |
| statusCode          | 状态码                      |
| msg                 | 信息                        |
| result              | 结果集                      |
| --demo-select-test1 | 接口 url                    |
| ----Retrieve22      | id=“Retrieve22”的命令结果集 |
