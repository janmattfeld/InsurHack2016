package com.zurich.authenticator.data.DummyData;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.state.StateData;

import java.util.concurrent.TimeUnit;

public class StateDummyDataBatch extends DummyDataBatch<StateData> {

    public static final int DEFAULT_ITEM_COUNT = 3;
    public static final long DEFAULT_TIMESTAMP_DELTA = TimeUnit.SECONDS.toMillis(1);

    private DataBatch<StateData> dataBatch;
    private int expectedSubType;

    public class StateDataManipulator implements DummyDataManipulator<StateData> {
        @Override
        public StateData manipulateData(int index, StateData stateData) {
            return stateData;
        }
    }

    public StateDummyDataBatch(DummyDataBuilder dummyDataBuilder) throws DummyDataException {
        super(dummyDataBuilder);
        expectedSubType = dummyDataBuilder.getExpectedSubType();
        dummyDataManipulator = dummyDataBuilder.getDummyDataManipulator();
        restoreDefaults();
    }

    @Override
    protected void restoreDefaults() {
        super.restoreDefaults();
        if (dummyDataManipulator == null) {
            dummyDataManipulator = new StateDataManipulator();
        }
    }

    @Override
    public StateData generateData(int index) {
        StateData stateData = new StateData(expectedSubType);
        stateData.setTimestamp(generateTimestamps(startTimestamp, timestampDelta, index));
        stateData.setValue(0);
        stateData = dummyDataManipulator.manipulateData(index, stateData);
        return stateData;
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
