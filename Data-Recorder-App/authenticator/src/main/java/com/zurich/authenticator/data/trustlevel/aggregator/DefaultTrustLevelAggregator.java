package com.zurich.authenticator.data.trustlevel.aggregator;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.classification.manager.ClassificationManager;
import com.zurich.authenticator.data.merger.ClassificationMerger;
import com.zurich.authenticator.data.merger.DataMerger;
import com.zurich.authenticator.data.merger.MergeException;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.trustlevel.TrustLevelData;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class DefaultTrustLevelAggregator extends TrustLevelAggregator {

    private static final String TAG = DefaultTrustLevelAggregator.class.getSimpleName();

    public DefaultTrustLevelAggregator(long aggregationInterval) {
        super(DataAggregator.TYPE_DEFAULT, aggregationInterval);
    }

    @Override
    public TrustLevelData aggregate() throws DataAggregationException {
        List<Integer> classificationTypes = getClassificationTypes();
        List<ClassificationData> classificationDataList = getSpecificClassifications(classificationTypes);

        try {
            ClassificationData mergedClassification = ClassificationMerger.merge(classificationDataList, DataMerger.MEAN);
            float value = mergedClassification.getValue();
            float confidence = (float) classificationDataList.size() / classificationTypes.size();
            return new TrustLevelData(value, confidence);
        } catch (MergeException ex) {
            throw new DataAggregationException("Unable to merge classifications", ex);
        }
    }

    public static List<Integer> getClassificationTypes() {
        List<Integer> classificationTypes = new ArrayList<>();
        classificationTypes.add(ClassificationManager.CLASSIFICATION_WALKING_SPECIFIC);
        return classificationTypes;
    }

    public static List<ClassificationData> getSpecificClassifications(List<Integer> classificationTypes) {
        List<ClassificationData> classificationDataList = new ArrayList<>();
        for (Integer classificationType : classificationTypes) {
            try {
                classificationDataList.add(getLatestClassification(classificationType));
            } catch (DataPersistingException dataPersistingException) {
                Logger.v(TAG, "Unable to get latest classification for " + classificationType + ": " + dataPersistingException.getMessage());
            }
        }
        return classificationDataList;
    }

    public static ClassificationData getLatestClassification(int classificationType) throws DataPersistingException {
        DataBatch<ClassificationData> classificationDataBatch = ClassificationManager.getMemoryDataBatch(classificationType);
        try {
            return classificationDataBatch.getLast();
        } catch (IndexOutOfBoundsException ex) {
            throw new DataPersistingException(ex);
        }
    }

}
