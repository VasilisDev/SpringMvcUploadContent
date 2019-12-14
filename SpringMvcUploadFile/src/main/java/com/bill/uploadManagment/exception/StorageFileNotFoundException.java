package com.bill.uploadManagment.exception;

public class StorageFileNotFoundException extends  StorageException {

    public StorageFileNotFoundException(String msg) {
        super(msg);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
