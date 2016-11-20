package com.zurich.authenticator.data.feature.calculator;

import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.feature.FeatureData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ModeFeatureCalculator extends FeatureCalculator {

    private long timeSpan = TimeUnit.SECONDS.toMillis(1);
    private int sensorType;

    public ModeFeatureCalculator(int sensorType) {
        this.sensorType = sensorType;
    }

    @Override
    public boolean canCalculate() {
        return false;
    }

    @Override
    public FeatureData calculate() throws DataCalculationException {
        throw new DataCalculationException("Not implemented");
    }

    public static float[] calculateMode(float[][] values) {
        // TODO: implement if needed
        return new float[0];
    }

    public static float calculateMode(float[] values) {
        // get value frequencies
        HashMap<Float, Integer> frequencies = new HashMap<>();
        for (float value : values) {
            Integer currentFrequency = frequencies.get(value);
            Integer newFrequency = (currentFrequency == null) ? 1 : currentFrequency + 1;
            frequencies.put(value, newFrequency);
        }

        // get value with highest frequency
        float modeValue = 0;
        int highestFrequency = 0;
        for (Map.Entry<Float, Integer> frequencyEntry : frequencies.entrySet()) {
            int frequency = frequencyEntry.getValue();
            if (frequency > highestFrequency) {
                highestFrequency = frequency;
                modeValue = frequencyEntry.getKey();
            }
        }
        return modeValue;
    }

}
