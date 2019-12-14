package com.bill.uploadManagment.exception;

public class StorageException extends RuntimeException {

    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}