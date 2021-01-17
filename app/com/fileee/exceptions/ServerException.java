package com.fileee.exceptions;

public class ServerException extends MiddlewareException {

    public ServerException(String errCode, String message) {
        super(errCode, message);
    }

    public ServerException(String errCode, String message, Throwable root) {
        super(errCode, message, root);
    }
}
