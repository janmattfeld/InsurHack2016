package com.zurich.authenticator.data.sensor;

public class SensorEventUnavailableException extends Exception {

    public SensorEventUnavailableException() {
    }

    public SensorEventUnavailableException(String message) {
        super(message);
    }

    public SensorEventUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public SensorEventUnavailableException(Throwable cause) {
        super(cause);
    }

}
