package com.zurich.authenticator.data.batch;

import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.generic.Data;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BinnedDataBatchTest {

    private BinnedDataBatch<Data> binnedDataBatch;

    @Before
    public void setUp() throws Exception {
        binnedDataBatch = createDummyBinnedDataBatch(50);
    }

    public static List<Data> createDummyDataList(int itemCount) {
        long newestTimestamp = System.currentTimeMillis() - 1;
        long timestampDelta = TimeUnit.SECONDS.toMillis(1);

        List<Data> dataList = new ArrayList<>();
        for (int count = itemCount - 1; count >= 0; count--) {
            Data data = new FeatureData();
            data.setTimestamp(newestTimestamp - (count * timestampDelta));
            dataList.add(data);
        }
        return dataList;
    }

    public static BinnedDataBatch<Data> createDummyBinnedDataBatch(int itemCount) {
        List<Data> dataList = createDummyDataList(itemCount);
        return createDummyBinnedDataBatch(dataList);
    }

    public static BinnedDataBatch<Data> createDummyBinnedDataBatch(List<Data> dataList) {
        int binCount = 5;
        long binSize = TimeUnit.SECONDS.toMillis(5);
        BinnedDataBatch<Data> dataBatch = new BinnedDataBatch<>(binCount, binSize, dataList);
        return dataBatch;
    }

    @Test
    public void getBinnedDataList_lazyInitialization_correctState() throws Exception {
        assertTrue("Binning has been performed before needed", binnedDataBatch.binnedDataList == null);
        assertTrue("Lazy initialization hasn't performed binning", binnedDataBatch.getBinnedDataList() != null);

        // set a new data list
        binnedDataBatch.setDataList(new ArrayList<Data>());
        assertTrue("Binned data has not been invalidated", binnedDataBatch.binnedDataList == null);
    }

    @Test
    public void getBinnedDataList_filledList_returnsMatchingBins() throws Exception {
        List<List<Data>> binnedDataList = binnedDataBatch.getBinnedDataList();
        assertEquals("Bin count doesn't match binned data list size", binnedDataBatch.getBinCount(), binnedDataList.size());

        int expectedEntriesPerBin = 5;
        for (List<Data> dataBin : binnedDataList) {
            assertEquals("Invalid number of data entries in bin", expectedEntriesPerBin, dataBin.size());
        }

        Data firstBinnedDataEntry = binnedDataBatch.getDataSince(binnedDataBatch.getMinimumTimestamp()).get(0);
        Data lastBinnedDataEntry = binnedDataBatch.getLast();
        assertTrue("First data entry is not in first bin", binnedDataList.get(0).contains(firstBinnedDataEntry));
        assertTrue("Last data entry is not in last bin", binnedDataList.get(binnedDataList.size() - 1).contains(lastBinnedDataEntry));
    }

    @Test
    public void getBinnedDataList_emptyList_returnsMatchingBins() throws Exception {
        binnedDataBatch.setDataList(new ArrayList<Data>());
        List<List<Data>> binnedDataList = binnedDataBatch.getBinnedDataList();
        assertEquals("Bin count doesn't match binned data list size", binnedDataBatch.getBinCount(), binnedDataList.size());

        int expectedEntriesPerBin = 0;
        for (List<Data> dataBin : binnedDataList) {
            assertEquals("Invalid number of data entries in bin", expectedEntriesPerBin, dataBin.size());
        }
    }

    @Test
    public void hasDataInBin_filledBin_returnsTrue() throws Exception {
        for (int binIndex = 0; binIndex < binnedDataBatch.getBinnedDataList().size(); binIndex++) {
            assertTrue("Non empty bin should return true", binnedDataBatch.hasDataInBin(binIndex));
        }
    }

    @Test
    public void hasDataInBin_emptyBin_returnsFalse() throws Exception {
        binnedDataBatch.setDataList(new ArrayList<Data>());
        for (int binIndex = 0; binIndex < binnedDataBatch.getBinnedDataList().size(); binIndex++) {
            assertTrue("Empty bin should return false", !binnedDataBatch.hasDataInBin(binIndex));
        }
    }

    @Test
    public void getDataInBin_validBinIndex_returnsMatchingData() throws Exception {
        binnedDataBatch.getBinnedDataList();
        Data firstBinnedDataEntry = binnedDataBatch.getDataSince(binnedDataBatch.getMinimumTimestamp()).get(0);
        Data lastBinnedDataEntry = binnedDataBatch.getLast();
        assertTrue("First data entry is not in first bin", binnedDataBatch.getDataInBin(0).contains(firstBinnedDataEntry));
        assertTrue("Last data entry is not in last bin", binnedDataBatch.getDataInBin(binnedDataBatch.binCount - 1).contains(lastBinnedDataEntry));
    }

    @Test
    public void getDataInBin_invalidBinIndex_throwsException() throws Exception {
        try {
            binnedDataBatch.getDataInBin(binnedDataBatch.getBinCount());
            fail("No exception thrown");
        } catch (IndexOutOfBoundsException expectedException) {
        }
    }

    @Test
    public void getFirstFirstTimestamp_filledList_returnsEarliestTimestamp() throws Exception {
        long expected = binnedDataBatch.getDataList().get(0).getTimestamp();
        long actual = binnedDataBatch.getFirstTimestamp();
        assertEquals("First timestamp doesn't match", expected, actual);
    }

    @Test
    public void getLastTimestamp_filledList_returnsLatestTimestamp() throws Exception {
        long expected = binnedDataBatch.getDataList().get(binnedDataBatch.getDataList().size() - 1).getTimestamp();
        long actual = binnedDataBatch.getLastTimestamp();
        assertEquals("Last timestamp doesn't match", expected, actual);
    }

    @Test
    public void getBinIndex_validTimestamp_returnsMatchingBinIndex() throws Exception {
        long timestampDelta = binnedDataBatch.getMaximumTimestamp() - binnedDataBatch.getMinimumTimestamp();
        long meanTimestamp = binnedDataBatch.getMinimumTimestamp() + (timestampDelta / 2);
        int expected = binnedDataBatch.getBinCount() / 2;
        int actual = binnedDataBatch.getBinIndex(meanTimestamp);
        assertEquals("Wrong bin index assigned mean timestamp", expected, actual);

        expected = binnedDataBatch.getBinCount() - 1;
        actual = binnedDataBatch.getBinIndex(binnedDataBatch.getMaximumTimestamp());
        assertEquals("Wrong bin index assigned maximum timestamp", expected, actual);

        expected = 0;
        actual = binnedDataBatch.getBinIndex(binnedDataBatch.getMinimumTimestamp());
        assertEquals("Wrong bin index assigned maximum timestamp", expected, actual);
    }

    @Test
    public void getBinIndex_invalidTimestamp_throwsException() throws Exception {
        try {
            binnedDataBatch.getBinIndex(0);
            fail("No exception thrown for < minimum");
        } catch (IndexOutOfBoundsException expectedException) {
        }
        try {
            binnedDataBatch.getBinIndex(System.currentTimeMillis() + 1);
            fail("No exception thrown for > maximum");
        } catch (IndexOutOfBoundsException expectedException) {
        }
    }

}