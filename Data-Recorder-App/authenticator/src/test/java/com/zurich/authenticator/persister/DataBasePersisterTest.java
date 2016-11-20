package com.zurich.authenticator.persister;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;

import com.zurich.authenticator.BuildConfig;
import com.zurich.authenticator.data.DummyData.ClassificationDummyDataBatch;
import com.zurich.authenticator.data.DummyData.DummyDataBuilder;
import com.zurich.authenticator.data.DummyData.DummyDataException;
import com.zurich.authenticator.data.DummyData.FeatureDummyDataBatch;
import com.zurich.authenticator.data.DummyData.SensorEventDummyDataBatch;
import com.zurich.authenticator.data.DummyData.StateDummyDataBatch;
import com.zurich.authenticator.data.DummyData.TrustLevelDummyDataBatch;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.classification.manager.ClassificationManager;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.database.DataBasePersister;
import com.zurich.authenticator.data.persister.database.DataBaseQueryBuilder;
import com.zurich.authenticator.data.persister.database.SqlDataBaseHelper;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.state.manager.StateManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, instrumentedPackages = {"com.zurich.authenticator.data.persister.database"})
public class DataBasePersisterTest {

    private static final String TAG = DataBasePersisterTest.class.getSimpleName();
    private static final int DEFAULT_ITEM_COUNT = 5;
    private static final long DEFAULT_TIMESTAMP_DELTA = TimeUnit.SECONDS.toMillis(1);
    private static final long DEFAULT_START_TIMESTAMP = 0;

    DataBasePersister defaultDataBasePersister = new DataBasePersister(RuntimeEnvironment.application);

    public void persistSensorEventTestData(int subType) throws DummyDataException, DataPersistingException {
        SensorEventDummyDataBatch sensorEventDummyDataBatch = (SensorEventDummyDataBatch) DummyDataBuilder
                .forSensorEventData()
                .withSubType(subType)
                .withItemCount(DEFAULT_ITEM_COUNT)
                .withStartTimestamp(DEFAULT_START_TIMESTAMP)
                .withTimestampDelta(DEFAULT_TIMESTAMP_DELTA)
                .build();
        List<Data> dataList = new ArrayList<Data>(sensorEventDummyDataBatch.getDataBatch().getDataList());
        defaultDataBasePersister.persist(dataList);
    }

    public void persistSensorEventTestData() throws DummyDataException, DataPersistingException {
        persistSensorEventTestData(Sensor.TYPE_ACCELEROMETER);
    }

    public void persistStateTestData() throws DummyDataException, DataPersistingException {
        StateDummyDataBatch stateDummyDataBatch = (StateDummyDataBatch) DummyDataBuilder
                .forStateData()
                .withSubType(StateManager.STATE_DISPLAY_ON)
                .withItemCount(DEFAULT_ITEM_COUNT)
                .withStartTimestamp(DEFAULT_START_TIMESTAMP)
                .withTimestampDelta(DEFAULT_TIMESTAMP_DELTA)
                .build();
        List<Data> dataList = new ArrayList<Data>(stateDummyDataBatch.getDataBatch().getDataList());
        defaultDataBasePersister.persist(dataList);
    }

    public void persistFeatureTestData() throws DummyDataException, DataPersistingException {
        FeatureDummyDataBatch featureDummyDataBatch = (FeatureDummyDataBatch) DummyDataBuilder
                .forFeatureData()
                .withSubType(FeatureManager.TYPE_VARIANCE_ACCELEROMETER)
                .withItemCount(DEFAULT_ITEM_COUNT)
                .withStartTimestamp(0)
                .build();
        List<Data> dataList = new ArrayList<Data>(featureDummyDataBatch.getDataBatch().getDataList());
        defaultDataBasePersister.persist(dataList);
    }

    public void persistClassificationTestData() throws DummyDataException, DataPersistingException {
        ClassificationDummyDataBatch classificationDummyDataBatch = (ClassificationDummyDataBatch) DummyDataBuilder
                .forClassificationData()
                .withSubType(ClassificationManager.CLASSIFICATION_WALKING)
                .withItemCount(DEFAULT_ITEM_COUNT)
                .withStartTimestamp(DEFAULT_START_TIMESTAMP)
                .withTimestampDelta(DEFAULT_TIMESTAMP_DELTA)
                .build();
        List<Data> dataList = new ArrayList<Data>(classificationDummyDataBatch.getDataBatch().getDataList());
        defaultDataBasePersister.persist(dataList);
    }

