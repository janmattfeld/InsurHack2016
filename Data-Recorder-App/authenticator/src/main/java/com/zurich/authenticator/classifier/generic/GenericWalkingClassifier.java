package com.zurich.authenticator.classifier.generic;

import com.zurich.authenticator.data.batch.BinnedDataBatch;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.classification.ClassificationException;
import com.zurich.authenticator.data.classification.manager.ClassificationManager;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.merger.MergeException;
import com.zurich.authenticator.data.persister.DataPersistingException;

import java.util.ArrayList;
import java.util.List;

public final class GenericWalkingClassifier extends GenericClassifier {

    public GenericWalkingClassifier() {
        super(ClassificationManager.CLASSIFICATION_WALKING);
    }

    @Override
    protected List<DataBatch> getRequiredDataBatches() throws ClassificationException {
        List<DataBatch> dataBatches = new ArrayList<>();
        try {
            dataBatches.add(FeatureManager.getMemoryDataBatch(FeatureManager.TYPE_VARIANCE_ACCELEROMETER));
        } catch (DataPersistingException ex) {
            throw new ClassificationException("Required data not available", ex);
        }
        return dataBatches;
    }

    @Override
    protected boolean isClassified(List<BinnedDataBatch> binnedDataBatches, int index) throws ClassificationException {
        if (binnedDataBatches.isEmpty()) {
            throw new ClassificationException("No binned data batches");
        }

        for (BinnedDataBatch binnedDataBatch : binnedDataBatches) {
            if (binnedDataBatch.getSubType() == FeatureManager.TYPE_VARIANCE_ACCELEROMETER) {
                if (!binnedDataBatch.hasDataInBin(index)) {
                    throw new ClassificationException("No binned data available");
                }
                FeatureData featureData;
                try {
                    featureData = (FeatureData) binnedDataBatch.getRepresentativeDataInBin(index);
                } catch (MergeException e) {
                    throw new ClassificationException(e);
                }
                float x = featureData.getValues()[0];
                float y = featureData.getValues()[1];
                float z = featureData.getValues()[2];

                if (x < 5 || x > 20) {
                    return false;
                }

                if (y < 15 || y > 45) {
                    return false;
                }

                if (z < 15 || z > 45) {
                    return false;
                }
            }
        }
        return true;
    }

}
