package com.zurich.authenticator.data.DummyData;

import com.zurich.authenticator.data.generic.Data;

public class DummyDataBuilder {

    private long startTimestamp = NOT_SET;
    private long timestampDelta = NOT_SET;
    private int itemCount = NOT_SET;

    private int expectedType;
    private int expectedSubType = NOT_SET;

    private DummyDataManipulator dummyDataManipulator;

    static final int NOT_SET = -1;

    private DummyDataBuilder() {

    }

    private static DummyDataBuilder forData(int dataType) {
        DummyDataBuilder instance = new DummyDataBuilder();
        instance.expectedType = dataType;

        return instance;
    }

    public static DummyDataBuilder forSensorEventData() {
        return forData(Data.TYPE_SENSOR_EVENT);
    }

    public static DummyDataBuilder forStateData() {
        return forData(Data.TYPE_STATE);
    }

    public static DummyDataBuilder forFeatureData() {
        return forData(Data.TYPE_FEATURE);
    }

    public static DummyDataBuilder forClassificationData() {
        return forData(Data.TYPE_CLASSIFICATION);
    }

    public static DummyDataBuilder forTrustLevelData() {
        return forData(Data.TYPE_TRUST_LEVEL);
    }

    public DummyDataBuilder withSubType(int subType) {
        expectedSubType = subType;
        return this;
    }

    public DummyDataBuilder withItemCount(int itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    public DummyDataBuilder withStartTimestamp(long timestamp) {
        startTimestamp = timestamp;
        return this;
    }

    public DummyDataBuilder withTimestampDelta(long timestampDelta) {
        this.timestampDelta = timestampDelta;
        return this;
    }

    public DummyDataBuilder withDummyDataManipulator(DummyDataManipulator dummyDataManipulator) {
        this.dummyDataManipulator = dummyDataManipulator;
        return this;
    }

    public DummyDataBatch build() throws DummyDataException {
        validateConfiguration();
        switch (expectedType) {
            case Data.TYPE_SENSOR_EVENT:
                return new SensorEventDummyDataBatch(this);
            case Data.TYPE_STATE:
                return new StateDummyDataBatch(this);
            case Data.TYPE_FEATURE:
                return new FeatureDummyDataBatch(this);
            case Data.TYPE_CLASSIFICATION:
                return new ClassificationDummyDataBatch(this);
            case Data.TYPE_TRUST_LEVEL:
                return new TrustLevelDummyDataBatch(this);
            default:
                throw new DummyDataException();
        }
    }

    public void validateConfiguration() throws DummyDataException {
        if ((itemCount < 0) && (itemCount != NOT_SET)) {
            throw new DummyDataException("ItemCount must be positive.");
        }
        if ((startTimestamp < 0) && (startTimestamp != NOT_SET)) {
            throw new DummyDataException("StartTimestamp must be positive.");
        }
        if ((timestampDelta <= 0) && (timestampDelta != NOT_SET)) {
            throw new DummyDataException("TimestampDelta must be greater than 0.");
        }
        // TODO validate Manipulator
    }


    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getExpectedSubType() {
        return expectedSubType;
    }

    public void setExpectedSubType(int expectedSubType) {
        this.expectedSubType = expectedSubType;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getTimestampDelta() {
        return timestampDelta;
    }

    public DummyDataManipulator getDummyDataManipulator() {
        return dummyDataManipulator;
    }

}