package com.zurich.authenticator.data.persister.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersister;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.state.StateData;
import com.zurich.authenticator.data.trustlevel.TrustLevelData;
import com.zurich.authenticator.util.StringUtils;
import com.zurich.authenticator.util.logging.Logger;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.text.NormalText;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataBasePersister extends DataPersister {

    private static final String TAG = DataBasePersister.class.getSimpleName();

    private SQLiteDatabase sqlLiteDataBase;
    private final String tableTag;

    public DataBasePersister(Context context) {
        this(context, SqlDataBaseHelper.TAG_DEFAULT);
    }

    public DataBasePersister(Context context, String tableTag) {
        Logger.d(TAG, "DataBasePersister() called with: context = [" + context + "], tableTag = [" + tableTag + "]");
        if (!SqlDataBaseHelper.getAcceptedTags().contains(tableTag)) {
            tableTag = SqlDataBaseHelper.TAG_DEFAULT;
        }
        this.tableTag = tableTag;

        SqlDataBaseHelper sqlDataBaseHelper = new SqlDataBaseHelper(context);
        try {
            sqlLiteDataBase = sqlDataBaseHelper.getWritableDatabase();
        } catch (RuntimeException e) {
            Logger.w(TAG, "Could not get writable database");
            e.printStackTrace();
            // will be thrown in tests because it's not mocked
        }

        if (tableTag.equals(SqlDataBaseHelper.TAG_RECORD)) {
            dropAndCreateNewTable(Data.TYPE_SENSOR_EVENT);
        }
    }

    @Override
    public boolean canPersist(Data data) {
        if (sqlLiteDataBase == null) {
            return false;
        }
        switch (data.getType()) {
            case Data.TYPE_SENSOR_EVENT:
            case Data.TYPE_STATE:
            case Data.TYPE_FEATURE:
            case Data.TYPE_CLASSIFICATION:
            case Data.TYPE_TRUST_LEVEL:
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void persist(Data data) throws DataPersistingException {
        switch (data.getType()) {
            case Data.TYPE_SENSOR_EVENT:
                persist((SensorEventData) data);
                break;
            case Data.TYPE_STATE:
                persist((StateData) data);
                break;
            case Data.TYPE_FEATURE:
                persist((FeatureData) data);
                break;
            case Data.TYPE_CLASSIFICATION:
                persist((ClassificationData) data);
                break;
            case Data.TYPE_TRUST_LEVEL:
                persist((TrustLevelData) data);
                break;
            default: {
                throw new DataPersistingException("Data with dataType " + data.getType() + " can not be persisted");
            }
        }
    }

    @Override
    public void persist(List<Data> dataList) throws DataPersistingException {
        //TODO: optimize to improve performance
        for (Data data : dataList) {
            persist(data);
        }
    }

    @Override
    public void clearPersistedData() {
        // TODO: clear persisted data
    }

    public void insert(String tableName, ContentValues contentValues) throws DataPersistingException {
        if (sqlLiteDataBase != null) {
            long insertId = sqlLiteDataBase.insert(tableName, null, contentValues);
            if (insertId == SqlDataBaseHelper.SQL_ERROR_VALUE) {
                throw new DataPersistingException("Could not insert content values into table " + tableName);
            }
        } else {
            throw new DataPersistingException("Database is null");
        }
    }

    public boolean dropTable(String tableName) {
        Logger.d(TAG, "dropTable() called with: tableName = [" + tableName + "]");
        try {
            if (!getAvailableTableNames().contains(tableName)) {
                throw new DataPersistingException("Table doesn't exist");
            }
            sqlLiteDataBase.execSQL(SqlDataBaseHelper.SQL_DROP_TABLE + " " + tableName);
            return true;
        } catch (Exception ex) {
            Logger.w(TAG, "Could not drop table " + tableName + ": " + ex.getMessage());
            return false;
        }
    }

    public void createNewTable(String sqlSetupCommand) {
        try {
            Logger.d(TAG, "createNewTable() called with: sqlSetupCommand = [" + sqlSetupCommand + "]");
            SqlDataBaseHelper.setupTableForTag(sqlLiteDataBase, sqlSetupCommand);
        } catch (NullPointerException ex) {
            Logger.w(TAG, ex.getMessage());
        }
    }

    public void dropAndCreateNewTable(int type) {
        String tableName = SqlDataBaseHelper.getTableName(type, tableTag);
        boolean successfulDeleted = dropTable(tableName);
        if (successfulDeleted) {
            createNewTable(SqlDataBaseHelper.getCreateTableSqlCommand(tableTag, type));
        }
    }

    public boolean deleteDatabase() {
        /* TODO
        File file = new File(sqlLiteDataBase.getPath());
        return SQLiteDatabase.deleteDatabase(file);
        */
        return false;
    }

    // Query

    public List<Data> queryDataList(DataBaseQueryBuilder dataBaseQueryBuilder) throws DataPersistingException {
        List<Data> resultList = new ArrayList<>();
        try {
            Map<String, Cursor> cursorsMap = getCursors(dataBaseQueryBuilder);
            Set<String> keys = cursorsMap.keySet();
            for (String key : keys) {
                Cursor cursor = cursorsMap.get(key);
                if (!(cursor.getCount() == 0)) {
                    int dataType = SqlDataBaseHelper.getDataType(key);
                    List<Data> dataList = getDataListFromCursor(cursor, dataType, dataBaseQueryBuilder.getOrder());
                    resultList.addAll(dataList);
                }
            }
        } catch (SQLiteException ex) {
            throw new DataPersistingException("Query failed: " + ex.getMessage());
        }
        return resultList;
    }

    public DataBatch<Data> queryDataBatch(DataBaseQueryBuilder dataBaseQueryBuilder) throws DataPersistingException {
        return new DataBatch<>(queryDataList(dataBaseQueryBuilder));
    }

    public List<DataBatch> queryDataBatches(DataBaseQueryBuilder dataBaseQueryBuilder) throws DataPersistingException {
        List<DataBatch> dataBatchList = new ArrayList<>();
        int numberOfKnownDataBatches = 0;
        Map<Map.Entry<Integer, Integer>, Integer> dataBatchPositionMap = new HashMap<>();
        List<Data> dataList = queryDataList(dataBaseQueryBuilder);
        for (Data data : dataList) {
            int dataType = data.getType();
            int subType;
            switch (dataType) {
                case Data.TYPE_SENSOR_EVENT:
                    subType = ((SensorEventData) data).getSensorType();
                    break;
                case Data.TYPE_STATE:
                    subType = ((StateData) data).getStateType();
                    break;
                case Data.TYPE_FEATURE:
                    subType = ((FeatureData) data).getFeatureType();
                    break;
                case Data.TYPE_CLASSIFICATION:
                    subType = ((ClassificationData) data).getClassificationType();
                    break;
                case Data.TYPE_TRUST_LEVEL:
                    subType = Data.TYPE_NOT_SET;
                    break;
                default:
                    Logger.w(TAG, "Unknown dataType: " + dataType);
                    continue;
            }
            Map.Entry<Integer, Integer> typeCombination = new AbstractMap.SimpleEntry<>(dataType, subType);
            if (!dataBatchPositionMap.containsKey(typeCombination)) {
                DataBatch dataBatch = new DataBatch();
                dataBatch.setType(dataType);
                dataBatch.setSubType(subType);
                dataBatchList.add(dataBatch);
                dataBatchPositionMap.put(typeCombination, numberOfKnownDataBatches);
                numberOfKnownDataBatches++;
            }
            int index = dataBatchPositionMap.get(typeCombination);
            dataBatchList.get(index).getDataList().add(data);
        }
        return dataBatchList;
    }

    public List<Data> getDataListFromCursor(Cursor cursor, int dataType, String order) {
        int timestampPosition = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_TIMESTAMP);
        int confidencePosition = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_CONFIDENCE);
        int valuesPosition = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_VALUES);
        int valuePosition = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_VALUE);
        int subTypePosition = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_SUB_TYPE);
        List<Data> dataList = new ArrayList<>();
        switch (dataType) {
            case Data.TYPE_SENSOR_EVENT:
                dataList = getSensorEventDataList(cursor, timestampPosition, subTypePosition, valuesPosition);
                break;
            case Data.TYPE_STATE:
                dataList = getStatDataList(cursor, timestampPosition, subTypePosition, valuePosition);
                break;
            case Data.TYPE_FEATURE:
                dataList = getFeatureDataList(cursor, timestampPosition, subTypePosition, valuesPosition);
                break;
            case Data.TYPE_CLASSIFICATION:
                dataList = getClassificationDataList(cursor, timestampPosition, subTypePosition, valuePosition);
                break;
            case Data.TYPE_TRUST_LEVEL:
                dataList = getTrustLevelDataList(cursor, timestampPosition, confidencePosition, valuePosition);
                break;
        }
        if (order.equals(SqlDataBaseHelper.SQL_ORDER_DESC)) {
            Collections.reverse(dataList);
        }
        return dataList;
    }

    public Map<String, Cursor> getCursors(DataBaseQueryBuilder dataBaseQueryBuilder) {
        Map<String, Cursor> cursors = new HashMap<>();
        Map.Entry<String, String[]> selectionConditions = SqlDataBaseHelper.getSelectionConditions(
                dataBaseQueryBuilder.getSubTypes(),
                dataBaseQueryBuilder.getStartTimestamp(),
                dataBaseQueryBuilder.getEndTimestamp());

        List<String> requestedSelectionColumns = SqlDataBaseHelper.getRequestedSelectionColumns(
                dataBaseQueryBuilder.getSubTypes().size(),
                dataBaseQueryBuilder.getStartTimestamp(),
                dataBaseQueryBuilder.getEndTimestamp());

        for (String tableName : dataBaseQueryBuilder.getTableNames()) {
            String tableTypeName = tableName.substring(tableName.indexOf("_") + 1);
            LinkedHashSet<String> tableColumns = SqlDataBaseHelper.AVAILABLE_TABLE_COLUMNS.get(tableTypeName);
            if (tableColumns != null) {
                if (tableColumns.containsAll(requestedSelectionColumns)) {
                    Cursor cursor = getCursor(
                            dataBaseQueryBuilder.isDistinct(), tableName,
                            selectionConditions,
                            dataBaseQueryBuilder.getOrder(),
                            dataBaseQueryBuilder.getLimit());
                    cursors.put(tableName, cursor);
                }
            }
        }
        return cursors;
    }

    public Cursor getCursor(boolean distinct, String tableName,
                            Map.Entry<String, String[]> selectionConditions,
                            String order, String limit) {
        String timestampOrder = SqlDataBaseHelper.COLUMN_TIMESTAMP + " " + order;
        Cursor cursor = sqlLiteDataBase.query(distinct, tableName, null,
                selectionConditions.getKey(), selectionConditions.getValue(),
                null, null, timestampOrder, limit);
        cursor.moveToFirst();
        return cursor;
    }

    public List<String> getAvailableTableNames() {
        Cursor c = sqlLiteDataBase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tableNames = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                tableNames.add(c.getString(0));
                c.moveToNext();
            }
        }
        c.close();
        return tableNames;
    }

    // type specific methods

    public void persist(SensorEventData sensorEventData) throws DataPersistingException {
        ContentValues contentValues = getContentValues(sensorEventData);
        String tableName = SqlDataBaseHelper.getTableName(Data.TYPE_SENSOR_EVENT, tableTag);
        insert(tableName, contentValues);
    }

    public void persist(StateData stateData) throws DataPersistingException {
        ContentValues contentValues = getContentValues(stateData);
        String tableName = SqlDataBaseHelper.getTableName(Data.TYPE_STATE, tableTag);
        insert(tableName, contentValues);
    }

    public void persist(FeatureData featureData) throws DataPersistingException {
        ContentValues contentValues = getContentValues(featureData);
        String tableName = SqlDataBaseHelper.getTableName(Data.TYPE_FEATURE, tableTag);
        insert(tableName, contentValues);
    }

    public void persist(ClassificationData classificationData) throws DataPersistingException {
        ContentValues contentValues = getContentValues(classificationData);
        String tableName = SqlDataBaseHelper.getTableName(Data.TYPE_CLASSIFICATION, tableTag);
        insert(tableName, contentValues);
    }

    public void persist(TrustLevelData trustLevelData) throws DataPersistingException {
        ContentValues contentValues = getContentValues(trustLevelData);
        String tableName = SqlDataBaseHelper.getTableName(Data.TYPE_TRUST_LEVEL, tableTag);
        insert(tableName, contentValues);
    }

    protected static ContentValues getContentValues(SensorEventData sensorEventData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlDataBaseHelper.COLUMN_TIMESTAMP, sensorEventData.getTimestamp());
        contentValues.put(SqlDataBaseHelper.COLUMN_SUB_TYPE, sensorEventData.getSensorType());
        contentValues.put(SqlDataBaseHelper.COLUMN_VALUES,
                StringUtils.serializeToCsv(sensorEventData.getValues()));
        return contentValues;
    }

    protected static ContentValues getContentValues(StateData stateData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlDataBaseHelper.COLUMN_TIMESTAMP, stateData.getTimestamp());
        contentValues.put(SqlDataBaseHelper.COLUMN_SUB_TYPE, stateData.getStateType());
        contentValues.put(SqlDataBaseHelper.COLUMN_VALUE, stateData.getValue());
        return contentValues;
    }

    protected static ContentValues getContentValues(FeatureData featureData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlDataBaseHelper.COLUMN_TIMESTAMP, featureData.getTimestamp());
        contentValues.put(SqlDataBaseHelper.COLUMN_SUB_TYPE, featureData.getFeatureType());
        contentValues.put(SqlDataBaseHelper.COLUMN_VALUES,
                StringUtils.serializeToCsv(featureData.getValues()));
        return contentValues;
    }

    protected static ContentValues getContentValues(ClassificationData classificationData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlDataBaseHelper.COLUMN_TIMESTAMP, classificationData.getTimestamp());
        contentValues.put(SqlDataBaseHelper.COLUMN_SUB_TYPE, classificationData.getClassificationType());
        contentValues.put(SqlDataBaseHelper.COLUMN_VALUE, classificationData.getValue());
        return contentValues;
    }

    protected static ContentValues getContentValues(TrustLevelData trustLevelData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlDataBaseHelper.COLUMN_TIMESTAMP, trustLevelData.getTimestamp());
        contentValues.put(SqlDataBaseHelper.COLUMN_CONFIDENCE, trustLevelData.getConfidence());
        contentValues.put(SqlDataBaseHelper.COLUMN_VALUE, trustLevelData.getValue());
        return contentValues;
    }

    protected static ContentValues getContentValues(int level, String tag, String message, String exceptionMessage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlDataBaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());
        contentValues.put(SqlDataBaseHelper.COLUMN_LOG_LEVEL, level);
        contentValues.put(SqlDataBaseHelper.COLUMN_LOG_TAG, tag);
        contentValues.put(SqlDataBaseHelper.COLUMN_LOG_MESSAGE, message);
        contentValues.put(SqlDataBaseHelper.COLUMN_LOG_THROWABLE, exceptionMessage);
        return contentValues;
    }

    private List getSensorEventDataList(Cursor cursor, int timestampPosition,
                                        int subTypePosition, int valuesPosition) {
        List<SensorEventData> sensorEventDataList = new ArrayList<>();
        if (cursor.getPosition() != SqlDataBaseHelper.SQL_ERROR_VALUE) {
            for (int j = 0; j < cursor.getCount(); j++) {
                sensorEventDataList.add(getSensorEventData(cursor, timestampPosition,
                        subTypePosition, valuesPosition));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return sensorEventDataList;
    }

    private SensorEventData getSensorEventData(Cursor cursor, int timestampPosition,
                                               int subTypePosition, int valuesPosition) {
        SensorEventData sensorEventData = new SensorEventData();
        sensorEventData.setTimestamp(cursor.getLong(timestampPosition));
        sensorEventData.setSensorType(cursor.getInt(subTypePosition));
        sensorEventData.setValues(StringUtils.deserializeCsvToFloats(
                cursor.getString(valuesPosition)));
        return sensorEventData;
    }

    private List getStatDataList(Cursor cursor, int timestampPosition,
                                 int subTypePosition, int valuePosition) {
        List<StateData> stateDataList = new ArrayList<>();
        if (cursor.getPosition() != SqlDataBaseHelper.SQL_ERROR_VALUE) {
            for (int j = 0; j < cursor.getCount(); j++) {
                stateDataList.add(getStatData(cursor, timestampPosition,
                        subTypePosition, valuePosition));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return stateDataList;
    }

    private StateData getStatData(Cursor cursor, int timestampPosition,
                                  int subTypePosition, int valuePosition) {
        StateData stateData = new StateData();
        stateData.setTimestamp(cursor.getLong(timestampPosition));
        stateData.setStateType(cursor.getInt(subTypePosition));
        stateData.setValue(cursor.getFloat(valuePosition));
        return stateData;
    }

    private List getFeatureDataList(Cursor cursor, int timestampPosition,
                                    int subTypePosition, int valuesPosition) {
        List<FeatureData> featureDataList = new ArrayList<>();
        if (cursor.getPosition() != SqlDataBaseHelper.SQL_ERROR_VALUE) {
            for (int j = 0; j < cursor.getCount(); j++) {
                featureDataList.add(getFeatureData(cursor, timestampPosition,
                        subTypePosition, valuesPosition));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return featureDataList;
    }

    private FeatureData getFeatureData(Cursor cursor, int timestampPosition,
                                       int subTypePosition, int valuesPosition) {
        FeatureData featureData = new FeatureData();
        featureData.setTimestamp(cursor.getLong(timestampPosition));
        featureData.setFeatureType(cursor.getInt(subTypePosition));
        featureData.setValues(StringUtils.deserializeCsvToFloats(cursor.getString(valuesPosition)));
        return featureData;
    }

    private List getClassificationDataList(Cursor cursor, int timestampPosition,
                                           int subTypePosition, int valuePosition) {
        List<ClassificationData> classificationDataList = new ArrayList<>();
        if (cursor.getPosition() != SqlDataBaseHelper.SQL_ERROR_VALUE) {
            for (int j = 0; j < cursor.getCount(); j++) {
                classificationDataList.add(getClassificationData(cursor,
                        timestampPosition, subTypePosition, valuePosition));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return classificationDataList;
    }

    private ClassificationData getClassificationData(Cursor cursor, int timestampPosition,
                                                     int subTypePosition, int valuePosition) {
        ClassificationData classificationData = new ClassificationData();
        classificationData.setTimestamp(cursor.getLong(timestampPosition));
        classificationData.setClassificationType(cursor.getInt(subTypePosition));
        classificationData.setValue(cursor.getFloat(valuePosition));
        return classificationData;
    }

    private List getTrustLevelDataList(Cursor cursor, int timestampPosition,
                                       int confidencePosition, int valuePosition) {
        List<TrustLevelData> trustLevelDataList = new ArrayList<>();
        if (cursor.getPosition() != SqlDataBaseHelper.SQL_ERROR_VALUE) {
            for (int j = 0; j < cursor.getCount(); j++) {
                trustLevelDataList.add(getTrustLevelData(cursor, timestampPosition,
                        confidencePosition, valuePosition));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return trustLevelDataList;
    }

    private TrustLevelData getTrustLevelData(Cursor cursor, int timestampPosition,
                                             int confidencePosition, int valuePosition) {
        TrustLevelData trustLevelData = new TrustLevelData();
        trustLevelData.setTimestamp(cursor.getLong(timestampPosition));
        trustLevelData.setConfidence(cursor.getFloat(confidencePosition));
        trustLevelData.setValue(cursor.getFloat(valuePosition));
        return trustLevelData;
    }

    public void logMessageIntoDataBase(int level, String tag, String message, Throwable throwable) throws DataPersistingException {
        String exceptionMessage = null;
        if (throwable != null) {
            exceptionMessage = throwable.getMessage();
        }
        ContentValues contentValues = getContentValues(level, tag, message, exceptionMessage);
        insert(SqlDataBaseHelper.TABLE_LOG, contentValues);
    }

    @Override
    public String toString() {
        return toMarkdownElement().toString();
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        try {
            StringBuilder sb = new StringBuilder(TAG)
                    .append(" with tag ")
                    .append(tableTag)
                    .append("\n");
            List<DataBatch> dataBatchList = new DataBaseQueryBuilder(this)
                    .forData()
                    .withTableTag(tableTag)
                    .asDataBatches();
            for (DataBatch dataBatch : dataBatchList) {
                sb.append(dataBatch.toMarkdownElement());
            }
            sb.append("\n");
            return new NormalText(sb);
        } catch (DataPersistingException ex) {
            Logger.w(TAG, "MarkDown could not be created because query failed: " + ex.getMessage());
            return new NormalText("");
        }
    }

    @Override
    public void logPersistedData() {
        if (sqlLiteDataBase == null) {
            return;
        }
        Logger.v(TAG, toMarkdownElement().toString());
    }

    public String getTableTag() {
        return tableTag;
    }

    public SQLiteDatabase getSqlLiteDataBase() {
        return sqlLiteDataBase;
    }

    public void setSqlLiteDataBase(SQLiteDatabase sqlLiteDataBase) {
        this.sqlLiteDataBase = sqlLiteDataBase;
    }
}
