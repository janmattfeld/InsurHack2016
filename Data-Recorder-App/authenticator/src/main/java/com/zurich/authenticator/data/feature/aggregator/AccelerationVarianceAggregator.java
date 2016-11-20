package com.zurich.authenticator.data.feature.aggregator;

import android.hardware.Sensor;

import com.zurich.authenticator.data.feature.calculator.VarianceFeatureCalculator;
import com.zurich.authenticator.data.feature.manager.FeatureManager;

public class AccelerationVarianceAggregator extends FeatureAggregator {

    public AccelerationVarianceAggregator(long aggregationInterval) {
        super(FeatureManager.TYPE_VARIANCE_ACCELEROMETER, aggregationInterval);
        dataCalculator = new VarianceFeatureCalculator(Sensor.TYPE_ACCELEROMETER);
    }

}
