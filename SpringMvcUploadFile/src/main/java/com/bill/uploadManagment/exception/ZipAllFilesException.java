package com.bill.uploadManagment.exception;

public class ZipAllFilesException extends  RuntimeException {

    public ZipAllFilesException(String msg) {
        super(msg);
    }

    public ZipAllFilesException(String message, Throwable cause) {
        super(message, cause);
    }
}

