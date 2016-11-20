package com.zurich.authenticator.data.feature.calculator;

import com.zurich.authenticator.data.calculator.DataCalculator;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.SensorEventUnavailableException;
import com.zurich.authenticator.data.sensor.manager.SensorEventManager;

import java.util.List;

public abstract class FeatureCalculator extends DataCalculator<FeatureData> {

    public static List<SensorEventData> getSensorEventDataFromLast(long milliseconds, int sensoryType) throws SensorEventUnavailableException {
        return SensorEventManager.getSensorEventDataSince(System.currentTimeMillis() - milliseconds, sensoryType);
    }

    public static float[] getValuesFromSensorEventData(float[][] data, int dimension) {
        float[] dataDimension = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            dataDimension[i] = data[i][dimension];
        }
        return dataDimension;
    }
}
