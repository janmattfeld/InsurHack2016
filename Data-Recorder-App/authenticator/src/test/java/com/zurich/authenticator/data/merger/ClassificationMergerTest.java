package com.zurich.authenticator.data.merger;

import com.zurich.authenticator.data.feature.calculator.MeanFeatureCalculator;
import com.zurich.authenticator.data.feature.calculator.MedianFeatureCalculator;
import com.zurich.authenticator.data.feature.calculator.ModeFeatureCalculator;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ClassificationMergerTest {

    private float[] sampleValues = new float[]{13, 18, 13, 14, 13, 16, 14, 21, 13};

    @Test
    public void mean_distinctValues_isCorrect() throws Exception {
        float expected = 15;
        float actual = MeanFeatureCalculator.calculateMean(sampleValues);
        assertEquals("Mean is incorrect", expected, actual);
    }

    @Test
    public void mean_singleValue_isCorrect() throws Exception {
        float expected = 123;
        float actual = MeanFeatureCalculator.calculateMean(new float[]{expected});
        assertEquals("Mean is incorrect", expected, actual);
    }

    @Test
    public void mode_distinctValues_isCorrect() throws Exception {
        float expected = 13;
        float actual = ModeFeatureCalculator.calculateMode(sampleValues);
        assertEquals("Mode is incorrect", expected, actual);
    }

    @Test
    public void mode_singleValue_isCorrect() throws Exception {
        float expected = 123;
        float actual = ModeFeatureCalculator.calculateMode(new float[]{expected});
        assertEquals("Mode is incorrect", expected, actual);
    }

    @Test
    public void median_distinctValues_isCorrect() throws Exception {
        float expected = 14;
        float actual = MedianFeatureCalculator.calculateMedian(sampleValues);
        assertEquals("Median is incorrect", expected, actual);
    }

    @Test
    public void median_singleValue_isCorrect() throws Exception {
        float expected = 123;
        float actual = MedianFeatureCalculator.calculateMedian(new float[]{expected});
        assertEquals("Median is incorrect", expected, actual);
    }

}