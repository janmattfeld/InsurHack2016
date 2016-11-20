package com.zurich.authenticator.data.batch;

import com.zurich.authenticator.data.DummyData.DummyDataBuilder;
import com.zurich.authenticator.data.DummyData.DummyDataException;
import com.zurich.authenticator.data.DummyData.DummyDataManipulator;
import com.zurich.authenticator.data.DummyData.FeatureDummyDataBatch;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersistingException;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class DataBatchTest {

    private static final long startTimestamp = TimeUnit.DAYS.toMillis(30);
    private DataBatch<Data> dataBatch;

    @Before
    public void setUp() throws Exception {
        dataBatch = createMockedDataBatch(10);
    }

    public static DataBatch<Data> createMockedDataBatch(int itemCount) throws DataPersistingException {
        DataBatch<Data> dataBatch = new DataBatch<>();
        List<Data> dataList = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            Data data = mock(Data.class);
            data.setType(Data.TYPE_NOT_SET);
            dataList.add(data);
        }
        dataBatch.setDataList(dataList);
        return dataBatch;
    }

    public static DataBatch<Data> createDummyDataBatch(int itemCount) throws DummyDataException {
        FeatureDummyDataBatch featureDummyDataBatch = (FeatureDummyDataBatch) DummyDataBuilder.forFeatureData()
                .withItemCount(itemCount)
                .withStartTimestamp(startTimestamp)
                .withTimestampDelta(TimeUnit.SECONDS.toMillis(1))
                .withDummyDataManipulator(new DummyDataManipulator<FeatureData>() {
                    @Override
                    public FeatureData manipulateData(int index, FeatureData data) {
                        data.setValues(new float[]{index, index, index});
                        return data;
                    }
                })
                .build();

        DataBatch<Data> dataBatch = new DataBatch<>();
        dataBatch.getDataList().addAll(featureDummyDataBatch.getDataBatch().getDataList());
        return dataBatch;
    }

    @Test
    public void add_newData_addsData() throws Exception {
        int oldItemCount = dataBatch.getDataList().size();
        Data newData = mock(Data.class);
        dataBatch.add(newData);
        int newItemCount = dataBatch.getDataList().size();
        assertEquals("List size didn't increase by 1", oldItemCount + 1, newItemCount);
        assertTrue("List doesn't contain new data", dataBatch.getDataList().contains(newData));
    }

    @Test
    public void add_existingData_ignoresData() throws Exception {
        int oldItemCount = dataBatch.getDataList().size();
        Data existingData = dataBatch.getFirst();
        dataBatch.add(existingData);
        int newItemCount = dataBatch.getDataList().size();
        assertEquals("List size increased by 1", oldItemCount, newItemCount);
    }

    @Test
    public void remove_newData_ignoresData() throws Exception {
        int oldItemCount = dataBatch.getDataList().size();
        Data newData = mock(Data.class);
        dataBatch.remove(newData);
        int newItemCount = dataBatch.getDataList().size();
        assertEquals("List size changed", oldItemCount, newItemCount);
    }

    @Test
    public void remove_existingData_removesData() throws Exception {
        int oldItemCount = dataBatch.getDataList().size();
        Data existingData = dataBatch.getFirst();
        dataBatch.remove(existingData);
        int newItemCount = dataBatch.getDataList().size();
        assertEquals("List size didn't decrease by 1", oldItemCount - 1, newItemCount);
    }

    @Test
    public void getFirst_filledList_returnsFirst() throws Exception {
        Data expectedFirstData = dataBatch.getDataList().get(0);
        Data actualFirstData = dataBatch.getFirst();
        assertEquals("Wrong data returned as first data", expectedFirstData, actualFirstData);
    }

    @Test
    public void getFirst_emptyList_throwsException() throws Exception {
        dataBatch.getDataList().clear();
        try {
            dataBatch.getFirst();
            fail("No exception thrown");
        } catch (IndexOutOfBoundsException expectedException) {
        }
    }

    @Test
    public void getLast_filledList_returnsLast() throws Exception {
        int lastDataIndex = dataBatch.getDataList().size() - 1;
        Data expectedLastData = dataBatch.getDataList().get(lastDataIndex);
        Data actualLastData = dataBatch.getLast();
        assertEquals("Wrong data returned as last data", expectedLastData, actualLastData);
    }

    @Test
    public void getLast_emptyList_throwsException() throws Exception {
        dataBatch.getDataList().clear();
        try {
            dataBatch.getLast();
            fail("No exception thrown");
        } catch (IndexOutOfBoundsException expectedException) {
        }
    }

    @Test
    public void getDataFromLast_filledList_returnsMatchingData() throws Exception {
        dataBatch = createDummyDataBatch(10);
        long duration = System.currentTimeMillis() - startTimestamp;
        List<Data> dataList = dataBatch.getDataFromLast(duration - TimeUnit.SECONDS.toMillis(5) + 1);
        int expectedDataCount = 5;
        int actualDataCount = dataList.size();
        assertEquals("Unexpected number of data entries returned", expectedDataCount, actualDataCount);
        assertTrue("List doesn't contain last data entry", dataList.contains(dataBatch.getLast()));
    }

    @Test
    public void getDataSince_filledList_returnsMatchingData() throws Exception {
        dataBatch = createDummyDataBatch(10);
        long timestamp = startTimestamp + TimeUnit.SECONDS.toMillis(5) + 1;
        List<Data> dataList = dataBatch.getDataSince(timestamp);
        int expectedDataCount = 4;
        int actualDataCount = dataList.size();
        assertEquals("Unexpected number of data entries returned", expectedDataCount, actualDataCount);
        assertTrue("List doesn't contain last data entry", dataList.contains(dataBatch.getLast()));
    }

    @Test
    public void getDataBetween_filledList_returnsMatchingData() throws Exception {
        dataBatch = createDummyDataBatch(10);
        long startTimestamp = DataBatchTest.startTimestamp + TimeUnit.SECONDS.toMillis(5) + 1;
        long endTimestamp = startTimestamp + TimeUnit.SECONDS.toMillis(2);
        List<Data> dataList = dataBatch.getDataBetween(startTimestamp, endTimestamp);
        int expectedDataCount = 2;
        int actualDataCount = dataList.size();
        assertEquals("Unexpected number of data entries returned", expectedDataCount, actualDataCount);
        assertFalse("List contains last data entry", dataList.contains(dataBatch.getLast()));
    }

    @Test
    public void trim_notFullList_doesNothing() throws Exception {
        int oldItemCount = dataBatch.getDataList().size();
        dataBatch.trim();
        int newItemCount = dataBatch.getDataList().size();
        assertEquals("List size changed", oldItemCount, newItemCount);
    }

    @Test
    public void trim_fullList_containsNewData() throws Exception {
        dataBatch.setCapacity(dataBatch.getDataList().size());
        Data oldData = dataBatch.getFirst();
        Data newData = mock(Data.class);
        dataBatch.getDataList().add(newData);
        dataBatch.trim();
        assertEquals("List size doesn't match batch capacity", dataBatch.getCapacity(), dataBatch.getDataList().size());
        assertTrue("Trim removed new data", dataBatch.getDataList().contains(newData));
        assertTrue("Trim didn't remove old data", !dataBatch.getDataList().contains(oldData));
    }

    @Test
    public void trim_fullList_removesOldData() throws Exception {
        dataBatch.setCapacity(dataBatch.getDataList().size() - 2);
        List<Data> dataCopy = new ArrayList<>(dataBatch.getDataList());
        dataBatch.trim();
        assertEquals("List size doesn't match batch capacity", dataBatch.getCapacity(), dataBatch.getDataList().size());
        assertTrue("List does not contain all valid data", dataBatch.getDataList().containsAll(dataCopy.subList(dataCopy.size() - dataBatch.getCapacity(), dataCopy.size())));
        for (int i = 0; i < dataCopy.size() - dataBatch.getCapacity(); i++) {
            assertTrue("Trim didn't remove old data", !dataBatch.getDataList().contains(dataCopy.get(i)));
        }
    }

}