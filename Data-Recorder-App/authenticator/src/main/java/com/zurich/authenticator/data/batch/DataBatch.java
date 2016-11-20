package com.zurich.authenticator.data.batch;

import com.google.gson.annotations.Expose;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.state.StateData;
import com.zurich.authenticator.util.logging.Logger;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.MarkdownSerializable;
import com.zurich.authenticator.util.markdown.table.Table;
import com.zurich.authenticator.util.markdown.table.TableRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBatch<T extends Data> implements MarkdownSerializable {

    public static final int CAPACITY_UNLIMITED = -1;
    public static final int CAPACITY_DEFAULT = 100;

    @Expose
    protected List<T> dataList = new ArrayList<>();
    protected int capacity = CAPACITY_DEFAULT;
    protected boolean autoTrim = true;
    @Expose
    protected int type = Data.TYPE_NOT_SET;
    @Expose
    protected int subType = Data.TYPE_NOT_SET;

    public DataBatch() {
    }

    public DataBatch(int capacity) {
        this.capacity = capacity;
    }

    public DataBatch(List<T> dataList) {
        this.dataList = dataList;
    }

    public void add(T data) throws DataPersistingException {
        add(data, autoTrim);
    }

    public void add(T data, boolean trim) throws DataPersistingException {
        checkType(data);
        if (!dataList.contains(data)) {
            dataList.add(data);
            if (trim) {
                trim();
            }
        }
    }

    public void add(List<T> dataList) {
        add(dataList, autoTrim);
    }

    public void add(List<T> dataList, boolean trim) {
        try {
            checkType(dataList);
            this.dataList.addAll(dataList);
            if (trim) {
                trim();
            }
        } catch (DataPersistingException ex) {
            Logger.w(this.toString(), "Data has not been added: " + ex.getMessage());
        }
    }

    public void remove(T data) {
        if (dataList.contains(data)) {
            dataList.remove(data);
        }
    }

    public T getFirst() throws IndexOutOfBoundsException {
        if (dataList.size() < 1) {
            throw new IndexOutOfBoundsException("Batch is empty");
        }
        return dataList.get(0);
    }

    public T getLast() throws IndexOutOfBoundsException {
        if (dataList.size() < 1) {
            throw new IndexOutOfBoundsException("Batch is empty");
        }
        return dataList.get(dataList.size() - 1);
    }

    public List<T> getDataFromLast(long milliseconds) {
        return getDataSince(System.currentTimeMillis() - milliseconds);
    }

    public List<T> getDataSince(long timestamp) {
        List<T> dataSince = new ArrayList<>(dataList);
        // iterate over data in reverse order
        for (int i = dataSince.size() - 1; i >= 0; i--) {
            long dataTimestamp = dataSince.get(i).getTimestamp();
            if (dataTimestamp < timestamp) {
                // remove all older data entries
                dataSince.subList(0, i + 1).clear();
                break;
            }
        }
        return dataSince;
    }

    public List<T> getDataBetween(long startTimestamp, long endTimestamp) {
        List<T> dataBetween = new ArrayList<>(dataList);
        // remove data before start timestamp
        for (int i = dataBetween.size() - 1; i >= 0; i--) {
            long dataTimestamp = dataBetween.get(i).getTimestamp();
            if (dataTimestamp < startTimestamp) {
                // remove all older data entries
                dataBetween.subList(0, i + 1).clear();
                break;
            }
        }

        // remove data after end timestamp
        for (int i = 0; i < dataBetween.size(); i++) {
            long dataTimestamp = dataBetween.get(i).getTimestamp();
            if (dataTimestamp > endTimestamp) {
                // remove all older data entries
                dataBetween.subList(i, dataBetween.size()).clear();
                break;
            }
        }

        return dataBetween;
    }

    public void trim() {
        dataList = getTrimmedDataList(dataList, capacity);
    }

    public List<T> getTrimmedDataList(List<T> dataList, int capacity) {
        if (capacity == CAPACITY_UNLIMITED) {
            return dataList;
        }
        List<T> trimmedList = new ArrayList<>(dataList);
        int overflowCount = dataList.size() - capacity;
        if (overflowCount > 0) {
            trimmedList.subList(0, overflowCount).clear();
        }
        return trimmedList;
    }

    /**
     * Iterates over all {@link Data} entries and sets the
     * sub type.
     */
    public void applySubType() {
        switch (type) {
            case Data.TYPE_SENSOR_EVENT:
                for (T data : dataList) {
                    ((SensorEventData) data).setSensorType(subType);
                }
                break;
            case Data.TYPE_STATE:
                for (T data : dataList) {
                    ((StateData) data).setStateType(subType);
                }
                break;
            case Data.TYPE_FEATURE:
                for (T data : dataList) {
                    ((FeatureData) data).setFeatureType(subType);
                }
                break;
            case Data.TYPE_CLASSIFICATION:
                for (T data : dataList) {
                    ((ClassificationData) data).setClassificationType(subType);
                }
                break;
        }
    }

    public void checkType(T data) throws DataPersistingException {
        int dataType = data.getType();
        if (dataType == Data.TYPE_NOT_SET) {
            // mocked data has no type
            // throw new DataPersistingException("No data type found");
        }
        if (type == Data.TYPE_NOT_SET) {
            type = dataType;
        } else if (type != dataType) {
            throw new DataPersistingException("The type " + dataType + " does not match with the dataBatch type " + type + ".");
        }
        checkSubType(data);
    }

    public void checkSubType(T data) throws DataPersistingException {
        int dataSubType = Data.TYPE_NOT_SET;
        switch (type) {
            case Data.TYPE_SENSOR_EVENT:
                dataSubType = ((SensorEventData) data).getSensorType();
                break;
            case Data.TYPE_STATE:
                dataSubType = ((StateData) data).getStateType();
                break;
            case Data.TYPE_FEATURE:
                dataSubType = ((FeatureData) data).getFeatureType();
                break;
            case Data.TYPE_CLASSIFICATION:
                dataSubType = ((ClassificationData) data).getClassificationType();
                break;
            case Data.TYPE_TRUST_LEVEL:
                return;
            case Data.TYPE_NOT_SET:
            case 0:
                // mocked data has no type
                return;
            default:
                throw new DataPersistingException("Unknown Type");
        }
        if (subType == Data.TYPE_NOT_SET) {
            subType = dataSubType;
        } else if (subType != dataSubType) {
            throw new DataPersistingException("The subType " + dataSubType + " does not match with the dataBatch subType " + subType + ".");
        }
    }

    public void checkType(List<T> dataList) throws DataPersistingException {
        if (!dataList.isEmpty()) {
            // check one representative as to check all would be too expensive; no guarantee that all are correct
            checkType(dataList.get(0));
        }
    }

    @Override
    public String toString() {
        return toOverviewString();
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        return toTable().trim(10);
    }

    public String toOverviewString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName())
                .append(" with ").append(dataList.size())
                .append(" of ").append(capacity)
                .append(" items");

        if (dataList.size() > 0) {
            sb.append(", similar to: ").append(getLast());
        }

        return sb.toString();
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder()
                .append(toOverviewString())
                .append("\n")
                .append(toTable().trim(10));
        return sb.toString();
    }

    public Table toTable() {
        Table table = new Table();
        List<Integer> alignments = Arrays.asList(Table.ALIGN_RIGHT, Table.ALIGN_LEFT);
        table.setAlignments(alignments);

        List<String> headers = Arrays.asList("Index", "Data");
        TableRow headerRow = new TableRow(new ArrayList<Object>(headers));
        table.getRows().add(headerRow);

        for (int index = 0; index < dataList.size(); index++) {
            TableRow tableRow = new TableRow();
            tableRow.getItems().add(String.valueOf(index));
            tableRow.getItems().add(dataList.get(index).toString());
            table.getRows().add(tableRow);
        }
        return table;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        try {
            checkType(dataList);
        } catch (DataPersistingException ex) {
            Logger.w(this.toString(), "Data has not been set: " + ex.getMessage());
            return;
        }
        this.dataList = dataList;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean getAutoTrim() {
        return autoTrim;
    }

    public void setAutoTrim(boolean autoTrim) {
        this.autoTrim = autoTrim;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }
}
