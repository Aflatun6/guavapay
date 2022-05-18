package com.parcel.app.exception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String s) {
        super(s);
    }
}
