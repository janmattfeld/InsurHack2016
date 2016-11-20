package com.zurich.authenticator.data.merger;

import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.feature.calculator.MeanFeatureCalculator;
import com.zurich.authenticator.data.feature.calculator.MedianFeatureCalculator;
import com.zurich.authenticator.data.feature.calculator.ModeFeatureCalculator;
import com.zurich.authenticator.data.feature.manager.FeatureManager;

import java.util.List;

public class FeatureMerger extends DataMerger<FeatureData> {

    private static FeatureMerger instance;

    private FeatureMerger() {
        super();
    }

    public static FeatureMerger getInstance() {
        if (instance == null) {
            instance = new FeatureMerger();
        }
        return instance;
    }

    public static FeatureData merge(List<FeatureData> featureDataList, int mergeMode) throws MergeException {
        return getInstance().mergeData(featureDataList, mergeMode);
    }

    @Override
    protected void setupMergeFunctions() {
        mergeFunctionMap.put(DataMerger.MEAN, getMean());
        mergeFunctionMap.put(DataMerger.MODE, getMode());
        mergeFunctionMap.put(DataMerger.MEDIAN, getMedian());
    }

    private float[][] getValues(List<FeatureData> dataList) throws MergeException {
        float[][] values = FeatureManager.getValuesFromFeatureData(dataList);
        if (values.length < 1) {
            throw new MergeException("No values available");
        }
        return values;
    }

    private FeatureData createMergedFeature(List<FeatureData> dataList, float[] values) throws MergeException {
        // get representative data
        FeatureData midstData = getMidstData(dataList);

        // create merged data
        FeatureData mergedData = new FeatureData(midstData);
        mergedData.setValues(values);
        return mergedData;
    }

    private MergeFunction<FeatureData> getMean() {
        return new MergeFunction<FeatureData>() {
            @Override
            public FeatureData merge(List<FeatureData> dataList) throws MergeException {
                float[][] values = getValues(dataList);
                float[] meanValues = MeanFeatureCalculator.calculateMean(values);
                return createMergedFeature(dataList, meanValues);
            }
        };
    }

    private MergeFunction<FeatureData> getMode() {
        return new MergeFunction<FeatureData>() {
            @Override
            public FeatureData merge(List<FeatureData> dataList) throws MergeException {
                float[][] values = getValues(dataList);
                float[] modeValues = ModeFeatureCalculator.calculateMode(values);
                return createMergedFeature(dataList, modeValues);
            }
        };
    }

    private MergeFunction<FeatureData> getMedian() {
        return new MergeFunction<FeatureData>() {
            @Override
            public FeatureData merge(List<FeatureData> dataList) throws MergeException {
                float[][] values = getValues(dataList);
                float[] medianValues = MedianFeatureCalculator.calculateMedian(values);
                return createMergedFeature(dataList, medianValues);
            }
        };
    }

}
