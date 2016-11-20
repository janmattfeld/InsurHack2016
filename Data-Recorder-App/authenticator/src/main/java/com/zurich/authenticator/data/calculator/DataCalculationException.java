package com.zurich.authenticator.data.calculator;

public class DataCalculationException extends Exception {

    public DataCalculationException() {
    }

    public DataCalculationException(String message) {
        super(message);
    }

    public DataCalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataCalculationException(Throwable cause) {
        super(cause);
    }

}