    public void persistTrustLevelTestData() throws DummyDataException, DataPersistingException {
        TrustLevelDummyDataBatch trustLevelDummyDataBatch = (TrustLevelDummyDataBatch) DummyDataBuilder
                .forTrustLevelData()
                .withItemCount(DEFAULT_ITEM_COUNT)
                .withStartTimestamp(DEFAULT_START_TIMESTAMP)
                .withTimestampDelta(DEFAULT_TIMESTAMP_DELTA)
                .build();
        List<Data> dataList = new ArrayList<Data>(trustLevelDummyDataBatch.getDataBatch().getDataList());
        defaultDataBasePersister.persist(dataList);
    }

    public void persistTestData() throws DummyDataException, DataPersistingException {
        persistSensorEventTestData();
        persistStateTestData();
        persistFeatureTestData();
        persistClassificationTestData();
        persistTrustLevelTestData();
    }

    @Test
    public void dataBaseConstruction_ConstructorWithNullPath_createsInMemoryDatabase() {
        SqlDataBaseHelper helper = new SqlDataBaseHelper(null, null);
        SQLiteDatabase database = helper.getReadableDatabase();
        assertDatabaseOpened(database);
    }

    @Test
    public void canPersist_validData_returnsTrue() {
        SensorEventData sensorEventData = new SensorEventData(Sensor.TYPE_ACCELEROMETER);
        sensorEventData.setValues(new float[]{0, 0, 0});
        assertTrue("Can't persist valid data", defaultDataBasePersister.canPersist(sensorEventData));
    }

    @Test
    public void canPersist_invalidData_returnsFalse() {
        Data data = new SensorEventData(Sensor.TYPE_ACCELEROMETER);
        data.setType(Data.TYPE_NOT_SET);
        assertFalse("Can persist invalid data", defaultDataBasePersister.canPersist(data));
    }

    @Test
    public void canPersist_noExistingDataBase_canNotPersist() {
        defaultDataBasePersister.setSqlLiteDataBase(null);
        SensorEventData sensorEventData = new SensorEventData(Sensor.TYPE_ACCELEROMETER);
        sensorEventData.setValues(new float[]{0, 0, 0});
        assertFalse("Can persist data on null dataBase", defaultDataBasePersister.canPersist(sensorEventData));
    }

    @Test
    public void getAvailableTableNames_existingDataBase_returnsAllActualTableNames() {
        List<String> possibleTableNames = SqlDataBaseHelper.getPossibleTableNames();
        // +1 for android_metadata table
        int numberOfCreatedTables = SqlDataBaseHelper.getPossibleTableNames().size() + 1;
        boolean containsMetaData = false;

        List<String> tableNames = defaultDataBasePersister.getAvailableTableNames();

        assertEquals("Database doesn't contain correct number of tables", tableNames.size(), numberOfCreatedTables);
        for (String tableName : tableNames) {
            if (possibleTableNames.contains(tableName)) {
                possibleTableNames.remove(tableName);
            } else if (tableName.contains("meta")) {
                containsMetaData = true;
            } else {
                fail("Database should not contain " + tableName);
            }
        }
        assertTrue("Database doesn't contain all possible tableNames", possibleTableNames.isEmpty());
        assertTrue("Database doesn't contain metadata table", containsMetaData);
    }

    @Test
    public void createNewTable_nonEmptyTableName_createsNewTable() {
        String tableName = "test";
        List<String> tableNames = defaultDataBasePersister.getAvailableTableNames();
        assertFalse("DataBase shouldn't contain " + tableName, tableNames.contains(tableName));
        defaultDataBasePersister.createNewTable("CREATE TABLE " + tableName + "(id INTEGER);");
        tableNames = defaultDataBasePersister.getAvailableTableNames();
        assertTrue("DataBase should contain " + tableName, tableNames.contains(tableName));
    }

    @Test
    public void dropTable_existingTable_dropsTable() {
        String tableName = SqlDataBaseHelper.getTableName(Data.TYPE_SENSOR_EVENT, SqlDataBaseHelper.TAG_DEFAULT);
        List<String> tableNames = defaultDataBasePersister.getAvailableTableNames();
        assertTrue("DataBase should contain " + tableName, tableNames.contains(tableName));
        assertTrue("Drop table should return true", defaultDataBasePersister.dropTable(tableName));
        tableNames = defaultDataBasePersister.getAvailableTableNames();
        assertFalse("DataBase shouldn't contain " + tableName, tableNames.contains(tableName));
    }

