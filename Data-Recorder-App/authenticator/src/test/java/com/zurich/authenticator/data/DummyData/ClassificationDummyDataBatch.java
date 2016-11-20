package com.zurich.authenticator.data.DummyData;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.classification.ClassificationData;

import java.util.concurrent.TimeUnit;

public class ClassificationDummyDataBatch extends DummyDataBatch<ClassificationData> {

    private static final int DEFAULT_ITEM_COUNT = 3;
    private static final long DEFAULT_TIMESTAMP_DELTA = TimeUnit.SECONDS.toMillis(1);

    private DataBatch<ClassificationData> dataBatch;
    private int expectedSubType;

    private class ClassificationDataManipulator implements DummyDataManipulator<ClassificationData> {
        @Override
        public ClassificationData manipulateData(int index, ClassificationData classificationData) {
            return classificationData;
        }
    }

    public ClassificationDummyDataBatch(DummyDataBuilder dummyDataBuilder) throws DummyDataException {
        super(dummyDataBuilder);
        expectedSubType = dummyDataBuilder.getExpectedSubType();
        dummyDataManipulator = dummyDataBuilder.getDummyDataManipulator();
        restoreDefaults();
    }

    @Override
    protected void restoreDefaults() {
        super.restoreDefaults();
        if (dummyDataManipulator == null) {
            dummyDataManipulator = new ClassificationDataManipulator();
        }
    }

    @Override
    public ClassificationData generateData(int index) {
        ClassificationData classificationData = new ClassificationData(expectedSubType);
        classificationData.setTimestamp(generateTimestamps(startTimestamp, timestampDelta, index));
        classificationData.setValue(0);
        classificationData = dummyDataManipulator.manipulateData(index, classificationData);
        return classificationData;
    }

    @Override
    public int getDefaultItemCount() {
        return DEFAULT_ITEM_COUNT;
    }

    @Override
    public long getDefaultTimestampDelta() {
        return DEFAULT_TIMESTAMP_DELTA;
    }

}
