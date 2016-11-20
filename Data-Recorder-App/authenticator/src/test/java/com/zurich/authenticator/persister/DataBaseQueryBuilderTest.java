package com.zurich.authenticator.persister;

import android.content.Context;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.database.DataBasePersister;
import com.zurich.authenticator.data.persister.database.DataBaseQueryBuilder;
import com.zurich.authenticator.data.persister.database.SqlDataBaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DataBaseQueryBuilderTest {
    @Mock
    Context context;
    DataBasePersister dataBasePersister;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void query_nothing_throwsException() throws DataPersistingException {
        try {
            new DataBaseQueryBuilder(dataBasePersister)
                    .asQueryBuilder();
            fail("No exception thrown");
        } catch (DataPersistingException ex) {
        }
    }

    @Test
    public void query_validName_containsValues() throws DataPersistingException {
        String tableName = SqlDataBaseHelper.getTableName(Data.TYPE_SENSOR_EVENT, SqlDataBaseHelper.TAG_RECORD);
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forTable(tableName)
                .asQueryBuilder();
        assertEquals("Size should be 1", dataBaseQueryBuilder.getTableNames().size(), 1);
        assertEquals("Table name should be: " + tableName, dataBaseQueryBuilder.getTableNames().get(0), tableName);
        testOnlyTableNameSet(dataBaseQueryBuilder);
    }

    @Test
    public void query_validNames_containsData() throws DataPersistingException {
        List<String> tableNames = new ArrayList<>();
        tableNames.add(SqlDataBaseHelper.getTableName(Data.TYPE_SENSOR_EVENT, SqlDataBaseHelper.TAG_RECORD));
        tableNames.add(SqlDataBaseHelper.getTableName(Data.TYPE_SENSOR_EVENT, SqlDataBaseHelper.TAG_DEFAULT));
        tableNames.add(SqlDataBaseHelper.getTableName(Data.TYPE_TRUST_LEVEL, SqlDataBaseHelper.TAG_DEFAULT));
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forTables(tableNames)
                .asQueryBuilder();
        assertEquals("Number of table names doesn't match", tableNames.size(), dataBaseQueryBuilder.getTableNames().size());
        testOnlyTableNameSet(dataBaseQueryBuilder);
    }

    @Test
    public void query_invalidTableNames_throwException() throws DataPersistingException {
        try {
            String tableName = "wrong_table_name";
            DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                    .forTable(tableName)
                    .asQueryBuilder();
            fail("No exception thrown");
        } catch (DataPersistingException ex) {
        }
    }

    @Test
    public void query_validType_containsSensorEvents() throws DataPersistingException {
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forSensorEvents()
                .asQueryBuilder();
        checkTableNamesForGivenType(Data.TYPE_SENSOR_EVENT, dataBaseQueryBuilder);
    }

    @Test
    public void query_validType_containsStates() throws DataPersistingException {
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forStates()
                .asQueryBuilder();
        checkTableNamesForGivenType(Data.TYPE_STATE, dataBaseQueryBuilder);
    }

    @Test
    public void query_validType_containsFeatures() throws DataPersistingException {
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forFeatures()
                .asQueryBuilder();
        checkTableNamesForGivenType(Data.TYPE_FEATURE, dataBaseQueryBuilder);
    }

    @Test
    public void query_validType_containsClassifications() throws DataPersistingException {
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forClassifications()
                .asQueryBuilder();
        checkTableNamesForGivenType(Data.TYPE_CLASSIFICATION, dataBaseQueryBuilder);
    }

    @Test
    public void query_validType_containsTrustLevels() throws DataPersistingException {
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forTrustLevels()
                .asQueryBuilder();
        checkTableNamesForGivenType(Data.TYPE_TRUST_LEVEL, dataBaseQueryBuilder);
    }

    @Test
    public void query_validTypes_containsDataWithTypes() throws DataPersistingException {
        List<Integer> dataTypes = new ArrayList<>();
        dataTypes.add(Data.TYPE_FEATURE);
        dataTypes.add(Data.TYPE_CLASSIFICATION);
        dataTypes.add(Data.TYPE_TRUST_LEVEL);

        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forTypes(dataTypes)
                .asQueryBuilder();
        checkTableNamesForGivenTypes(dataTypes, dataBaseQueryBuilder);
    }

    @Test
    public void query_invalidType_throwsException() {
        try {
            new DataBaseQueryBuilder(dataBasePersister)
                    .forType(Data.TYPE_NOT_SET)
                    .asQueryBuilder();
            fail("No exception thrown");
        } catch (DataPersistingException ex) {
        }
    }

    @Test
    public void query_validTag_containsDifferentData() throws DataPersistingException {
        String tableTag = SqlDataBaseHelper.TAG_RECORD;
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forTrustLevels()
                .withTableTag(tableTag)
                .asQueryBuilder();
        checkTableNamesForGivenTag(tableTag, Data.TYPE_TRUST_LEVEL, dataBaseQueryBuilder);
    }

    @Test
    public void query_validTags_containsDifferentData() throws DataPersistingException {
        List<String> tableTags = new ArrayList<>();
        tableTags.add(SqlDataBaseHelper.TAG_RECORD);
        tableTags.add(SqlDataBaseHelper.TAG_DEFAULT);
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forClassifications()
                .withTableTags(tableTags)
                .asQueryBuilder();
        checkTableNamesForGivenTags(tableTags, Data.TYPE_CLASSIFICATION, dataBaseQueryBuilder);
    }

    @Test
    public void query_validAndInvalidTags_queriesValidTags() throws DataPersistingException {
        List<String> tableTags = new ArrayList<>();
        tableTags.add(SqlDataBaseHelper.TAG_RECORD);
        tableTags.add("wrong_tag");
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forFeatures()
                .withTableTags(tableTags)
                .asQueryBuilder();
        tableTags.remove("wrong_tag");
        checkTableNamesForGivenTags(tableTags, Data.TYPE_FEATURE, dataBaseQueryBuilder);
    }

    @Test
    public void query_invalidTag_throwsException() throws DataPersistingException {
        try {
            String tableTag = "wrong_tag";
            DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                    .forStates()
                    .withTableTag(tableTag)
                    .asQueryBuilder();
            fail("No exception thrown");
        } catch (DataPersistingException ex) {
        }
    }

    @Test
    public void query_invalidTimestamp_throwsException() throws DataPersistingException {
        try {
            DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                    .forSensorEvents()
                    .getDataSince(-2)
                    .asQueryBuilder();
            fail("No exception thrown");
        } catch (DataPersistingException ex) {
        }
    }

    @Test
    public void query_withTimestampsAndDuration_throwsException() throws DataPersistingException {
        try {
            DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                    .forSensorEvents()
                    .getDataBetween(0, System.currentTimeMillis())
                    .withDuration(15)
                    .asQueryBuilder();
            fail("No exception thrown");
        } catch (DataPersistingException ex) {
        }
    }

    @Test
    public void query_withDuration_setsTimestampsCorrectly() throws DataPersistingException {
        long startTimestamp = 10;
        long duration = 15;
        DataBaseQueryBuilder dataBaseQueryBuilder = new DataBaseQueryBuilder(dataBasePersister)
                .forSensorEvents()
                .getDataSince(startTimestamp)
                .withDuration(duration)
                .asQueryBuilder();
        long endTimestamp = startTimestamp + duration;
        assertEquals("Endtimestamp wasn't calculated correctly", endTimestamp, dataBaseQueryBuilder.getEndTimestamp());
    }

    // helper methods

    public void checkTableNamesForGivenType(int dataType, DataBaseQueryBuilder dataBaseQueryBuilder) throws DataPersistingException {
        List<Integer> dataTypes = new ArrayList<>();
        dataTypes.add(dataType);
        checkTableNamesForGivenTypes(dataTypes, dataBaseQueryBuilder);
    }

    public void checkTableNamesForGivenTypes(List<Integer> dataTypes, DataBaseQueryBuilder dataBaseQueryBuilder) throws DataPersistingException {
        List<String> tableNameCopy = new ArrayList<>(dataBaseQueryBuilder.getTableNames());
        List<String> expectedTableNames = new ArrayList<>();
        for (int dataType : dataTypes) {
            expectedTableNames.addAll(SqlDataBaseHelper.getTableNamesForType(dataType));
        }
        for (String tableName : dataBaseQueryBuilder.getTableNames()) {
            for (int dataType : dataTypes) {
                if (tableName.contains(SqlDataBaseHelper.getTableName(dataType))) {
                    tableNameCopy.remove(tableName);
                }
            }
            if (expectedTableNames.contains(tableName)) {
                expectedTableNames.remove(tableName);
            }
        }
        assertTrue("Table names did not only contain table names for the given types", tableNameCopy.isEmpty());
        assertTrue("Did not contain all table names", expectedTableNames.isEmpty());
        testOnlyTableNameSet(dataBaseQueryBuilder);
    }

    public static void testOnlyTableNameSet(DataBaseQueryBuilder dataBaseQueryBuilder) {
        assertEquals("Limit should be null", dataBaseQueryBuilder.getLimit(), null);
        assertEquals("StartTimestamp should not be set", dataBaseQueryBuilder.getStartTimestamp(), DataBaseQueryBuilder.NOT_SET);
        assertEquals("EndTimestamp should not be set", dataBaseQueryBuilder.getEndTimestamp(), DataBaseQueryBuilder.NOT_SET);
        assertTrue("Subtypes should be empty", dataBaseQueryBuilder.getSubTypes().isEmpty());
        assertFalse("Distinct should be false", dataBaseQueryBuilder.isDistinct());
        assertEquals("Order should be ASC", dataBaseQueryBuilder.getOrder(), SqlDataBaseHelper.SQL_ORDER_ASC);
    }

    public static void checkTableNamesForGivenTag(String tableTag, int dataType, DataBaseQueryBuilder dataBaseQueryBuilder) {
        List<String> tableTags = new ArrayList<>();
        tableTags.add(tableTag);
        checkTableNamesForGivenTags(tableTags, dataType, dataBaseQueryBuilder);
    }

    public static void checkTableNamesForGivenTags(List<String> tableTags, int dataType, DataBaseQueryBuilder dataBaseQueryBuilder) {
        List<String> tableNameCopy = new ArrayList<>(dataBaseQueryBuilder.getTableNames());
        List<String> expectedTableNames = new ArrayList<>();
        for (String tableTag : tableTags) {
            expectedTableNames.add(SqlDataBaseHelper.getTableName(dataType, tableTag));
        }
        for (String tableName : dataBaseQueryBuilder.getTableNames()) {
            for (String tableTag : tableTags) {
                if (tableName.contains(tableTag)) {
                    tableNameCopy.remove(tableName);
                }
            }
            if (expectedTableNames.contains(tableName)) {
                expectedTableNames.remove(tableName);
            }
        }
        assertTrue("Table names did not only contain table names for the given tags", tableNameCopy.isEmpty());
        assertTrue("Did not contain all table names", expectedTableNames.isEmpty());
        testOnlyTableNameSet(dataBaseQueryBuilder);
    }

}
