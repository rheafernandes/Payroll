package com.fileee.exceptions;

import com.fileee.enums.ResponseCode;

import java.text.MessageFormat;

public class MiddlewareException extends RuntimeException {


    private static final long serialVersionUID = -3866941321932941766L;
    private String errCode;

    /**
     * Initializes the middleware exception with a given error code and message.
     * @param errCode Error code from the service
     * @param message Error message (static). For parameter substitution use the other constructor
     */
    public MiddlewareException(String errCode, String message) {
        super(message);
        this.errCode = errCode;
    }


    /**
     * Initializes the middleware exception with a template message
     * @param errCode Error code from the service
     * @param message Error message
     * @param root Root cause exception
     */
    public MiddlewareException(String errCode, String message, Throwable root) {
        super(message, root);
        this.errCode = errCode;
    }

    public String getErrCode() {
        return errCode;
    }

    public ResponseCode getResponseCode() {
        return ResponseCode.SERVER_ERROR;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(errCode).append(": ");
        builder.append(super.getMessage());
        return builder.toString();
    }
}
