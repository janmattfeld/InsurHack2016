package com.zurich.authenticator.data.feature.calculator;

import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.SensorEventUnavailableException;
import com.zurich.authenticator.data.sensor.manager.SensorEventManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MeanAbsoluteDifferenceFeatureCalculator extends FeatureCalculator {

    private long timeSpan = TimeUnit.SECONDS.toMillis(1);
    private int sensorType;

    public MeanAbsoluteDifferenceFeatureCalculator(int sensorType) {
        this.sensorType = sensorType;
    }

    @Override
    public boolean canCalculate() {
        return true;
    }

    @Override
    public FeatureData calculate() throws DataCalculationException {
        FeatureData averageFeature = new FeatureData(FeatureManager.TYPE_MEAN_ABSOLUTE_DIFFERENCE);
        try {
            List<SensorEventData> sensorEventDataList = getSensorEventDataFromLast(timeSpan, sensorType);
            float[][] values = SensorEventManager.getValuesFromSensorEventData(sensorEventDataList);
            float[] averageValues = calculateMeanAbsoluteDifference(values);
            averageFeature.setValues(averageValues);
            return averageFeature;
        } catch (SensorEventUnavailableException e) {
            throw new DataCalculationException(e);
        }
    }

    public static float[] calculateMeanAbsoluteDifference(float[][] values) {
        if (values.length < 1) {
            return new float[0];
        }

        float[] averageValues = MeanFeatureCalculator.calculateMean(values);

        // sum up the distances to the average value
        float[] differenceValues = new float[averageValues.length];
        for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
            for (int dimension = 0; dimension < averageValues.length; dimension++) {
                differenceValues[dimension] += (Math.abs(values[valueIndex][dimension]) - averageValues[dimension]);
            }
        }

        // get the average distance for each dimension
        for (int dimension = 0; dimension < differenceValues.length; dimension++) {
            differenceValues[dimension] /= values.length;
        }

        return averageValues;
    }

}
