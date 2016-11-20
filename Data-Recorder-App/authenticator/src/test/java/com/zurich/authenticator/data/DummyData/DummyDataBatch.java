package com.zurich.authenticator.data.DummyData;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.generic.Data;

import java.util.ArrayList;
import java.util.List;


public abstract class DummyDataBatch<T extends Data> {

    DummyDataManipulator<T> dummyDataManipulator;
    long startTimestamp = DummyDataBuilder.NOT_SET;
    long timestampDelta = DummyDataBuilder.NOT_SET;
    int itemCount = DummyDataBuilder.NOT_SET;

    DataBatch<T> dataBatch;

    public DummyDataBatch() {
        restoreDefaults();
    }

    public DummyDataBatch(DummyDataBuilder dummyDataBuilder) {
        itemCount = dummyDataBuilder.getItemCount();
        startTimestamp = dummyDataBuilder.getStartTimestamp();
        timestampDelta = dummyDataBuilder.getTimestampDelta();
    }

    protected void restoreDefaults() {
        if (itemCount == DummyDataBuilder.NOT_SET) {
            itemCount = getDefaultItemCount();
        }
        if (startTimestamp == DummyDataBuilder.NOT_SET) {
            startTimestamp = System.currentTimeMillis();
        }
        if (timestampDelta == DummyDataBuilder.NOT_SET) {
            timestampDelta = getDefaultTimestampDelta();
        }
    }

    public DataBatch<T> getDataBatch() {
        if (dataBatch == null) {
            dataBatch = new DataBatch<>();
            List<T> dataList = generateDataList(itemCount);
            dataBatch.setDataList(dataList);
        }
        return dataBatch;
    }

    public List<T> generateDataList(int itemCount) {
        List<T> dataList = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            dataList.add(generateData(i));
        }
        return dataList;
    }

    public abstract T generateData(int index);

    public abstract int getDefaultItemCount();

    public abstract long getDefaultTimestampDelta();

    public static long generateTimestamps(long startTimestamp, long timestampDelta, int index) {
        return startTimestamp + (timestampDelta * index);
    }

    public DummyDataManipulator<T> getDummyDataManipulator() {
        return dummyDataManipulator;
    }

    public void setDummyDataManipulator(DummyDataManipulator<T> dummyDataManipulator) {
        this.dummyDataManipulator = dummyDataManipulator;
    }

}
