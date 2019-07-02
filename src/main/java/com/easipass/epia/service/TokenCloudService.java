//package com.easipass.epia.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.easipass.epia.EpiaApplication;
//import com.easipass.epia.db.DBFactory;
//import com.easipass.epia.db.DBService;
//import com.easipass.epia.util.CheckTokenUtil;
//import com.easipass.epia.util.ResponseResult;
//import com.easipass.epia.util.StringHelper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by lql on 2017/11/03.
// * Update by lql on 2018/06/12.
// */
//@Component
//@Service
//public class TokenCloudService {
//
//    public ResponseResult checkTokenInfoByAccount(String urlParamToken, String jsonParamToken) {
//        String dataString = "";
//        JSONObject dataObject = null;
//        JSONObject tokenObject = null;
//        if (!urlParamToken.equals("")) {
//            String[] tokenParamsArray = urlParamToken.split("&&");
//            dataString = tokenParamsArray[0];
//            String tokenUrl = tokenParamsArray[1];
//            String[] tokenArray = tokenUrl.split("_");
//            tokenObject = new JSONObject();
//            tokenObject.put("account", tokenArray[0]);
//            tokenObject.put("datetime", tokenArray[1]);
//            tokenObject.put("ciphertext", tokenArray[2]);
//        } else {
//            // 解析出data
//            JSONObject jsonParamObject = JSONObject.parseObject(jsonParamToken);
//            dataObject = jsonParamObject.getJSONObject("data");
//            tokenObject = jsonParamObject.getJSONObject("token");
//        }
//
//        //请求后台存储的账户token信息
//        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
//        param.add("account", tokenObject.get("account"));
//        DBService dbService = DBFactory.createDBService(EpiaApplication.ctx);
//        List<Map<String, Object>> result = getTokenInfoByAccount(dbService, tokenObject.getString("account"));
//        JSONObject dbInfoObject = JSONObject.parseObject(JSON.toJSONString(result.get(0)));
//        CheckTokenUtil checkTokenUtil = new CheckTokenUtil();
//        ResponseResult checkTokenResult = checkTokenUtil.checkToken(dataString, dataObject, tokenObject, dbInfoObject);
//        return checkTokenResult;
//    }
//
//
//    public List<Map<String, Object>> getTokenInfoByAccount(DBService dbService, String account) {
//        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
//        try {
//            String sql = "select account, token_key, status, end_time, tolerant_sec ,check_ciphertext\n" +
//                    "  from SYS_TOKEN_CLOUD t\n" +
//                    " where t.account = ?\n" +
//                    "   and t.status = 0\n" +
//                    "   and sysdate < t.end_time\n";
//            List list = new ArrayList<>();
//            list.add(account);
//            result = dbService.select(sql, list);
//            //最后操作时间；
////        String sqlForUp = "update sys_token_cloud d set d.last_time = sysdate where d.account=? and d.status=0 and sysdate<d.end_time";
////        List list2 = new ArrayList<>();
////        list2.add(account);
////        int num_b = dbService.update(sqlForUp, list2);
//        } catch (Exception ex) {
//            throw ex;
//        }
//        return result;
//    }
//
//
//}