    @Test
    public void dropTable_nonExistingTable_noTableDropped() {
        String tableName = SqlDataBaseHelper.getTableName(Data.TYPE_NOT_SET, SqlDataBaseHelper.TAG_DEFAULT);
        List<String> tableNames = defaultDataBasePersister.getAvailableTableNames();
        assertFalse("DataBase shouldn't contain " + tableName, tableNames.contains(tableName));

        assertFalse("Drop table should return false", defaultDataBasePersister.dropTable(tableName));
    }

    @Test
    public void dropAndCreateNewTable_existingTable_createsNewTable() throws DataPersistingException, DummyDataException {
        persistSensorEventTestData();
        int dataType = Data.TYPE_SENSOR_EVENT;
        String tableName = SqlDataBaseHelper.getTableName(dataType, defaultDataBasePersister.getTableTag());

        List<String> tableNames = defaultDataBasePersister.getAvailableTableNames();
        assertTrue("DataBase should contain " + tableName, tableNames.contains(tableName));

        List<Data> dataList = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forSensorEvents()
                .asDataList();

        assertFalse("DataList should contain sensor event data", dataList.isEmpty());

        defaultDataBasePersister.dropAndCreateNewTable(dataType);

        tableNames = defaultDataBasePersister.getAvailableTableNames();
        assertTrue("DataBase should contain " + tableName, tableNames.contains(tableName));

        dataList = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forSensorEvents()
                .asDataList();

        assertTrue("DataList should not contain any data", dataList.isEmpty());
    }

    @Test
    public void dropAndCreateNewTable_nonExistingTable_createsNoTable() {
        List<String> availableTables = defaultDataBasePersister.getAvailableTableNames();
        defaultDataBasePersister.dropAndCreateNewTable(Data.TYPE_NOT_SET);
        assertEquals("TableNames should be the same", availableTables, defaultDataBasePersister.getAvailableTableNames());
    }

    @Test
    public void deleteDataBase_correctPath_dataBaseDeleted() {

        /* TODO
        persistSensorEventTestData();
        List<Data> dataList = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forSensorEvents()
                .asDataList();
        assertFalse(dataList.isEmpty());
        assertTrue("", defaultDataBasePersister.deleteDatabase());

        dataList = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forSensorEvents()
                .asDataList();

        //assertTrue(dataList.isEmpty());

        assertNotEquals("", defaultDataBasePersister.getSqlLiteDataBase(), null);

        try {
            List<String> tableNames = defaultDataBasePersister.getAvailableTableNames();
            for (String tableName : tableNames) {
                System.out.println(tableName);
            }
        } catch (Exception ex) {

        }
        */
    }

    @Test
    public void query_dataOnDroppedTable_throwsException() throws DummyDataException {
        try {
            persistSensorEventTestData();
            String tableName = SqlDataBaseHelper.getTableName(Data.TYPE_SENSOR_EVENT, SqlDataBaseHelper.TAG_DEFAULT);
            defaultDataBasePersister.dropTable(tableName);
            List<Data> dataList = new DataBaseQueryBuilder(defaultDataBasePersister)
                    .forSensorEvents()
                    .asDataList();
            fail("Exception should hav been thrown");
        } catch (DataPersistingException ex) {

        }
    }

    @Test
    public void query_allData_returnsDataForEverything() throws DataPersistingException, DummyDataException {
        persistTestData();
        int numberOfExpectedDataBatches = 5;
        List<DataBatch> dataBatchList = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forData()
                .asDataBatches();

        assertEquals("Query did not return correct number of items", dataBatchList.size(), numberOfExpectedDataBatches);
        for (DataBatch<Data> dataBatch : dataBatchList) {
            assertEquals("Requested dataBatch has incorrect number of data", dataBatch.getDataList().size(), DEFAULT_ITEM_COUNT);
        }
    }

    @Test
    public void query_validTimestamps_returnsDataInTimeInterval() throws DataPersistingException, DummyDataException {
        long startTimestamp = 1000;
        long endTimestamp = 3000;

        persistFeatureTestData();
        persistClassificationTestData();
        int numberOfExpectedDataBatches = 2;

        // +1 to ensure boundaries are included
        int numberOfExpectedDataInBatch = (int) ((endTimestamp - startTimestamp) / DEFAULT_TIMESTAMP_DELTA) + 1;

        List<DataBatch> dataBatchList = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forData()
                .getDataBetween(startTimestamp, endTimestamp)
                .asDataBatches();

        assertEquals("Query did not return correct number of items", dataBatchList.size(), numberOfExpectedDataBatches);
        for (DataBatch<Data> dataBatch : dataBatchList) {
            assertEquals("Requested dataBatch has incorrect number of data", dataBatch.getDataList().size(), numberOfExpectedDataInBatch);
            for (Data data : dataBatch.getDataList()) {
                assertTimestampInRange(startTimestamp, endTimestamp, data.getTimestamp());
            }
        }

    }

