package com.zurich.authenticator.data.classification;

public class ClassificationException extends Exception {

    public ClassificationException() {
    }

    public ClassificationException(String message) {
        super(message);
    }

    public ClassificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassificationException(Throwable cause) {
        super(cause);
    }

}
