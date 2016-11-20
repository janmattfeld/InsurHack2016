package com.zurich.authenticator.data.sensor.aggregator;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.aggregation.DataAggregatorManager;
import com.zurich.authenticator.data.generic.Data;

public final class SensorEventAggregators extends DataAggregatorManager<SensorEventAggregator> {

    private static SensorEventAggregators instance;

    public SensorEventAggregators() {
        super(Data.TYPE_SENSOR_EVENT);
    }

    public static SensorEventAggregators getInstance() {
        if (instance == null) {
            instance = new SensorEventAggregators();
        }
        return instance;
    }

    public static SensorEventAggregator getDefaultAggregator() throws DataAggregationException {
        return getInstance().getAggregator(DataAggregator.TYPE_DEFAULT);
    }

}