    @Test
    public void query_validSubtype_returnsDataForSubtype() throws DataPersistingException, DummyDataException {
        persistFeatureTestData();
        persistSensorEventTestData();
        persistSensorEventTestData(Sensor.TYPE_GAME_ROTATION_VECTOR);

        int numberOfExpectedDataBatches = 1;
        int expectedType = Data.TYPE_SENSOR_EVENT;
        int expectedSubType = Sensor.TYPE_ACCELEROMETER;

        List<DataBatch> dataBatchList = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forSensorEvents()
                .withSubType(Sensor.TYPE_ACCELEROMETER)
                .asDataBatches();

        assertEquals("Query did not return correct number of items", dataBatchList.size(), numberOfExpectedDataBatches);
        for (DataBatch<Data> dataBatch : dataBatchList) {
            assertEquals("Requested dataBatch has incorrect number of data", dataBatch.getDataList().size(), DEFAULT_ITEM_COUNT);
            assertTypeMatch(expectedType, expectedSubType, dataBatch.getType(), dataBatch.getSubType());
            for (Data data : dataBatch.getDataList()) {
                assertTypeMatch(expectedType, expectedSubType, data.getType(), ((SensorEventData) data).getSensorType());
            }
        }
    }

    @Test
    public void query_multipleSubTypesWithTimestamp_returnsDataForSubTypeInInterval() throws DataPersistingException, DummyDataException {
        long startTimestamp = 1000;
        long endTimestamp = 3000;

        persistTestData();

        // +1 to ensure boundaries are included
        int numberOfExpectedDataInBatch = (int) ((endTimestamp - startTimestamp) / DEFAULT_TIMESTAMP_DELTA) + 1;
        ;

        List<Integer> subTypes = new ArrayList<>();
        subTypes.add(Sensor.TYPE_ACCELEROMETER);
        subTypes.add(FeatureManager.TYPE_VARIANCE_ACCELEROMETER);
        List<DataBatch> dataList = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forData()
                .withSubTypes(subTypes)
                .getDataBetween(startTimestamp, endTimestamp)
                .asDataBatches();

        assertEquals("Query did not return correct number of items", dataList.size(), subTypes.size());
        for (DataBatch<Data> dataBatch : dataList) {
            boolean validType = dataBatch.getType() == Data.TYPE_SENSOR_EVENT || dataBatch.getType() == Data.TYPE_FEATURE;
            assertEquals("Requested dataBatch has incorrect number of data", dataBatch.getDataList().size(), numberOfExpectedDataInBatch);
            assertTrue("Type of dataBatch did not match", validType);
            assertTrue("SubType of dataBatch did not match", subTypes.contains(dataBatch.getSubType()));
            for (Data data : dataBatch.getDataList()) {
                assertTimestampInRange(startTimestamp, endTimestamp, data.getTimestamp());
            }
        }

    }

    @Test
    public void query_validSubTypeOnWrongTable_emptyResult() throws DataPersistingException, DummyDataException {
        persistFeatureTestData();
        List<Data> dataList = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forSensorEvents()
                .withSubType(Sensor.TYPE_ACCELEROMETER)
                .asDataList();

        assertTrue("DataList is not empty", dataList.isEmpty());
    }

    @Test
    public void query_selectionForWrongSchema_noQueriesPerformed() throws DataPersistingException, DummyDataException {
        persistTrustLevelTestData();
        Map<String, Cursor> cursorMap = new DataBaseQueryBuilder(defaultDataBasePersister)
                .forTrustLevels()
                .withSubType(Sensor.TYPE_ACCELEROMETER)
                .asCursorMap();

        assertTrue("CursorMap is not empty", cursorMap.isEmpty());
    }

    private static void assertTypeMatch(int expectedType, int expectedSubType, int actualType, int actualSubType) {
        assertEquals("Type of data did not match", actualType, expectedType);
        assertEquals("Type of data did not match", actualSubType, expectedSubType);
    }

    private static void assertTimestampInRange(long startTimestamp, long endTimestamp, long actualTimestamp) {
        boolean validTimestamp = actualTimestamp >= startTimestamp && actualTimestamp <= endTimestamp;
        assertTrue("Queried data outside the given interval", validTimestamp);
    }

    private static void assertDatabaseOpened(SQLiteDatabase database) {
        assertTrue(database != null);
        assertTrue(database.isOpen());
    }

}
