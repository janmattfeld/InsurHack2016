package com.zurich.authenticator.data.feature.manager;

import android.content.Context;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.DataAggregationObserver;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.feature.aggregator.AccelerationDeviationAggregator;
import com.zurich.authenticator.data.feature.aggregator.AccelerationMeanAbsoluteDifferenceAggregator;
import com.zurich.authenticator.data.feature.aggregator.AccelerationMeanAggregator;
import com.zurich.authenticator.data.feature.aggregator.AccelerationVarianceAggregator;
import com.zurich.authenticator.data.feature.aggregator.FeatureAggregator;
import com.zurich.authenticator.data.feature.aggregator.FeatureAggregators;
import com.zurich.authenticator.data.feature.persister.FeatureMemoryPersister;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.manager.DataManager;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.PersisterManager;
import com.zurich.authenticator.data.persister.memory.MemoryPersister;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FeatureManager extends DataManager {

    private static final String TAG = FeatureManager.class.getSimpleName();

    private static FeatureManager instance;

    public static final int TYPE_VARIANCE = 3010;
    public static final int TYPE_VARIANCE_ACCELEROMETER = TYPE_VARIANCE + 1;

    public static final int TYPE_MEAN = 3020;
    public static final int TYPE_MEAN_ACCELEROMETER = TYPE_MEAN + 1;

    public static final int TYPE_MEAN_ABSOLUTE_DIFFERENCE = 3030;
    public static final int TYPE_MEAN_ABSOLUTE_DIFFERENCE_ACCELEROMETER = TYPE_MEAN_ABSOLUTE_DIFFERENCE + 1;

    public static final int TYPE_STANDARD_DEVIATION = 3040;
    public static final int TYPE_STANDARD_DEVIATION_ACCELEROMETER = TYPE_STANDARD_DEVIATION + 1;

    public static final int TYPE_MODE = 3050;

    private static final Map<Integer, String> readableFeatureTypes = getReadableFeatureTypes();
    private FeatureMemoryPersister featureMemoryPersister;

    private FeatureManager() {

    }

    public static FeatureManager getInstance() {
        if (instance == null) {
            instance = new FeatureManager();
        }
        return instance;
    }

    public static void initialize(Context context) {
        getInstance().initializeManager(context);
    }

    @Override
    public void setupDataAggregators(Context context) throws DataAggregationException {
        List<FeatureAggregator> featureAggregators = getDefaultDataAggregators(context);

        for (FeatureAggregator featureAggregator : featureAggregators) {
            featureAggregator.addAggregationObserver(new DataAggregationObserver<FeatureData>() {
                @Override
                public void onDataAggregated(FeatureData featureData, DataAggregator<FeatureData> featureAggregator) {
                    try {
                        PersisterManager.persist(featureData, PersisterManager.STRATEGY_MEMORY);
                    } catch (DataPersistingException e) {
                        Logger.e(TAG, "onDataAggregated: ", e);
                    }
                }
            });
            featureAggregator.startAggregation();
            FeatureAggregators.getInstance().addDataAggregator(featureAggregator);
        }
    }

    @Override
    public List<FeatureAggregator> getDefaultDataAggregators(Context context) {
        List<FeatureAggregator> aggregators = new ArrayList<>();
        aggregators.add(new AccelerationMeanAggregator(1000));
        aggregators.add(new AccelerationVarianceAggregator(250));
        aggregators.add(new AccelerationDeviationAggregator(500));
        aggregators.add(new AccelerationMeanAbsoluteDifferenceAggregator(500));
        // Add new aggregators here
        return aggregators;
    }

    public static FeatureMemoryPersister getMemoryPersister() throws DataPersistingException {
        FeatureManager instance = getInstance();
        if (instance.featureMemoryPersister == null) {
            MemoryPersister memoryPersister = (MemoryPersister) PersisterManager.getDataPersister(PersisterManager.STRATEGY_MEMORY);
            instance.featureMemoryPersister = (FeatureMemoryPersister) memoryPersister.getPersister(Data.TYPE_FEATURE);
        }
        return instance.featureMemoryPersister;
    }

    public static DataBatch<FeatureData> getMemoryDataBatch(int featureType) throws DataPersistingException {
        return getMemoryPersister().getDataBatch(featureType);
    }

    public static String getReadableFeatureType(int type) {
        if (!readableFeatureTypes.containsKey(type)) {
            return "Unknown Feature";
        }
        return readableFeatureTypes.get(type);
    }

    private static Map<Integer, String> getReadableFeatureTypes() {
        if (readableFeatureTypes != null) {
            return readableFeatureTypes;
        }
        Map<Integer, String> readableFeatureTypes = new HashMap<>();
        readableFeatureTypes.put(TYPE_MEAN, "Mean");
        readableFeatureTypes.put(TYPE_MEAN_ABSOLUTE_DIFFERENCE, "Mean Absolute Difference");
        readableFeatureTypes.put(TYPE_MEAN_ABSOLUTE_DIFFERENCE_ACCELEROMETER, "Mean Absolute Difference Accelerometer");
        readableFeatureTypes.put(TYPE_VARIANCE, "Variance");
        readableFeatureTypes.put(TYPE_VARIANCE_ACCELEROMETER, "Variance Accelerometer");
        readableFeatureTypes.put(TYPE_STANDARD_DEVIATION, "Standard Deviation");
        readableFeatureTypes.put(TYPE_STANDARD_DEVIATION_ACCELEROMETER, "Standard Deviation Accelerometer");
        return readableFeatureTypes;
    }


    public static float[][] getValuesFromFeatureData(List<FeatureData> featureDataList) {
        float[][] values = new float[featureDataList.size()][];
        for (int i = 0; i < featureDataList.size(); i++) {
            values[i] = featureDataList.get(i).getValues();
        }
        return values;
    }

    public static float[] getValuesInDimension(List<FeatureData> featureDataList, int dimension) {
        float[][] values = getValuesFromFeatureData(featureDataList);
        return getValuesInDimension(values, dimension);
    }

    public static float[] getValuesInDimension(float[][] values, int dimension) {
        float[] dimensionValues = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            dimensionValues[i] = values[i][dimension];
        }
        return dimensionValues;
    }

    public static List<FeatureData> getFeatureDataSince(long timestamp, DataBatch<FeatureData> featureDataBatch) throws Exception {
        List<FeatureData> featureDataList = featureDataBatch.getDataSince(timestamp);
        if (featureDataList.size() == 0) {
            throw new Exception("No features available since " + timestamp);
        }
        return featureDataList;
    }

}
