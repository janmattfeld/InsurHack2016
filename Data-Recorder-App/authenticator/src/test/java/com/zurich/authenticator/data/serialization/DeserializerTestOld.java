package com.zurich.authenticator.data.serialization;

public class DeserializerTestOld extends SerializationTestOld {

    /*
    private JsonDeserializer<SensorEventData> jsonSensorDataDeserializer;
    private JsonDeserializer<StateData> jsonStateDataDeserializer;
    private JsonDeserializer<FeatureData> jsonFeatureDataDeserializer;
    private JsonDeserializer<ClassificationData> jsonClassificationDataDeserializer;

    private static final List<Long> expectedTimestamps = new ArrayList<>(Arrays.asList(1477038707995L, 1477038708995L, 1477038709995L));
    private static final float[][] expectedValueArrays = new float[][]{{2.0f, 2.0f, 2.0f}, {1.0f, 1.0f, 1.0f}, {0.0f, 0.0f, 0.0f}};
    private static final float[] expectedValues = new float[]{2.0f, 1.0f, 0.0f};

    private int expectedType;
    private int expectedSubType;

    @Before
    public void setUp() throws Exception {
        jsonSensorDataDeserializer = JsonDeserializer.getInstance();
        jsonStateDataDeserializer = JsonDeserializer.getInstance();
        jsonFeatureDataDeserializer = JsonDeserializer.getInstance();
        jsonClassificationDataDeserializer = JsonDeserializer.getInstance();
    }

    @Test
    public void deserialize_emptyList_createsCorrectDataBatch() throws DeserializationException {
        expectedType = Data.TYPE_FEATURE;
        expectedSubType = FeatureManager.TYPE_VARIANCE;
        List<FeatureData> featureData = new ArrayList<>();
        String json = "{\"dataList\":" + featureData + ",\"type\":" + Data.TYPE_FEATURE + ",\"subType\":" + FeatureManager.TYPE_VARIANCE + "}";

        DataBatch<FeatureData> dataBatch = jsonFeatureDataDeserializer.deserialize(json, Deserializer.TOKEN_FEATURE);
        testEmptyDataBatch(dataBatch);
    }

    @Test
    public void deserialize_filledSensorList_createsCorrectDataBatch() throws Exception {
        expectedType = Data.TYPE_SENSOR_EVENT;
        expectedSubType = Sensor.TYPE_ACCELEROMETER;
        String json = createJsonString(expectedType, expectedSubType, expectedTimestamps, expectedValueArrays);

        DataBatch<SensorEventData> dataBatch = jsonSensorDataDeserializer.deserialize(json, Deserializer.TOKEN_SENSOR_EVENT);
        List<SensorEventData> dataList = dataBatch.getDataList();

        testDataBatch(dataBatch);

        for (int i = 0; i < dataList.size(); i++) {
            SensorEventData sd = dataList.get(i);
            testData(sd.getTimestamp(), sd.getSensorType(), sd.getValues(), i);
        }
    }

    @Test
    public void deserialize_filledStateList_createsCorrectDataBatch() throws Exception {
        expectedType = Data.TYPE_STATE;
        expectedSubType = StateManager.STATE_DISPLAY_ON;
        String json = createJsonString(Data.TYPE_STATE, StateManager.STATE_DISPLAY_ON, expectedTimestamps, expectedValues);

        DataBatch<StateData> dataBatch = jsonStateDataDeserializer.deserialize(json, Deserializer.TOKEN_STATE);
        List<StateData> dataList = dataBatch.getDataList();

        testDataBatch(dataBatch);

        for (int i = 0; i < dataList.size(); i++) {
            StateData sd = dataList.get(i);
            testData(sd.getTimestamp(), sd.getStateType(), sd.getValue(), i);
        }
    }

    @Test
    public void deserialize_filledFeatureList_createsCorrectDataBatch() throws Exception {
        expectedType = Data.TYPE_FEATURE;
        expectedSubType = FeatureManager.TYPE_VARIANCE;
        String json = createJsonString(Data.TYPE_FEATURE, FeatureManager.TYPE_VARIANCE, expectedTimestamps, expectedValueArrays);

        DataBatch<FeatureData> dataBatch = jsonFeatureDataDeserializer.deserialize(json, Deserializer.TOKEN_FEATURE);
        List<FeatureData> dataList = dataBatch.getDataList();

        testDataBatch(dataBatch);

        for (int i = 0; i < dataList.size(); i++) {
            FeatureData fd = dataList.get(i);
            testData(fd.getTimestamp(), fd.getFeatureType(), fd.getValues(), i);
        }
    }

    @Test
    public void deserialize_filledClassificationList_createsCorrectDataBatch() throws Exception {
        expectedType = Data.TYPE_CLASSIFICATION;
        expectedSubType = ClassificationManager.CLASSIFICATION_WALKING;
        String json = createJsonString(expectedType, expectedSubType, expectedTimestamps, expectedValues);

        DataBatch<ClassificationData> dataBatch = jsonClassificationDataDeserializer.deserialize(json, Deserializer.TOKEN_CLASSIFICATION);
        List<ClassificationData> dataList = dataBatch.getDataList();

        testDataBatch(dataBatch);

        for (int i = 0; i < dataList.size(); i++) {
            ClassificationData cd = dataList.get(i);
            testData(cd.getTimestamp(), cd.getClassificationType(), cd.getValue(), i);
        }
    }

    @Test
    public void deserialize_incompleteFeatureDataBatchJson_createsObject() throws Exception {
        String json = "{}";
        expectedType = Data.TYPE_NOT_SET;
        expectedSubType = Data.TYPE_NOT_SET;
        DataBatch<FeatureData> dataBatch = jsonFeatureDataDeserializer.deserialize(json, Deserializer.TOKEN_FEATURE);
        testEmptyDataBatch(dataBatch);
    }

    @Test
    public void deserialize_nullJson_createsNullObject() throws Exception {
        String json = "null";
        DataBatch<FeatureData> dataBatch = jsonFeatureDataDeserializer.deserialize(json, Deserializer.TOKEN_FEATURE);
        assertTrue("Created dataBatch instead of null object.", dataBatch == null);
    }

    @Test
    public void deserialize_invalidJson_throwsException() throws Exception {
        String json = "{\"datList\":[]}";
        try {
            DataBatch dataBatch = jsonFeatureDataDeserializer.deserialize(json, Deserializer.TOKEN_FEATURE);
            dataBatch.getDataList();
            //assertTrue(false);
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testEmptyDataBatch(DataBatch dataBatch) {
        assertTrue("List is not empty.", dataBatch.getDataList().isEmpty());
        testDataTypes(dataBatch);
    }

    private void testDataBatch(DataBatch dataBatch) {
        assertFalse("List is empty.", dataBatch.getDataList().isEmpty());
        testDataTypes(dataBatch);
    }

    private void testDataTypes(DataBatch dataBatch) {
        assertTrue("Data type is wrong.", expectedType == dataBatch.getType());
        assertTrue("Data subtype is wrong.", expectedSubType == dataBatch.getSubType());
    }

    private void testData(long timestamp, int subType, float value, int index) {
        assertTrue("Timestamp " + index + " is not correct", expectedTimestamps.get(index) == timestamp);
        assertTrue("ClassificationData " + index + " has not the correct classificationType.", expectedSubType == subType);
        assertTrue("Value in element " + index + " is not correct.", expectedValues[index] == value);
    }

    private void testData(long timestamp, int subType, float[] values, int index) {
        assertTrue("Timestamp " + index + " is not correct", expectedTimestamps.get(index) == timestamp);
        assertTrue("ClassificationData " + index + " has not the correct classificationType.", expectedSubType == subType);
        for (int j = 0; j < expectedValueArrays.length; j++) {
            assertTrue("Values in element " + index + " are not correct.", expectedValueArrays[index][j] == values[j]);
        }
    }
    */
}
