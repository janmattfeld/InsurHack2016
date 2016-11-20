package com.zurich.authenticator.data.aggregation;

public class DataAggregationException extends Exception {

    public DataAggregationException() {
    }

    public DataAggregationException(String message) {
        super(message);
    }

    public DataAggregationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAggregationException(Throwable cause) {
        super(cause);
    }

}
