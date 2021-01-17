package com.fileee.exceptions;

public class ResourceNotFoundException extends MiddlewareException {

    public ResourceNotFoundException(String errCode, String message) {
        super(errCode, message);
    }

    public ResourceNotFoundException(String errCode, String message, Throwable root) {
        super(errCode, message, root);
    }
}
