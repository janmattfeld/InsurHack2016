package com.zurich.authenticator.classifier.generic;

import com.zurich.authenticator.classifier.Classifier;
import com.zurich.authenticator.data.batch.BinnedDataBatch;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.classification.ClassificationException;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericClassifier extends Classifier {

    private static final String TAG = GenericClassifier.class.getSimpleName();

    protected int binCount = 15;
    protected long binSize = 250;

    public GenericClassifier(int classificationType) {
        super(classificationType);
    }

    protected abstract List<DataBatch> getRequiredDataBatches() throws ClassificationException;

    protected BinnedDataBatch getBinnedDataBatch(DataBatch dataBatch) throws ClassificationException {
        BinnedDataBatch binnedDataBatch = new BinnedDataBatch(binCount, binSize, new ArrayList(dataBatch.getDataList()));
        binnedDataBatch.setType(dataBatch.getType());
        binnedDataBatch.setSubType(dataBatch.getSubType());
        return binnedDataBatch;
    }

    protected List<BinnedDataBatch> getBinnedDataBatches(List<DataBatch> dataBatches) throws ClassificationException {
        List<BinnedDataBatch> binnedDataBatches = new ArrayList<>(dataBatches.size());
        for (DataBatch dataBatch : dataBatches) {
            binnedDataBatches.add(getBinnedDataBatch(dataBatch));
        }
        return binnedDataBatches;
    }

    @Override
    public boolean canClassify() {
        return true;
    }

    @Override
    public ClassificationData classify() throws ClassificationException {
        ClassificationData data = new ClassificationData();
        data.setClassificationType(classificationType);

        List<DataBatch> dataBatches = getRequiredDataBatches();
        List<BinnedDataBatch> binnedDataBatches = getBinnedDataBatches(dataBatches);

        for (int binIndex = 0; binIndex < binCount; binIndex++) {
            try {
                boolean classified = isClassified(binnedDataBatches, binIndex);
                data.addClassification(classified);
            } catch (ClassificationException e) {
                Logger.v(TAG, "Unable to classify bin: " + e.getMessage());
            }
        }

        try {
            data.calculateValue();
        } catch (ArithmeticException e) {
            throw new ClassificationException("No bin could be classified", e);
        }

        return data;
    }

    protected abstract boolean isClassified(List<BinnedDataBatch> binnedDataBatches, int index) throws ClassificationException;

    public int getBinCount() {
        return binCount;
    }

    public void setBinCount(int binCount) {
        this.binCount = binCount;
    }

    public long getBinSize() {
        return binSize;
    }

    public void setBinSize(long binSize) {
        this.binSize = binSize;
    }
}
