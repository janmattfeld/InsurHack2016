package com.zurich.authenticator.data.feature.calculator;

import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.SensorEventUnavailableException;
import com.zurich.authenticator.data.sensor.manager.SensorEventManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MedianFeatureCalculator extends FeatureCalculator {

    private long timeSpan = TimeUnit.SECONDS.toMillis(1);
    private int sensorType;

    public MedianFeatureCalculator(int sensorType) {
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
            float[] averageValues = calculateMedian(values);
            averageFeature.setValues(averageValues);
            return averageFeature;
        } catch (SensorEventUnavailableException e) {
            throw new DataCalculationException(e);
        }
    }

    public static float[] calculateMedian(float[][] values) {
        if (values.length < 1) {
            return new float[0];
        }

        int dimensions = values[0].length;
        float[] medianValues = new float[dimensions];
        for (int dimension = 0; dimension < dimensions; dimension++) {
            float[] dimensionValues = getValuesFromSensorEventData(values, dimension);
            medianValues[dimension] = calculateMedian(dimensionValues);
        }
        return medianValues;
    }

    public static float calculateMedian(float[] values) {
        // sort values
        Arrays.sort(values);

        // get centered value
        float medianValue;
        int middleIndex = values.length / 2;
        if (values.length % 2 == 1) {
            medianValue = values[middleIndex];
        } else {
            medianValue = (values[middleIndex - 1] + values[middleIndex]) / 2f;
        }
        return medianValue;
    }

}
