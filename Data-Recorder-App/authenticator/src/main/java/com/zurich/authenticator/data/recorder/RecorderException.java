package com.zurich.authenticator.data.recorder;

public class RecorderException extends Exception {

    public RecorderException() {
    }

    public RecorderException(String message) {
        super(message);
    }

    public RecorderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecorderException(Throwable cause) {
        super(cause);
    }

}
