package com.zurich.authenticator.data.DummyData;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.feature.FeatureData;

import java.util.concurrent.TimeUnit;

public class FeatureDummyDataBatch extends DummyDataBatch<FeatureData> {

    public static final int DEFAULT_ITEM_COUNT = 3;
    public static final long DEFAULT_TIMESTAMP_DELTA = TimeUnit.SECONDS.toMillis(1);

    private DataBatch<FeatureData> dataBatch;
    private int expectedSubType = DummyDataBuilder.NOT_SET;

    private class FeatureDataManipulator implements DummyDataManipulator<FeatureData> {
        @Override
        public FeatureData manipulateData(int index, FeatureData featureData) {
            return featureData;
        }
    }

    public FeatureDummyDataBatch(DummyDataBuilder dummyDataBuilder) throws DummyDataException {
        super(dummyDataBuilder);
        expectedSubType = dummyDataBuilder.getExpectedSubType();
        dummyDataManipulator = dummyDataBuilder.getDummyDataManipulator();
        restoreDefaults();
    }

    @Override
    protected void restoreDefaults() {
        super.restoreDefaults();
        if (dummyDataManipulator == null) {
            dummyDataManipulator = new FeatureDataManipulator();
        }
    }

    @Override
    public FeatureData generateData(int index) {
        FeatureData featureData = new FeatureData(expectedSubType);
        featureData.setTimestamp(generateTimestamps(startTimestamp, timestampDelta, index));
        featureData.setValues(new float[]{0, 0, 0});
        featureData = dummyDataManipulator.manipulateData(index, featureData);
        return featureData;
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
