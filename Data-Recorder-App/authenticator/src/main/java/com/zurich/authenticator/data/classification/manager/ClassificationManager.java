package com.zurich.authenticator.data.classification.manager;

import android.content.Context;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.DataAggregationObserver;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.classification.aggregator.ClassificationAggregator;
import com.zurich.authenticator.data.classification.aggregator.ClassificationAggregators;
import com.zurich.authenticator.data.classification.aggregator.SpecificWalkingClassificationAggregator;
import com.zurich.authenticator.data.classification.aggregator.WalkingClassificationAggregator;
import com.zurich.authenticator.data.classification.persister.ClassificationMemoryPersister;
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

public final class ClassificationManager extends DataManager {

    private static final String TAG = ClassificationManager.class.getSimpleName();

    private static ClassificationManager instance;

    public static final int CLASSIFICATION_WALKING = 401;
    public static final int CLASSIFICATION_WALKING_SPECIFIC = 402;

    private static final Map<Integer, String> readableClassificationTypes = getReadableClassificationTypes();

    private ClassificationMemoryPersister classificationMemoryPersister;

    private ClassificationManager() {

    }

    public static ClassificationManager getInstance() {
        if (instance == null) {
            instance = new ClassificationManager();
        }
        return instance;
    }

    public static void initialize(Context context) {
        getInstance().initializeManager(context);
    }

    @Override
    public void setupDataAggregators(Context context) throws DataAggregationException {
        List<ClassificationAggregator> classificationAggregators = getDefaultDataAggregators(context);

        for (ClassificationAggregator classificationAggregator : classificationAggregators) {
            classificationAggregator.addAggregationObserver(new DataAggregationObserver<ClassificationData>() {
                @Override
                public void onDataAggregated(ClassificationData classificationData, DataAggregator<ClassificationData> classificationDataDataAggregator) {
                    try {
                        PersisterManager.persist(classificationData, PersisterManager.STRATEGY_MEMORY);
                    } catch (DataPersistingException e) {
                        Logger.e(TAG, "onDataAggregated: ", e);
                    }
                }
            });
            classificationAggregator.startAggregation();
            ClassificationAggregators.getInstance().addDataAggregator(classificationAggregator);
        }
    }

    @Override
    public List<ClassificationAggregator> getDefaultDataAggregators(Context context) {
        List<ClassificationAggregator> aggregators = new ArrayList<>();
        aggregators.add(new WalkingClassificationAggregator(1000));
        aggregators.add(new SpecificWalkingClassificationAggregator(1000));
        // Add new aggregators here
        return aggregators;
    }

    public static float[] getValuesFromClassificationData(List<ClassificationData> classificationDataList) {
        float[] values = new float[classificationDataList.size()];
        for (int i = 0; i < classificationDataList.size(); i++) {
            values[i] = classificationDataList.get(i).getValue();
        }
        return values;
    }

    public static List<ClassificationData> getClassificationDataSince(long timestamp, DataBatch<ClassificationData> classificationDataBatch) throws Exception {
        List<ClassificationData> classificationDataList = classificationDataBatch.getDataSince(timestamp);
        if (classificationDataList.size() == 0) {
            throw new Exception("No classifications available since " + timestamp);
        }
        return classificationDataList;
    }

    public static ClassificationMemoryPersister getMemoryPersister() throws DataPersistingException {
        ClassificationManager instance = getInstance();
        if (instance.classificationMemoryPersister == null) {
            MemoryPersister memoryPersister = (MemoryPersister) PersisterManager.getDataPersister(PersisterManager.STRATEGY_MEMORY);
            instance.classificationMemoryPersister = (ClassificationMemoryPersister) memoryPersister.getPersister(Data.TYPE_CLASSIFICATION);
        }
        return instance.classificationMemoryPersister;
    }

    public static DataBatch<ClassificationData> getMemoryDataBatch(int classificationType) throws DataPersistingException {
        return getMemoryPersister().getDataBatch(classificationType);
    }

    public static float[] getValuesFromClassificaions(List<ClassificationData> classificationDataList) {
        float[] values = new float[classificationDataList.size()];
        for (int i = 0; i < classificationDataList.size(); i++) {
            values[i] = classificationDataList.get(i).getValue();
        }
        return values;
    }

    public static String getReadableClassificationType(int type) {
        if (!readableClassificationTypes.containsKey(type)) {
            return "Unknown Classification";
        }
        return readableClassificationTypes.get(type);
    }

    private static Map<Integer, String> getReadableClassificationTypes() {
        if (readableClassificationTypes != null) {
            return readableClassificationTypes;
        }
        Map<Integer, String> readableSensorTypes = new HashMap<>();
        readableSensorTypes.put(CLASSIFICATION_WALKING, "Walking");
        readableSensorTypes.put(CLASSIFICATION_WALKING_SPECIFIC, "Walking Specific");
        return readableSensorTypes;
    }

}
