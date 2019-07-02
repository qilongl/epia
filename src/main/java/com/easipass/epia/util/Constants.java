package com.easipass.epia.util;

/**
 * @author mlzhang
 * @version Jun 29, 2011 5:07:37 PM
 * description
 */
public class Constants {
    public static final String FLAG_T = "T";
    public static final String FLAG_F = "F";
    public static final String RESULT_STATUS_CODE_SUCCESS = "200";
    public static final String RESULT_STATUS_CODE_ERROR = "500";

    /**
     * 手机号码正则表达式
     */
    public static final String MOBILE_VERIFY_REGEX = "^1[\\d]{10}$";

    public static class CLIENT_ERROR_000 {
        public static final String ERROR_CODE001 = "001";
        public static final String ERROR_INFO001 = "未知错误，请稍后再试";
        public static final String ERROR_CODE002 = "002";
        public static final String ERROR_INFO002 = "该单证已经被修改无法保存！";
        public static final String ERROR_CODE003 = "003";
        public static final String ERROR_INFO003 = "数据库操作发生错误，自动回滚。";
        public static final String ERROR_CODE004 = "004";
        public static final String ERROR_INFO004 = "更新失败，请稍候再试";
        public static final String ERROR_CODE005 = "005";
        public static final String ERROR_INFO005 = "存储过程调用异常";
        public static final String ERROR_CODE006 = "006";
        public static final String ERROR_INFO006 = "业务异常";
    }

    public static class CLIENT_ERROR_100 {
        public static final String ERROR_CODE101 = "101";
        public static final String ERROR_INFO101 = "请求参数有误或缺失";
        public static final String ERROR_CODE102 = "102";
        public static final String ERROR_INFO102 = "未找到对应单证信息";
        public static final String ERROR_CODE103 = "103";
        public static final String ERROR_INFO103 = "存在相同单号无法新增";
        public static final String ERROR_CODE104 = "104";
        public static final String ERROR_INFO104 = "非法调用";
    }

    public static class USER_ERROR_200 {
        public static final String ERROR_CODE201 = "201";
        public static final String ERROR_INFO201 = "此用户未注册，请先注册！";
        public static final String ERROR_CODE202 = "202";
        public static final String ERROR_INFO202 = "您已注册！";
        public static final String ERROR_CODE203 = "203";
        public static final String ERROR_INFO203 = "统一身份认证缺少数据！";
        public static final String ERROR_CODE204 = "204";
        public static final String ERROR_INFO204 = "同一用户不能同时注册！";
        public static final String ERROR_CODE205 = "205";
        public static final String ERROR_INFO205 = "缺少筛选参数，请联系软件开发商！";
        public static final String ERROR_CODE206 = "206";
        public static final String ERROR_INFO206 = "您不是企业管理员，无法操作！";
        public static final String ERROR_CODE207 = "207";
        public static final String ERROR_INFO207 = "用户信息获取异常！";
        public static final String ERROR_CODE208 = "208";
        public static final String ERROR_INFO208 = "OAuth异常！";
        public static final String ERROR_CODE209 = "209";
        public static final String ERROR_INFO209 = "登陆失败！";
        public static final String ERROR_CODE210 = "210";
        public static final String ERROR_INFO210 = "认证凭据与用户名不匹配，请联系软件开发商！";
        public static final String ERROR_CODE211 = "211";
        public static final String ERROR_INFO211 = "重新预约失败!";
    }

    public static class CLIENT_ERROR_900 {
        public static final String ERROR_CODE901 = "901";
        public static final String ERROR_INFO901 = "验证参数有误或缺失";
        public static final String ERROR_CODE902 = "902";
        public static final String ERROR_INFO902 = "验证信息失败";
        public static final String ERROR_CODE903 = "903";
        public static final String ERROR_INFO903 = "服务器异常";
        public static final String ERROR_CODE904 = "904";
        public static final String ERROR_INFO904 = "用户授权失效，请重新登录";
        public static final String ERROR_CODE905 = "905";
        public static final String ERROR_INFO905 = "认证凭据为空";
        public static final String ERROR_CODE906 = "906";
        public static final String ERROR_INFO906 = "无权访问";
        public static final String ERROR_CODE907 = "907";
        public static final String ERROR_INFO907 = "网络异常，请稍后再试！";
        public static final String ERROR_CODE908 = "908";
        public static final String ERROR_INFO908 = "OAuth异常！";
    }

