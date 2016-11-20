package com.zurich.authenticator.data.feature.calculator;

import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.SensorEventUnavailableException;
import com.zurich.authenticator.data.sensor.manager.SensorEventManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class StandardDeviationFeatureCalculator extends FeatureCalculator {

    private long timeSpan = TimeUnit.SECONDS.toMillis(2);
    private int sensorType;

    public StandardDeviationFeatureCalculator(int sensorType) {
        this.sensorType = sensorType;
    }

    @Override
    public boolean canCalculate() {
        return true;
    }

    @Override
    public FeatureData calculate() throws DataCalculationException {
        FeatureData varianceFeature = new FeatureData(FeatureManager.TYPE_STANDARD_DEVIATION);
        try {
            List<SensorEventData> sensorEventDataList = getSensorEventDataFromLast(timeSpan, sensorType);
            float[][] values = SensorEventManager.getValuesFromSensorEventData(sensorEventDataList);
            float[] variance = calculateStandardDeviation(values);
            varianceFeature.setValues(variance);
            return varianceFeature;
        } catch (SensorEventUnavailableException e) {
            throw new DataCalculationException(e);
        }
    }

    public static float[] calculateStandardDeviation(float[][] values) {
        if (values.length < 1) {
            return new float[0];
        }
        float[] variance = VarianceFeatureCalculator.calculateVariance(values);
        float[] standardDeviation = new float[variance.length];
        for (int dimension = 0; dimension < variance.length; dimension++) {
            standardDeviation[dimension] = (float) Math.sqrt(variance[dimension]);
        }
        return standardDeviation;
    }

}
