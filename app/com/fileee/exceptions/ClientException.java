package com.fileee.exceptions;

public class ClientException extends MiddlewareException {
    public ClientException(String errCode, String message) {
        super(errCode, message);
    }

    public ClientException(String errCode, String message, Throwable root) {
        super(errCode, message, root);
    }
}
