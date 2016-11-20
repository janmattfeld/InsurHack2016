package com.zurich.authenticator.data.persister.database;

import android.database.Cursor;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataBaseQueryBuilder {

    public static final String TAG = DataBaseQueryBuilder.class.getSimpleName();

    public static final int NOT_SET = -1;

    private DataBasePersister dataBasePersister;

    private List<String> tableNames = new ArrayList<>();
    private List<Integer> subTypes = new ArrayList<>();

    private boolean distinct = false;
    private int limit = NOT_SET;
    private String order = SqlDataBaseHelper.SQL_ORDER_ASC;
    private long startTimestamp = NOT_SET;
    private long endTimestamp = NOT_SET;
    private long duration = NOT_SET;

    public DataBaseQueryBuilder(DataBasePersister dataBasePersister) {
        this.dataBasePersister = dataBasePersister;
    }

    public DataBaseQueryBuilder forTable(String tableName) {
        tableNames.add(tableName);
        return this;
    }

    public DataBaseQueryBuilder forTables(List<String> tableNames) {
        this.tableNames.addAll(tableNames);
        return this;
    }

    public DataBaseQueryBuilder forData() {
        tableNames.addAll(SqlDataBaseHelper.getPossibleTableNames());
        return this;
    }

    public DataBaseQueryBuilder forSensorEvents() {
        return forType(Data.TYPE_SENSOR_EVENT);
    }

    public DataBaseQueryBuilder forStates() {
        return forType(Data.TYPE_STATE);
    }

    public DataBaseQueryBuilder forFeatures() {
        return forType(Data.TYPE_FEATURE);
    }

    public DataBaseQueryBuilder forClassifications() {
        return forType(Data.TYPE_CLASSIFICATION);
    }

    public DataBaseQueryBuilder forTrustLevels() {
        return forType(Data.TYPE_TRUST_LEVEL);
    }

    public DataBaseQueryBuilder forTypes(List<Integer> tableTypes) {
        for (int tableType : tableTypes) {
            forType(tableType);
        }
        return this;
    }

    public DataBaseQueryBuilder forType(int dataType) {
        tableNames.addAll(SqlDataBaseHelper.getTableNamesForType(dataType));
        return this;
    }

    public DataBaseQueryBuilder withTableTag(String tableTag) {
        List<String> tableTags = new ArrayList<>();
        tableTags.add(tableTag);
        withTableTags(tableTags);
        return this;
    }

    public DataBaseQueryBuilder withTableTags(List<String> tableTags) {

        List<String> tableNamesCopy = new ArrayList<>(tableNames);
        for (String tableName : tableNamesCopy) {
            boolean containsTag = false;
            for (String tableTag : tableTags) {
                if (!SqlDataBaseHelper.getAcceptedTags().contains(tableTag)) {
                    Logger.w(TAG, "Table tag " + tableTag + " is not an accepted tag");
                    continue;
                }
                if (tableName.contains(tableTag)) {
                    containsTag = true;
                    break;
                }
            }
            if (!containsTag) {
                tableNames.remove(tableName);
            }
        }
        return this;
    }

    public DataBaseQueryBuilder withSubType(int subType) {
        this.subTypes.add(subType);
        return this;
    }

    public DataBaseQueryBuilder withSubTypes(List<Integer> subTypes) {
        this.subTypes.addAll(subTypes);
        return this;
    }

    public DataBaseQueryBuilder getDataSince(long startTimestamp) {
        this.startTimestamp = startTimestamp;
        return this;
    }

    public DataBaseQueryBuilder getDataBefore(long endTimestamp) {
        this.endTimestamp = endTimestamp;
        return this;
    }

    public DataBaseQueryBuilder getDataBetween(long startTimestamp, long endTimestamp) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        return this;
    }

    public DataBaseQueryBuilder withDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public DataBaseQueryBuilder getDataFromLast(long duration) {
        startTimestamp = Math.max(System.currentTimeMillis() - duration, 0);
        return this;
    }

    public DataBaseQueryBuilder withDistinctData() {
        // TODO implement select column from table
        distinct = true;
        return this;
    }

    public DataBaseQueryBuilder getFirst() {
        return getFirst(1);
    }

    public DataBaseQueryBuilder getLast() {
        return getLast(1);
    }

    public DataBaseQueryBuilder getFirst(int limit) {
        this.limit = limit;
        order = SqlDataBaseHelper.SQL_ORDER_ASC;
        return this;
    }

    public DataBaseQueryBuilder getLast(int limit) {
        this.limit = limit;
        order = SqlDataBaseHelper.SQL_ORDER_DESC;
        return this;
    }

    public List<Data> asDataList() throws DataPersistingException {
        setupConfiguration();
        validateConfiguration();
        return dataBasePersister.queryDataList(this);
    }

    public List<DataBatch> asDataBatches() throws DataPersistingException {
        setupConfiguration();
        validateConfiguration();
        return dataBasePersister.queryDataBatches(this);
    }

    public DataBatch asDataBatch() throws DataPersistingException {
        if (subTypes.size() > 1) {
            throw new DataPersistingException("DataList can only have up to one subType");
        }
        setupConfiguration();
        validateConfiguration();
        return dataBasePersister.queryDataBatch(this);
    }

    // for testing
    public DataBaseQueryBuilder asQueryBuilder() throws DataPersistingException {
        setupConfiguration();
        validateConfiguration();
        return this;
    }

    // for testing
    public Map<String, Cursor> asCursorMap() throws DataPersistingException {
        setupConfiguration();
        validateConfiguration();
        return dataBasePersister.getCursors(this);
    }

    public void setupConfiguration() {
        setTimestamps();
        String msg = "Table doesn't exist";
        removeUnknownElementsFromList(SqlDataBaseHelper.getPossibleTableNames(), tableNames, msg);
    }

    public void validateConfiguration() throws DataPersistingException {
        if (tableNames.isEmpty()) {
            throw new DataPersistingException("No tables were specified");
        }
        validateTimestamps();

        if (limit != NOT_SET) {
            if (limit <= 0) {
                throw new DataPersistingException("Limit must be greater 0.");
            }
        }
    }

    public void setTimestamps() {
        if (duration != NOT_SET) {
            if (startTimestamp != NOT_SET) {
                if (endTimestamp == NOT_SET) {
                    endTimestamp = startTimestamp + duration;
                }
            } else if (endTimestamp != NOT_SET) {
                startTimestamp = Math.max(endTimestamp - duration, 0);
            } else {
                startTimestamp = Math.max(System.currentTimeMillis() - duration, 0);
            }
        }
    }

    public void validateTimestamps() throws DataPersistingException {
        if ((startTimestamp < 0) && (startTimestamp != NOT_SET)
                || (endTimestamp < 0) && (endTimestamp != NOT_SET)
                || (duration < 0) && (duration != NOT_SET)) {
            throw new DataPersistingException("Timestamps must be positive");
        }
        if (startTimestamp != NOT_SET) {
            if (endTimestamp != NOT_SET) {
                if (endTimestamp < startTimestamp) {
                    throw new DataPersistingException("EndTimestamp must be greater or equal than startTimestamp");
                }
                if (duration != NOT_SET) {
                    if (endTimestamp - startTimestamp != duration) {
                        throw new DataPersistingException("Duration did not match interval between startTimestamp and endTimestamp");
                    }
                }
            }
        }
    }

    public static void removeUnknownElementsFromList(List<String> availableElements,
                                                     List<String> actualElements, String warningMessage) {
        List<String> notAvailableElements = new ArrayList<>();
        for (String actualElement : actualElements) {
            if (!availableElements.contains(actualElement)) {
                Logger.w(TAG, warningMessage + " : " + actualElement);
                notAvailableElements.add(actualElement);
            }
        }
        actualElements.removeAll(notAvailableElements);
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public String getLimit() {
        if (limit == NOT_SET) {
            return null;
        }
        return String.valueOf(limit);
    }

    public List<Integer> getSubTypes() {
        return subTypes;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public String getOrder() {
        return order;
    }

}