    public static String showInfo(String code) {
        String info = null;
        switch (code) {
            case CLIENT_ERROR_000.ERROR_CODE001:
                info = CLIENT_ERROR_000.ERROR_INFO001;
                break;
            case CLIENT_ERROR_000.ERROR_CODE002:
                info = CLIENT_ERROR_000.ERROR_INFO002;
                break;
            case CLIENT_ERROR_000.ERROR_CODE003:
                info = CLIENT_ERROR_000.ERROR_INFO003;
                break;
            case CLIENT_ERROR_000.ERROR_CODE004:
                info = CLIENT_ERROR_000.ERROR_INFO004;
                break;
            case CLIENT_ERROR_000.ERROR_CODE005:
                info = CLIENT_ERROR_000.ERROR_INFO005;
                break;
            case CLIENT_ERROR_000.ERROR_CODE006:
                info = CLIENT_ERROR_000.ERROR_INFO006;
                break;

            case CLIENT_ERROR_100.ERROR_CODE101:
                info = CLIENT_ERROR_100.ERROR_INFO101;
                break;
            case CLIENT_ERROR_100.ERROR_CODE102:
                info = CLIENT_ERROR_100.ERROR_INFO101;
                break;
            case CLIENT_ERROR_100.ERROR_CODE103:
                info = CLIENT_ERROR_100.ERROR_INFO103;
                break;
            case CLIENT_ERROR_100.ERROR_CODE104:
                info = CLIENT_ERROR_100.ERROR_INFO104;
                break;

            case USER_ERROR_200.ERROR_CODE201:
                info = USER_ERROR_200.ERROR_INFO201;
                break;
            case USER_ERROR_200.ERROR_CODE202:
                info = USER_ERROR_200.ERROR_INFO202;
                break;
            case USER_ERROR_200.ERROR_CODE203:
                info = USER_ERROR_200.ERROR_INFO203;
                break;
            case USER_ERROR_200.ERROR_CODE204:
                info = USER_ERROR_200.ERROR_INFO204;
                break;
            case USER_ERROR_200.ERROR_CODE205:
                info = USER_ERROR_200.ERROR_INFO205;
                break;
            case USER_ERROR_200.ERROR_CODE206:
                info = USER_ERROR_200.ERROR_INFO206;
                break;
            case USER_ERROR_200.ERROR_CODE207:
                info = USER_ERROR_200.ERROR_INFO207;
                break;
            case USER_ERROR_200.ERROR_CODE208:
                info = USER_ERROR_200.ERROR_INFO208;
                break;
            case USER_ERROR_200.ERROR_CODE209:
                info = USER_ERROR_200.ERROR_INFO209;
                break;
            case USER_ERROR_200.ERROR_CODE210:
                info = USER_ERROR_200.ERROR_INFO210;
                break;
            case USER_ERROR_200.ERROR_CODE211:
                info = USER_ERROR_200.ERROR_INFO211;
                break;

            case CLIENT_ERROR_900.ERROR_CODE901:
                info = CLIENT_ERROR_900.ERROR_INFO901;
                break;
            case CLIENT_ERROR_900.ERROR_CODE902:
                info = CLIENT_ERROR_900.ERROR_INFO902;
                break;
            case CLIENT_ERROR_900.ERROR_CODE903:
                info = CLIENT_ERROR_900.ERROR_INFO903;
                break;
            case CLIENT_ERROR_900.ERROR_CODE904:
                info = CLIENT_ERROR_900.ERROR_INFO904;
                break;
            case CLIENT_ERROR_900.ERROR_CODE905:
                info = CLIENT_ERROR_900.ERROR_INFO905;
                break;
            case CLIENT_ERROR_900.ERROR_CODE906:
                info = CLIENT_ERROR_900.ERROR_INFO906;
                break;
            case CLIENT_ERROR_900.ERROR_CODE907:
                info = CLIENT_ERROR_900.ERROR_INFO907;
                break;
            case CLIENT_ERROR_900.ERROR_CODE908:
                info = CLIENT_ERROR_900.ERROR_INFO908;
                break;
            default:
                info = "未知错误,请稍后再试";
                break;
        }
        return info;
    }


}
