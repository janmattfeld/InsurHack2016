package com.zurich.authenticator.data.DummyData;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.sensor.SensorEventData;

import java.util.concurrent.TimeUnit;

public class SensorEventDummyDataBatch extends DummyDataBatch<SensorEventData> {

    public static final int DEFAULT_ITEM_COUNT = 3;
    public static final long DEFAULT_TIMESTAMP_DELTA = TimeUnit.SECONDS.toMillis(1);

    private DataBatch<SensorEventData> dataBatch;
    private int expectedSubType;

    private class SensorEventDataManipulator implements DummyDataManipulator<SensorEventData> {
        @Override
        public SensorEventData manipulateData(int index, SensorEventData sensorEventData) {
            return sensorEventData;
        }
    }

    public SensorEventDummyDataBatch(DummyDataBuilder dummyDataBuilder) throws DummyDataException {
        super(dummyDataBuilder);
        expectedSubType = dummyDataBuilder.getExpectedSubType();
        dummyDataManipulator = dummyDataBuilder.getDummyDataManipulator();
        restoreDefaults();
    }

    @Override
    protected void restoreDefaults() {
        super.restoreDefaults();
        if (dummyDataManipulator == null) {
            dummyDataManipulator = new SensorEventDataManipulator();
        }
    }

    @Override
    public SensorEventData generateData(int index) {
        SensorEventData sensorEventData = new SensorEventData(expectedSubType);
        sensorEventData.setTimestamp(generateTimestamps(startTimestamp, timestampDelta, index));
        sensorEventData.setValues(new float[]{0, 0, 0});
        sensorEventData = dummyDataManipulator.manipulateData(index, sensorEventData);
        return sensorEventData;
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
