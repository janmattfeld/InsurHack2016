package com.zurich.authenticator.data.feature.calculator;

import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.SensorEventUnavailableException;
import com.zurich.authenticator.data.sensor.manager.SensorEventManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class VarianceFeatureCalculator extends FeatureCalculator {

    private long timeSpan = TimeUnit.SECONDS.toMillis(2);
    private int sensorType;

    public VarianceFeatureCalculator(int sensorType) {
        this.sensorType = sensorType;
    }

    @Override
    public boolean canCalculate() {
        return true;
    }

    @Override
    public FeatureData calculate() throws DataCalculationException {
        FeatureData varianceFeature = new FeatureData(FeatureManager.TYPE_VARIANCE);
        try {
            List<SensorEventData> sensorEventDataList = getSensorEventDataFromLast(timeSpan, sensorType);
            float[][] values = SensorEventManager.getValuesFromSensorEventData(sensorEventDataList);
            float[] variance = calculateVariance(values);
            varianceFeature.setValues(variance);
            return varianceFeature;
        } catch (SensorEventUnavailableException e) {
            throw new DataCalculationException(e);
        }
    }

    public static float[] calculateVariance(float[][] values) {
        if (values.length < 1) {
            return new float[0];
        }

        float[] average = MeanFeatureCalculator.calculateMean(values);

        // calculate squared distance to average in each dimension
        float[] squaredDistance = new float[average.length];
        for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
            for (int dimension = 0; dimension < squaredDistance.length; dimension++) {
                float distance = values[valueIndex][dimension] - average[dimension];
                squaredDistance[dimension] += distance * distance;
            }
        }

        // normalize over number of entries to retrieve standard deviation
        float[] variance = new float[squaredDistance.length];
        for (int dimension = 0; dimension < variance.length; dimension++) {
            //variance[dimension] = (float) Math.sqrt(squaredDistance[dimension] / values.length);
            variance[dimension] = squaredDistance[dimension] / values.length;
        }
        return variance;
    }

}
