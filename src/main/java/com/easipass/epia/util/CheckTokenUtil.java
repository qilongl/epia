package com.easipass.epia.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

/**
 * Created by lql on 2018/12/26 13:17
 **/
public class CheckTokenUtil {
    public ResponseResult checkToken(String dataString, JSONObject dataObject, JSONObject tokenObject, JSONObject dbInfoObject) {
        ResponseResult responseResult = new ResponseResult();
        try {
            if (dbInfoObject == null || dbInfoObject.equals("")) {
                responseResult.setStatusCode(ResponseResult.RESULT_STATUS_CODE_ERROR);
                responseResult.setMsg("无账号、失效或账户已过期！");
                return responseResult;
            }
            String datetime = tokenObject.getString("datetime");
            long a = new Date().getTime();
            long b = DateFormater.yyyyMMddHHmmss2Date(datetime).getTime();
            int tolerant = Math.abs((int) ((a - b) / 1000));
            if (tolerant > Integer.parseInt(dbInfoObject.get("TOLERANT_SEC").toString())) {
                responseResult.setStatusCode(ResponseResult.RESULT_STATUS_CODE_ERROR);
                responseResult.setMsg("时间差超过时间间隔容许参数！");
                return responseResult;
            }
            //使用本地秘钥对数据加密
            String tokenKey = dbInfoObject.getString("TOKEN_KEY");
            String ciphertextA = tokenObject.getString("ciphertext");
            String ciphertextB = "";
            if (dataString.equals("") || dataString == null) {
                String functionsString = JSONObject.toJSONString(dataObject, SerializerFeature.MapSortField);
                ciphertextB = DigestUtils.md5Hex(functionsString + tokenKey + datetime);
                responseResult.setResult(dataObject);
            } else {
                ciphertextB = DigestUtils.md5Hex(dataString + tokenKey + datetime);
                responseResult.setResult(dataString);
            }
            //获取控制项，用于判段是否需要执行密文校验
            int check_ciphertext = Integer.parseInt(dbInfoObject.get("CHECK_CIPHERTEXT").toString());
            if (!ciphertextA.equals(ciphertextB) && check_ciphertext != 0) {
                responseResult.setStatusCode(ResponseResult.RESULT_STATUS_CODE_ERROR);
                responseResult.setMsg("密文校验未通过！");
                return responseResult;
            }
             responseResult.setStatusCode(ResponseResult.RESULT_STATUS_CODE_SUCCESS);
            responseResult.setMsg("校验通过！");

        } catch (Exception ex) {
            ex.printStackTrace();
            responseResult.setMsg(ExceptionUtil.getErrorInfoFromException(ex));
            responseResult.setStatusCode(ResponseResult.RESULT_STATUS_CODE_ERROR);
        }
        return responseResult;
    }


}

