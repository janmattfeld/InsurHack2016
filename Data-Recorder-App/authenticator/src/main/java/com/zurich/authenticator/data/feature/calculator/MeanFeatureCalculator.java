package com.zurich.authenticator.data.feature.calculator;

import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.SensorEventUnavailableException;
import com.zurich.authenticator.data.sensor.manager.SensorEventManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MeanFeatureCalculator extends FeatureCalculator {

    private long timeSpan = TimeUnit.SECONDS.toMillis(1);
    private int sensorType;

    public MeanFeatureCalculator(int sensorType) {
        this.sensorType = sensorType;
    }

    @Override
    public boolean canCalculate() {
        return true;
    }

    @Override
    public FeatureData calculate() throws DataCalculationException {
        FeatureData averageFeature = new FeatureData(FeatureManager.TYPE_MEAN);
        try {
            List<SensorEventData> sensorEventDataList = getSensorEventDataFromLast(timeSpan, sensorType);
            float[][] values = SensorEventManager.getValuesFromSensorEventData(sensorEventDataList);
            float[] averageValues = calculateMean(values);

            averageFeature.setValues(averageValues);
            return averageFeature;
        } catch (SensorEventUnavailableException e) {
            throw new DataCalculationException(e);
        }
    }

    public static float[] calculateMean(float[][] values) {
        if (values.length < 1) {
            return new float[0];
        }

        // sum up all values
        float[] summedValues = new float[values[0].length];
        for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
            for (int dimension = 0; dimension < summedValues.length; dimension++) {
                summedValues[dimension] += values[valueIndex][dimension];
            }
        }

        // calculate average from summed values
        float[] averageValues = new float[summedValues.length];
        for (int dimension = 0; dimension < averageValues.length; dimension++) {
            averageValues[dimension] = summedValues[dimension] / values.length;
        }

        return averageValues;
    }

    public static float calculateMean(float[] values) {
        float valueSum = 0;
        for (int i = 0; i < values.length; i++) {
            valueSum += values[i];
        }
        return valueSum / values.length;
    }
}
