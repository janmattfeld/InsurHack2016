package com.zurich.authenticator.data.batch;

import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.merger.DataMerger;
import com.zurich.authenticator.data.merger.FeatureMerger;
import com.zurich.authenticator.data.merger.MergeException;
import com.zurich.authenticator.util.TimeUtils;
import com.zurich.authenticator.util.markdown.table.Table;
import com.zurich.authenticator.util.markdown.table.TableRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinnedDataBatch<T extends Data> extends DataBatch<T> {

    protected int binCount;
    protected long binSize;
    protected long minimumTimestamp;
    protected long maximumTimestamp;

    protected List<List<T>> binnedDataList;

    /**
     * Bins {@link Data} entries in a window ranging from the current timestamp
     * back to (binCount * binSize) milliseconds.
     *
     * @param binCount the number of bins
     * @param binSize  the range of one bin in milliseconds
     * @param dataList the list of {@link Data} to be binned
     */
    public BinnedDataBatch(int binCount, long binSize, List<T> dataList) {
        this.binCount = binCount;
        this.binSize = binSize;
        this.dataList = dataList;

        maximumTimestamp = System.currentTimeMillis();
        minimumTimestamp = maximumTimestamp - (binCount * binSize);
    }

    /**
     * Bins {@link Data} entries in a window ranging from the timestamp
     * of the newest data entry back to the timestamp of the oldest
     * data entry. Automatically sets the binSize accordingly.
     *
     * @param binCount the number of bins
     * @param dataList the list of {@link Data} to be binned
     */
    public BinnedDataBatch(int binCount, List<T> dataList) {
        this.binCount = binCount;
        this.dataList = dataList;

        minimumTimestamp = getFirstTimestamp();
        maximumTimestamp = getLastTimestamp();
        binSize = (int) Math.floor((maximumTimestamp - minimumTimestamp) / binCount);
    }

    /**
     * Performs the actual binning, if not done already
     *
     * @return the binned {@link Data}
     */
    public List<List<T>> getBinnedDataList() {
        if (binnedDataList == null) {
            // create a list with empty bins
            binnedDataList = new ArrayList<>(binCount);
            for (int binIndex = 0; binIndex < binCount; binIndex++) {
                binnedDataList.add(new ArrayList<T>());
            }

            // put data into bins
            for (T data : dataList) {
                try {
                    int binIndex = getBinIndex(data);
                    binnedDataList.get(binIndex).add(data);
                } catch (IndexOutOfBoundsException ex) {
                    // data shouldn't be added to any bin
                }
            }
        }
        return binnedDataList;
    }

    /**
     * Checks if the bin at the the specified index contains
     * any {@link Data} entries
     *
     * @param index the bin index
     * @return true if the the bin has one or more {@link Data} entries
     * @throws IndexOutOfBoundsException
     */
    public boolean hasDataInBin(int index) throws IndexOutOfBoundsException {
        return getDataInBin(index).size() > 0;
    }

    /**
     * Returns a list of {@link Data} entries that belong to the bin
     * at the specified index
     *
     * @param index the bin index
     * @return List of {@link Data} entries
     * @throws IndexOutOfBoundsException
     */
    public List<T> getDataInBin(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= binCount) {
            throw new IndexOutOfBoundsException("Invalid bin index: " + index);
        }
        return getBinnedDataList().get(index);
    }

    /**
     * Returns a {@link Data} entry that represents the entries belonging to the bin
     * at the specified index
     *
     * @param index the bin index
     * @return {@link Data} entry
     * @throws IndexOutOfBoundsException if the bin doesn't exist
     * @throws NullPointerException      if the bin is empty
     */
    public T getRepresentativeDataInBin(int index) throws IndexOutOfBoundsException, NullPointerException, MergeException {
        List<T> dataInBin = getDataInBin(index);
        if (dataInBin.isEmpty()) {
            throw new NullPointerException("Bin is empty");
        }
        // TODO: avoid casting!
        FeatureMerger.merge((List<FeatureData>) dataInBin, DataMerger.MEAN);
        return dataInBin.get(0);
    }

    protected long getFirstTimestamp() throws IndexOutOfBoundsException {
        return getFirst().getTimestamp();
    }

    protected long getLastTimestamp() throws IndexOutOfBoundsException {
        return getLast().getTimestamp();
    }

    protected int getBinIndex(T data) throws IndexOutOfBoundsException {
        return getBinIndex(data.getTimestamp());
    }

    protected int getBinIndex(long timestamp) throws IndexOutOfBoundsException {
        if (timestamp < minimumTimestamp || timestamp > maximumTimestamp) {
            throw new IndexOutOfBoundsException("Data timestamp is not in range");
        }
        return (int) Math.floor((timestamp - minimumTimestamp - 1) / binSize);
    }

    @Override
    public String toOverviewString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName())
                .append(" with ").append(binCount).append(" bins")
                .append(" à ").append(TimeUtils.getReadableDuration(binSize))
                .append(", ranging from ").append(minimumTimestamp)
                .append(" to ").append(maximumTimestamp);
        return sb.toString();
    }

    @Override
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder()
                .append(toOverviewString())
                .append("\n")
                .append(toTable().trim(25));
        return sb.toString();
    }

    @Override
    public Table toTable() {
        getBinnedDataList(); // invoke binning in case it hasn't been done yet

        Table table = new Table();
        List<Integer> alignments = Arrays.asList(Table.ALIGN_RIGHT, Table.ALIGN_LEFT);
        table.setAlignments(alignments);

        List<String> headers = Arrays.asList("Index", "Binned Data");
        TableRow headerRow = new TableRow(new ArrayList<Object>(headers));
        table.getRows().add(headerRow);

        for (int index = 0; index < binnedDataList.size(); index++) {
            TableRow tableRow = new TableRow();
            tableRow.getItems().add(String.valueOf(index));
            StringBuilder entries = new StringBuilder()
                    .append(binnedDataList.get(index).size())
                    .append(" entries");
            if (binnedDataList.get(index).size() > 0) {
                try {
                    entries.append(", like ").append(getRepresentativeDataInBin(index).toString());
                } catch (MergeException e) {
                    entries.append(", like ").append(getDataInBin(index).toString());
                }
            }
            tableRow.getItems().add(entries.toString());
            table.getRows().add(tableRow);
        }
        return table;
    }

    @Override
    public void setDataList(List<T> dataList) {
        super.setDataList(dataList);
        binnedDataList = null;
    }

    public int getBinCount() {
        return binCount;
    }

    public long getBinSize() {
        return binSize;
    }

    public long getMinimumTimestamp() {
        return minimumTimestamp;
    }

    public long getMaximumTimestamp() {
        return maximumTimestamp;
    }
}
