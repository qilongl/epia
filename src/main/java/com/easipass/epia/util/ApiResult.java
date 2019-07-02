package com.easipass.epia.util;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by mlzhang on 2017/7/7.
 */
@Data
public class ApiResult implements Serializable {
    public ApiResult() {
    }

    public static ApiResult newInstance(String flag, String errorCode, String errorInfo, Object data) {
        return new ApiResult(flag, errorCode, errorInfo, data);
    }

    private String flag;
    private String errorCode;
    private String errorInfo;
    private Object data;

    public ApiResult(String flag, String errorCode, String errorInfo, Object data) {
        this.flag = flag;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
        this.data = data;
    }

    /**
     * 成功
     *
     * @param data
     */
    public ApiResult(Object data) {
        this.flag = Constants.FLAG_T;
        this.data = data;
    }

    /**
     * 失败
     *
     * @param errorCode
     * @param errorInfo
     */
    public ApiResult(String errorCode, String errorInfo) {
        this.flag = Constants.FLAG_F;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }


}
