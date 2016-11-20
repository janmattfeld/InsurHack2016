package com.zurich.authenticator.data.DummyData;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.trustlevel.TrustLevelData;

import java.util.concurrent.TimeUnit;

public class TrustLevelDummyDataBatch extends DummyDataBatch<TrustLevelData> {

    public static final int DEFAULT_ITEM_COUNT = 3;
    public static final long DEFAULT_TIMESTAMP_DELTA = TimeUnit.SECONDS.toMillis(1);

    private DataBatch<TrustLevelData> dataBatch;

    private class TrustLevelDataManipulator implements DummyDataManipulator<TrustLevelData> {
        @Override
        public TrustLevelData manipulateData(int index, TrustLevelData trustLevelData) {
            return trustLevelData;
        }
    }

    public TrustLevelDummyDataBatch(DummyDataBuilder dummyDataBuilder) {
        super(dummyDataBuilder);
        dummyDataManipulator = dummyDataBuilder.getDummyDataManipulator();
        restoreDefaults();
    }

    @Override
    protected void restoreDefaults() {
        super.restoreDefaults();
        if (dummyDataManipulator == null) {
            dummyDataManipulator = new TrustLevelDataManipulator();
        }
    }

    @Override
    public TrustLevelData generateData(int index) {
        TrustLevelData trustLevelData = new TrustLevelData();
        trustLevelData.setTimestamp(generateTimestamps(startTimestamp, timestampDelta, index));
        trustLevelData.setValue(0);
        trustLevelData.setConfidence(0);
        trustLevelData = dummyDataManipulator.manipulateData(index, trustLevelData);
        return trustLevelData;
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
