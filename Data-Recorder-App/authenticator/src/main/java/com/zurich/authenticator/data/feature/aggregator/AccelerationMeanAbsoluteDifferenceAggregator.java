package com.zurich.authenticator.data.feature.aggregator;

import android.hardware.Sensor;

import com.zurich.authenticator.data.feature.calculator.MeanAbsoluteDifferenceFeatureCalculator;
import com.zurich.authenticator.data.feature.manager.FeatureManager;

public class AccelerationMeanAbsoluteDifferenceAggregator extends FeatureAggregator {

    public AccelerationMeanAbsoluteDifferenceAggregator(long aggregationInterval) {
        super(FeatureManager.TYPE_MEAN_ABSOLUTE_DIFFERENCE_ACCELEROMETER, aggregationInterval);
        dataCalculator = new MeanAbsoluteDifferenceFeatureCalculator(Sensor.TYPE_ACCELEROMETER);
    }

}
