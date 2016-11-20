package com.zurich.authenticator.data.sensor;

public class SensorUnavailableException extends Exception {

    public SensorUnavailableException() {
    }

    public SensorUnavailableException(String message) {
        super(message);
    }

    public SensorUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public SensorUnavailableException(Throwable cause) {
        super(cause);
    }

}
