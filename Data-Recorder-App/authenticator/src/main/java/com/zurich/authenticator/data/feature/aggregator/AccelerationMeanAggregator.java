package com.zurich.authenticator.data.feature.aggregator;

import android.hardware.Sensor;

import com.zurich.authenticator.data.feature.calculator.MeanFeatureCalculator;
import com.zurich.authenticator.data.feature.manager.FeatureManager;

public class AccelerationMeanAggregator extends FeatureAggregator {

    public AccelerationMeanAggregator(long aggregationInterval) {
        super(FeatureManager.TYPE_MEAN_ACCELEROMETER, aggregationInterval);
        dataCalculator = new MeanFeatureCalculator(Sensor.TYPE_ACCELEROMETER);
    }

}
