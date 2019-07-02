package com.easipass.epia.exception;

import com.easipass.epia.util.Constants;
import com.easipass.epia.util.ExceptionUtil;
import com.easipass.epia.util.StringHelper;
import lombok.Data;

/**
 * Created by lql on 2017/11/6.
 */
@Data
public class UserException extends Exception {
    private String errorId;
    private String message;
    private String id;

    public UserException(String ex) {
        super(ex);
    }

    public UserException(String id, String errorId, String prefixMsg, Exception ex) {
        this.message = prefixMsg + ExceptionUtil.getErrorInfoFromException(ex);
        this.errorId = StringHelper.isNotNull(errorId) ? errorId : Constants.RESULT_STATUS_CODE_ERROR;
        this.id = id;
    }

}
