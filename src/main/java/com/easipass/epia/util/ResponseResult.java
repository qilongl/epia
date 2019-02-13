package com.easipass.epia.util;

import java.io.Serializable;

/**
 * Created by lql on 2017/5/25.
 */
public class ResponseResult implements Serializable {
    public static final String RESULT_STATUS_CODE_SUCCESS = "200";//成功
    public static final String RESULT_STATUS_CODE_ERROR = "500";//异常
    public static final String RESULT_STATUS_CODE_DEFAULT = "9";//默认



    public static final String RESULT_STATUS_CODE_ = "9";//默认




    /**
     * 执行成功标识
     */
    private String statusCode = RESULT_STATUS_CODE_DEFAULT;

    /**
     * 执行提示信息
     */
    private String msg = "";

    /**
     * 执行返回结果对象
     */
    private Object result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "statusCode=" + statusCode +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}
