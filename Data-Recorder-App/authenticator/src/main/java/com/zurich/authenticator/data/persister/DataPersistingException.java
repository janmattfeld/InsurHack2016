package com.zurich.authenticator.data.persister;

public class DataPersistingException extends Exception {

    public DataPersistingException() {
    }

    public DataPersistingException(String message) {
        super(message);
    }

    public DataPersistingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataPersistingException(Throwable cause) {
        super(cause);
    }

}
