package com.zurich.authenticator.data.merger;

import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.classification.manager.ClassificationManager;
import com.zurich.authenticator.data.feature.calculator.MeanFeatureCalculator;
import com.zurich.authenticator.data.feature.calculator.MedianFeatureCalculator;
import com.zurich.authenticator.data.feature.calculator.ModeFeatureCalculator;

import java.util.List;

public class ClassificationMerger extends DataMerger<ClassificationData> {

    private static ClassificationMerger instance;

    private ClassificationMerger() {
        super();
    }

    public static ClassificationMerger getInstance() {
        if (instance == null) {
            instance = new ClassificationMerger();
        }
        return instance;
    }

    public static ClassificationData merge(List<ClassificationData> classificationDataList, int mergeMode) throws MergeException {
        return getInstance().mergeData(classificationDataList, mergeMode);
    }

    @Override
    protected void setupMergeFunctions() {
        mergeFunctionMap.put(DataMerger.MEAN, getMean());
        mergeFunctionMap.put(DataMerger.MODE, getMode());
        mergeFunctionMap.put(DataMerger.MEDIAN, getMedian());
    }

    private float[] getValues(List<ClassificationData> dataList) throws MergeException {
        float[] values = ClassificationManager.getValuesFromClassificaions(dataList);
        if (values.length < 1) {
            throw new MergeException("No values available");
        }
        return values;
    }

    private ClassificationData createMergedClassification(List<ClassificationData> dataList, float value) throws MergeException {
        // get representative data
        ClassificationData midstData = getMidstData(dataList);

        // create merged data
        ClassificationData mergedData = new ClassificationData(midstData);
        mergedData.setValue(value);
        return mergedData;
    }

    private MergeFunction<ClassificationData> getMean() {
        return new MergeFunction<ClassificationData>() {
            @Override
            public ClassificationData merge(List<ClassificationData> dataList) throws MergeException {
                float[] values = getValues(dataList);
                float meanValue = MeanFeatureCalculator.calculateMean(values);
                return createMergedClassification(dataList, meanValue);
            }
        };
    }

    private MergeFunction<ClassificationData> getMode() {
        return new MergeFunction<ClassificationData>() {
            @Override
            public ClassificationData merge(List<ClassificationData> dataList) throws MergeException {
                float[] values = getValues(dataList);
                float modeValue = ModeFeatureCalculator.calculateMode(values);
                return createMergedClassification(dataList, modeValue);
            }
        };
    }

    private MergeFunction<ClassificationData> getMedian() {
        return new MergeFunction<ClassificationData>() {
            @Override
            public ClassificationData merge(List<ClassificationData> dataList) throws MergeException {
                float[] values = getValues(dataList);
                float medianValue = MedianFeatureCalculator.calculateMedian(values);
                return createMergedClassification(dataList, medianValue);
            }
        };
    }

}
