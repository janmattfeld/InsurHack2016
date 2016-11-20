package com.zurich.authenticator.data.feature.aggregator;

import android.hardware.Sensor;

import com.zurich.authenticator.data.feature.calculator.StandardDeviationFeatureCalculator;
import com.zurich.authenticator.data.feature.manager.FeatureManager;

public class AccelerationDeviationAggregator extends FeatureAggregator {

    public AccelerationDeviationAggregator(long aggregationInterval) {
        super(FeatureManager.TYPE_STANDARD_DEVIATION_ACCELEROMETER, aggregationInterval);
        dataCalculator = new StandardDeviationFeatureCalculator(Sensor.TYPE_ACCELEROMETER);
    }

}
