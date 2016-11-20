package com.zurich.authenticator.data.persister.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.util.logging.Logger;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlDataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = SqlDataBaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Authenticator.db";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE";
    public static final String PRIMARY_KEY = "PRIMARY KEY";
    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS";
    public static final String SQL_AND = "AND";
    public static final String SQL_OR = "OR";
    public static final String SQL_ORDER_ASC = "ASC";
    public static final String SQL_ORDER_DESC = "DESC";

    public static final String TAG_DEFAULT = "default";
    public static final String TAG_RECORD = "record";

    public static final String TYPE_FLOAT = "FLOAT";
    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_INTEGER = "INTEGER";
    public static final String TYPE_BLOB = "BLOB";


    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "col_timestamp";
    public static final String COLUMN_SUB_TYPE = "col_sub_type";
    public static final String COLUMN_VALUES = "col_values";
    public static final String COLUMN_VALUE = "col_value";
    public static final String COLUMN_CONFIDENCE = "col_confidence";
    public static final String COLUMN_LOG_LEVEL = "col_log_level";
    public static final String COLUMN_LOG_TAG = "col_log_tag";
    public static final String COLUMN_LOG_MESSAGE = "col_log_message";
    public static final String COLUMN_LOG_THROWABLE = "col_log_throwable";

    public static final int SQL_ERROR_VALUE = -1;

    public static final String TABLE_SENSOR_EVENTS = getTableName(Data.TYPE_SENSOR_EVENT);
    public static final String TABLE_STATES = getTableName(Data.TYPE_STATE);
    public static final String TABLE_FEATURES = getTableName(Data.TYPE_FEATURE);
    public static final String TABLE_CLASSIFICATIONS = getTableName(Data.TYPE_CLASSIFICATION);
    public static final String TABLE_TRUST_LEVELS = getTableName(Data.TYPE_TRUST_LEVEL);
    public static final String TABLE_LOG = "log";

    public static final int TABLE_TYPE_MESSAGE = 8000;

    public static final Map<String, LinkedHashSet<String>> AVAILABLE_TABLE_COLUMNS = getAllAvailableTableColumns();
    public static final Map<String, String> COLUMN_TYPES = getColumnTypes();

    public SqlDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SqlDataBaseHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "onCreate() called with: db = [" + db + "]");
        setupTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.d(TAG, "onUpgrade() called with: db = [" + db + "], oldVersion = [" + oldVersion + "], newVersion = [" + newVersion + "]");
        switch (oldVersion) {
            // TODO cascade through versions to alter existing db's to the newest version
            default:
                File file = new File(db.getPath());
                SQLiteDatabase.deleteDatabase(file);
                this.onCreate(db);
        }
        // TODO: migrate database
    }

    public static void setupTables(SQLiteDatabase db) {
        for (String tag : getAcceptedTags()) {
            setupTablesForTag(db, tag);
        }
        db.execSQL(getCreateMessageTableSqlCommand());
    }

    public static void setupTablesForTag(SQLiteDatabase db, String tag) {
        db.execSQL(getCreateSensorEventsTableSqlCommand(tag));
        db.execSQL(getCreateStatesTableSqlCommand(tag));
        db.execSQL(getCreateFeaturesTableSqlCommand(tag));
        db.execSQL(getCreateClassificationTableSqlCommand(tag));
        db.execSQL(getCreateTrustLevelTableSqlCommand(tag));
    }

    public static void setupTableForTag(SQLiteDatabase db, String sqlSetupCommand) {
        db.execSQL(sqlSetupCommand);
    }

    public static String getTableName(int type) {
        String readableType = Data.getReadableType(type);
        return readableType.toLowerCase().replace(" ", "_");
    }

    public static String getTableName(int type, String tag) {
        String readableType = Data.getReadableType(type);
        readableType = tag + "_" + readableType;
        return readableType.toLowerCase().replace(" ", "_");
    }

    public static String getCreateTableSqlCommand(String tableName, Set<String> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append(SQL_CREATE_TABLE).append(" ")
                .append(tableName).append("(");
        int index = 0;
        for (String columnName : columns) {
            if (index != 0) {
                sb.append(", ");
            }
            sb.append(columnName).append(" ").append(COLUMN_TYPES.get(columnName));
            if (index == 0) {
                sb.append(" ").append(PRIMARY_KEY);
            }
            index++;
        }
        sb.append("); ");
        return sb.toString();
    }

    public static String getCreateTableSqlCommand(String tag, int type) {
        switch (type) {
            case Data.TYPE_SENSOR_EVENT:
                return getCreateSensorEventsTableSqlCommand(tag);
            case Data.TYPE_STATE:
                return getCreateStatesTableSqlCommand(tag);
            case Data.TYPE_FEATURE:
                return getCreateFeaturesTableSqlCommand(tag);
            case Data.TYPE_CLASSIFICATION:
                return getCreateClassificationTableSqlCommand(tag);
            case Data.TYPE_TRUST_LEVEL:
                return getCreateTrustLevelTableSqlCommand(tag);
            case TABLE_TYPE_MESSAGE:
                return getCreateMessageTableSqlCommand();
            default:
                Logger.w(TAG, "Can't get create table command for unknown tag: " + tag);
                return null;
        }
    }

    public static String getCreateSensorEventsTableSqlCommand(String tag) {
        String tableName = getTableName(Data.TYPE_SENSOR_EVENT, tag);
        return getCreateTableSqlCommand(tableName, AVAILABLE_TABLE_COLUMNS.get(TABLE_SENSOR_EVENTS));
    }

    public static String getCreateStatesTableSqlCommand(String tag) {
        String tableName = getTableName(Data.TYPE_STATE, tag);
        return getCreateTableSqlCommand(tableName, AVAILABLE_TABLE_COLUMNS.get(TABLE_STATES));
    }

    public static String getCreateFeaturesTableSqlCommand(String tag) {
        String tableName = getTableName(Data.TYPE_FEATURE, tag);
        return getCreateTableSqlCommand(tableName, AVAILABLE_TABLE_COLUMNS.get(TABLE_FEATURES));
    }

    public static String getCreateClassificationTableSqlCommand(String tag) {
        String tableName = getTableName(Data.TYPE_CLASSIFICATION, tag);
        return getCreateTableSqlCommand(tableName, AVAILABLE_TABLE_COLUMNS.get(TABLE_CLASSIFICATIONS));
    }

    public static String getCreateTrustLevelTableSqlCommand(String tag) {
        String tableName = getTableName(Data.TYPE_TRUST_LEVEL, tag);
        return getCreateTableSqlCommand(tableName, AVAILABLE_TABLE_COLUMNS.get(TABLE_TRUST_LEVELS));
    }

    public static String getCreateMessageTableSqlCommand() {
        return getCreateTableSqlCommand(TABLE_LOG, AVAILABLE_TABLE_COLUMNS.get(TABLE_LOG));
    }

    public static List<String> getAcceptedTags() {
        List<String> acceptedTags = new ArrayList<>();
        acceptedTags.add(TAG_DEFAULT);
        acceptedTags.add(TAG_RECORD);
        return acceptedTags;
    }

    public static List<String> getAllColumnNames() {
        List<String> columnNames = new ArrayList<>();
        columnNames.add(COLUMN_ID);
        columnNames.add(COLUMN_TIMESTAMP);
        columnNames.add(COLUMN_SUB_TYPE);
        columnNames.add(COLUMN_VALUES);
        columnNames.add(COLUMN_CONFIDENCE);
        columnNames.add(COLUMN_LOG_LEVEL);
        columnNames.add(COLUMN_LOG_TAG);
        columnNames.add(COLUMN_LOG_MESSAGE);
        columnNames.add(COLUMN_LOG_THROWABLE);
        return columnNames;
    }

    public static List<Integer> getTableTypes() {
        List<Integer> tableTypes = new ArrayList<>();
        tableTypes.add(Data.TYPE_SENSOR_EVENT);
        tableTypes.add(Data.TYPE_STATE);
        tableTypes.add(Data.TYPE_FEATURE);
        tableTypes.add(Data.TYPE_CLASSIFICATION);
        tableTypes.add(Data.TYPE_TRUST_LEVEL);
        tableTypes.add(TABLE_TYPE_MESSAGE);
        return tableTypes;
    }

    public static List<String> getPossibleTableNames() {
        List<String> tableNames = new ArrayList<>();
        for (int type : getTableTypes()) {
            if (type == TABLE_TYPE_MESSAGE) {
                tableNames.add(TABLE_LOG);
            } else {
                for (String tag : getAcceptedTags()) {
                    tableNames.add(getTableName(type, tag));
                }
            }
        }
        return tableNames;
    }

    public static List<String> getTableNamesForType(int tableType) {
        List<String> tableNames = new ArrayList<>();
        for (String tableTag : getAcceptedTags()) {
            tableNames.add(getTableName(tableType, tableTag));
        }
        return tableNames;
    }

    public static List<String> getTableNamesForTag(String tableTag) {
        List<String> tableNames = new ArrayList<>();
        for (int tableType : getTableTypes()) {
            tableNames.add(getTableName(tableType, tableTag));
        }
        return tableNames;
    }

    public static LinkedHashSet<String> getSensorEventSchema() {
        LinkedHashSet<String> sensorEventSchema = new LinkedHashSet<>();
        sensorEventSchema.add(COLUMN_ID);
        sensorEventSchema.add(COLUMN_TIMESTAMP);
        sensorEventSchema.add(COLUMN_SUB_TYPE);
        sensorEventSchema.add(COLUMN_VALUES);
        return sensorEventSchema;
    }

    public static LinkedHashSet<String> getStateSchema() {
        LinkedHashSet<String> stateSchema = new LinkedHashSet<>();
        stateSchema.add(COLUMN_ID);
        stateSchema.add(COLUMN_TIMESTAMP);
        stateSchema.add(COLUMN_SUB_TYPE);
        stateSchema.add(COLUMN_VALUE);
        return stateSchema;
    }

    public static LinkedHashSet<String> getFeatureSchema() {
        LinkedHashSet<String> featureSchema = new LinkedHashSet<>();
        featureSchema.add(COLUMN_ID);
        featureSchema.add(COLUMN_TIMESTAMP);
        featureSchema.add(COLUMN_SUB_TYPE);
        featureSchema.add(COLUMN_VALUES);
        return featureSchema;
    }

    public static LinkedHashSet<String> getClassificationSchema() {
        LinkedHashSet<String> classificationSchema = new LinkedHashSet<>();
        classificationSchema.add(COLUMN_ID);
        classificationSchema.add(COLUMN_TIMESTAMP);
        classificationSchema.add(COLUMN_SUB_TYPE);
        classificationSchema.add(COLUMN_VALUE);
        return classificationSchema;
    }

    public static LinkedHashSet<String> getTrustLevelSchema() {
        LinkedHashSet<String> trustLevelSchema = new LinkedHashSet<>();
        trustLevelSchema.add(COLUMN_ID);
        trustLevelSchema.add(COLUMN_TIMESTAMP);
        trustLevelSchema.add(COLUMN_CONFIDENCE);
        trustLevelSchema.add(COLUMN_VALUE);
        return trustLevelSchema;
    }

    public static LinkedHashSet<String> getLogSchema() {
        LinkedHashSet<String> logSchema = new LinkedHashSet<>();
        logSchema.add(COLUMN_ID);
        logSchema.add(COLUMN_TIMESTAMP);
        logSchema.add(COLUMN_LOG_LEVEL);
        logSchema.add(COLUMN_LOG_TAG);
        logSchema.add(COLUMN_LOG_MESSAGE);
        logSchema.add(COLUMN_LOG_THROWABLE);
        return logSchema;
    }

    public static Map<String, LinkedHashSet<String>> getAllAvailableTableColumns() {
        Map<String, LinkedHashSet<String>> availableTableColumns = new HashMap<>();
        availableTableColumns.put(TABLE_SENSOR_EVENTS, getSensorEventSchema());
        availableTableColumns.put(TABLE_STATES, getStateSchema());
        availableTableColumns.put(TABLE_FEATURES, getFeatureSchema());
        availableTableColumns.put(TABLE_CLASSIFICATIONS, getClassificationSchema());
        availableTableColumns.put(TABLE_TRUST_LEVELS, getTrustLevelSchema());
        availableTableColumns.put(TABLE_LOG, getLogSchema());
        return availableTableColumns;
    }

    public static Map<String, String> getColumnTypes() {
        Map<String, String> columnTypes = new HashMap<>();
        columnTypes.put(COLUMN_ID, TYPE_INTEGER);
        columnTypes.put(COLUMN_TIMESTAMP, TYPE_INTEGER);
        columnTypes.put(COLUMN_SUB_TYPE, TYPE_INTEGER);
        columnTypes.put(COLUMN_CONFIDENCE, TYPE_FLOAT);
        columnTypes.put(COLUMN_VALUE, TYPE_FLOAT);
        columnTypes.put(COLUMN_VALUES, TYPE_TEXT);
        return columnTypes;
    }

    public static int getDataType(String tableName) {
        int dataType = 0;
        if (tableName.contains(TABLE_SENSOR_EVENTS)) {
            dataType = Data.TYPE_SENSOR_EVENT;
        } else if (tableName.contains(TABLE_STATES)) {
            dataType = Data.TYPE_STATE;
        } else if (tableName.contains(TABLE_FEATURES)) {
            dataType = Data.TYPE_FEATURE;
        } else if (tableName.contains(TABLE_CLASSIFICATIONS)) {
            dataType = Data.TYPE_CLASSIFICATION;
        } else if (tableName.contains(TABLE_TRUST_LEVELS)) {
            dataType = Data.TYPE_TRUST_LEVEL;
        } else {
            Logger.w(TAG, "Table name doesn't match known type:" + tableName);
        }
        return dataType;
    }

    public static Map.Entry<String, String[]> getSelectionConditions(List<Integer> subTypes,
                                                                     long startTimestamp,
                                                                     long endTimestamp) {
        // TODO add new possible selection conditions
        StringBuilder selection = new StringBuilder();
        // use selectionArgs only for strings as android will bind and compare the args as strings
        // use ? as representative of the string within the selection string
        List<String> selectionArgs = new ArrayList<>();
        if ((subTypes != null) && (subTypes.size() > 0)) {
            // 'and' is stronger than 'or'; group the 'or's' in brackets
            selection.append("(");
            for (int subType : subTypes) {
                if (selection.length() > 1) {
                    selection.append(" ")
                            .append(SQL_OR)
                            .append(" ");
                }
                selection.append(COLUMN_SUB_TYPE)
                        .append("=")
                        .append(subType);
            }
            selection.append(")");
        }
        if (startTimestamp > 0) {
            if (selection.length() > 1) {
                selection.append(" ")
                        .append(SQL_AND)
                        .append(" ");
            }
            selection.append(COLUMN_TIMESTAMP)
                    .append(">=")
                    .append(String.valueOf(startTimestamp));
        }
        if (endTimestamp > 0) {
            if (selection.length() > 1) {
                selection.append(" ")
                        .append(SQL_AND)
                        .append(" ");
            }
            selection.append(COLUMN_TIMESTAMP)
                    .append("<=")
                    .append(String.valueOf(endTimestamp));
        }
        return new AbstractMap.SimpleEntry<>(selection.toString(), selectionArgs.toArray(new String[]{}));
    }

    public static List<String> getRequestedSelectionColumns(int size,
                                                            long startTimestamp,
                                                            long endTimestamp) {
        List<String> requestedColumns = new ArrayList<>();
        if (size > 0) {
            requestedColumns.add(COLUMN_SUB_TYPE);
        }
        if (startTimestamp >= 0 || endTimestamp > 0) {
            requestedColumns.add(COLUMN_TIMESTAMP);
        }
        return requestedColumns;
    }
}
