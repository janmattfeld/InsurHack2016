package com.zurich.authenticator.classifier.specific;

import com.zurich.authenticator.classifier.Classifier;
import com.zurich.authenticator.classifier.generic.GenericClassifier;
import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.classification.ClassificationException;
import com.zurich.authenticator.data.classification.aggregator.ClassificationAggregator;
import com.zurich.authenticator.data.classification.aggregator.ClassificationAggregators;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.merger.ClassificationMerger;
import com.zurich.authenticator.data.merger.DataMerger;
import com.zurich.authenticator.data.merger.MergeException;
import com.zurich.authenticator.data.outlier.LocalOutlierCalculator;
import com.zurich.authenticator.data.outlier.OutlierCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class SpecificClassifier extends Classifier {

    private static final String TAG = SpecificClassifier.class.getSimpleName();

    protected int genericClassificationType;
    protected long minimumDuration = TimeUnit.SECONDS.toMillis(3);
    protected long maximumDuration = TimeUnit.SECONDS.toMillis(15);

    protected Set<Integer> featureTypes = new HashSet<>();
    protected Map<Integer, OutlierCalculator> outlierCalculatorMap = new HashMap<>();

    public SpecificClassifier(int classificationType, int genericClassificationType) {
        super(classificationType);
        this.genericClassificationType = genericClassificationType;
        featureTypes = getFeatureTypes();
        setupOutlierCalculators();
    }

    protected void setupOutlierCalculators() {
        for (Integer featureType : featureTypes) {
            OutlierCalculator outlierCalculator = new LocalOutlierCalculator();
            outlierCalculator.setTrainedData(getTrainedData(featureType));
            outlierCalculatorMap.put(featureType, outlierCalculator);
        }
    }

    public abstract Set<Integer> getFeatureTypes();

    @Override
    public boolean canClassify() {
        return false;
    }

    @Override
    public ClassificationData classify() throws ClassificationException {
        // update current data
        for (Map.Entry<Integer, OutlierCalculator> outlierCalculatorEntry : outlierCalculatorMap.entrySet()) {
            List<FeatureData> currentData = getCurrentData(outlierCalculatorEntry.getKey());
            outlierCalculatorEntry.getValue().setCurrentData(currentData);
        }

        // perform classification
        ClassificationData classificationData = classify(outlierCalculatorMap);
        classificationData.setClassificationType(classificationType);
        return classificationData;
    }

    public static ClassificationData classify(Map<Integer, OutlierCalculator> outlierCalculators) throws ClassificationException {
        List<ClassificationData> classificationDataList = new ArrayList<>();
        for (Map.Entry<Integer, OutlierCalculator> outlierCalculatorEntry : outlierCalculators.entrySet()) {
            try {
                ClassificationData classificationData = outlierCalculatorEntry.getValue().calculate();
                classificationDataList.add(classificationData);
            } catch (DataCalculationException ex) {
                throw new ClassificationException(ex);
            }
        }

        if (classificationDataList.size() < 1) {
            throw new ClassificationException("No outlier detection was operational");
        }

        try {
            ClassificationData mergedClassificationData = ClassificationMerger.merge(classificationDataList, DataMerger.MEAN);
            return mergedClassificationData;
        } catch (MergeException e) {
            throw new ClassificationException("Unable to merge classifications", e);
        }
    }

    protected void updateTrainedData() {
        // TODO: get trained data if required
    }

    public List<FeatureData> getTrainedData(int featureType) {
        // TODO: get trained data
        return new ArrayList<>();
    }

    public List<FeatureData> getCurrentData(int featureType) {
        // TODO: get current data
        return new ArrayList<>();
    }

    public GenericClassifier getGenericClassifier() throws DataAggregationException {
        return (GenericClassifier) getGenericClassificationAggregator().getClassifier();
    }

    public ClassificationAggregator getGenericClassificationAggregator() throws DataAggregationException {
        return ClassificationAggregators.getInstance().getAggregator(genericClassificationType);
    }

    public int getGenericClassificationType() {
        return genericClassificationType;
    }

    public long getMinimumDuration() {
        return minimumDuration;
    }

    public long getMaximumDuration() {
        return maximumDuration;
    }
}
