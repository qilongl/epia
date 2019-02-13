package com.easipass.epia.exception;

import com.easipass.epia.util.ExceptionUtil;
import com.easipass.epia.util.ResponseResult;
import com.easipass.epia.util.StringHelper;

/**
 * Created by lql on 2017/11/6.
 */
public class UserException extends Exception {
    private String errorId;
    private String message;
    private String id;

    public UserException(String ex) {
        super(ex);
    }

    public UserException(String id, String errorId, String prefixMsg, Exception ex) {
        this.message = prefixMsg + ExceptionUtil.getErrorInfoFromException(ex);
        this.errorId = StringHelper.isNotNull(errorId) ? errorId : ResponseResult.RESULT_STATUS_CODE_ERROR;
        this.id = id;
    }

    public String getErrorId() {
        return errorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
