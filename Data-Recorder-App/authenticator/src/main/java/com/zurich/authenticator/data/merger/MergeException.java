package com.zurich.authenticator.data.merger;

public class MergeException extends Exception {

    public MergeException() {
    }

    public MergeException(String message) {
        super(message);
    }

    public MergeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MergeException(Throwable cause) {
        super(cause);
    }

}
