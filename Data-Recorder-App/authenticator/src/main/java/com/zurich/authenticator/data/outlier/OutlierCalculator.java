package com.zurich.authenticator.data.outlier;

import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.calculator.DataCalculator;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.feature.FeatureData;

import java.util.List;

public abstract class OutlierCalculator extends DataCalculator<ClassificationData> {

    protected List<FeatureData> trainedData;
    protected List<FeatureData> currentData;
    protected List<FeatureData> outliers;

    @Override
    public boolean canCalculate() {
        return true;
    }

    @Override
    public ClassificationData calculate() throws DataCalculationException {
        ClassificationData data = new ClassificationData();
        data.setPositivesCount(currentData.size() - getOutliers().size());
        data.setNegativesCount(getOutliers().size());
        try {
            data.calculateValue();
        } catch (ArithmeticException e) {
            throw new DataCalculationException(e);
        }
        return data;
    }

    public float getOutlierRatio() throws DataCalculationException {
        return (float) getOutliers().size() / (float) currentData.size();
    }

    public abstract List<FeatureData> detectOutliers() throws DataCalculationException;

    public List<FeatureData> getOutliers() throws DataCalculationException {
        if (outliers == null) {
            outliers = detectOutliers();
        }
        return outliers;
    }

    public List<FeatureData> getTrainedData() {
        return trainedData;
    }

    public void setTrainedData(List<FeatureData> trainedData) {
        this.trainedData = trainedData;
    }

    public List<FeatureData> getCurrentData() {
        return currentData;
    }

    public void setCurrentData(List<FeatureData> currentData) {
        this.currentData = currentData;
    }

}
