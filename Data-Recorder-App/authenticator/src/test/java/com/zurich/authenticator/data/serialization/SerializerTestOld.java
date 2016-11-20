package com.zurich.authenticator.data.serialization;

import com.zurich.authenticator.data.generic.Data;

public class SerializerTestOld<T extends Data> extends SerializationTestOld {

    /*
    private JsonSerializer<FeatureData> jsonFeatureDataSerializer;
    private JsonSerializer<SensorEventData> jsonSensorDataSerializer;
    private JsonSerializer<StateData> jsonStateDataSerializer;
    private JsonSerializer<ClassificationData> jsonClassificationDataSerializer;

    private static long[] expectedTimestamps;
    private int itemCount = 3;

    @Before
    public void setUp() throws Exception {
        jsonFeatureDataSerializer = JsonSerializer.getInstance();
        jsonSensorDataSerializer = JsonSerializer.getInstance();
        jsonStateDataSerializer = JsonSerializer.getInstance();
        jsonClassificationDataSerializer = JsonSerializer.getInstance();
    }

    private static DataBatch<SensorEventData> createDummySensorDataBatch(int itemCount) throws DummyDataException {
        expectedTimestamps = new long[itemCount];
        SensorEventDummyDataBatch sensorEventDummyDataBatch = (SensorEventDummyDataBatch) DummyDataBuilder.forSensorEventData()
                .withSubType(Sensor.TYPE_ACCELEROMETER)
                .build();
        DataBatch<SensorEventData> dataBatch = sensorEventDummyDataBatch.getDataBatch();
        for (int i = 0; i < itemCount; i++) {
            expectedTimestamps[i] = dataBatch.getDataList().get(i).getTimestamp();
        }
        return dataBatch;
    }

    private static DataBatch<StateData> createDummyStateDataBatch(int itemCount) throws DummyDataException {
        expectedTimestamps = new long[itemCount];
        StateDummyDataBatch stateDummyDataBatch = (StateDummyDataBatch) DummyDataBuilder.forStateData()
                .withSubType(StateManager.STATE_DISPLAY_ON)
                .build();
        DataBatch<StateData> dataBatch = stateDummyDataBatch.getDataBatch();
        for (int i = 0; i < itemCount; i++) {
            expectedTimestamps[i] = dataBatch.getDataList().get(i).getTimestamp();
        }
        return dataBatch;
    }

    private static DataBatch<FeatureData> createDummyFeatureDataBatch(int itemCount) throws DummyDataException {
        expectedTimestamps = new long[itemCount];
        FeatureDummyDataBatch featureDummyDataBatch = (FeatureDummyDataBatch) DummyDataBuilder.forFeatureData()
                .withSubType(FeatureManager.TYPE_VARIANCE)
                .build();
        DataBatch<FeatureData> dataBatch = featureDummyDataBatch.getDataBatch();
        for (int i = 0; i < itemCount; i++) {
            expectedTimestamps[i] = dataBatch.getDataList().get(i).getTimestamp();
        }
        return dataBatch;
    }

    private static DataBatch<ClassificationData> createDummyClassificationDataBatch(int itemCount) throws DummyDataException {
        expectedTimestamps = new long[itemCount];
        ClassificationDummyDataBatch classificationDummyDataBatch = (ClassificationDummyDataBatch) DummyDataBuilder.forClassificationData()
                .withSubType(ClassificationManager.CLASSIFICATION_WALKING)
                .build();
        DataBatch<ClassificationData> dataBatch = classificationDummyDataBatch.getDataBatch();
        for (int i = 0; i < itemCount; i++) {
            expectedTimestamps[i] = dataBatch.getDataList().get(i).getTimestamp();
        }
        return dataBatch;
    }


    @Test
    public void serialize_emptyList_createsCorrectJson() throws Exception {
        DataBatch<FeatureData> dataBatch = new DataBatch<>();
        String json = jsonFeatureDataSerializer.serialize(dataBatch);
        List<FeatureData> featureDataList = new ArrayList<>();
        String expected = createJsonString(dataBatch.getType(), dataBatch.getSubType(), dataBatch.getTimestamps(), FeatureManager.getValuesFromFeatureData(featureDataList));
        testJson(expected, json);
    }

    @Test
    public void serialize_filledSensorList_createsCorrectJson() throws Exception {
        DataBatch<SensorEventData> sensorEventDataBatch = createDummySensorDataBatch(itemCount);
        String json = jsonSensorDataSerializer.serialize(sensorEventDataBatch);
        List<SensorEventData> sensorEventDataList = SensorEventManager.getSensorEventDataSince(0, sensorEventDataBatch);
        String expected = createJsonString(sensorEventDataBatch.getType(), sensorEventDataBatch.getSubType(), sensorEventDataBatch.getTimestamps(), SensorEventManager.getValuesFromSensorEventData(sensorEventDataList));
        testJson(expected, json);
    }

    @Test
    public void serialize_filledStateList_createsCorrectJson() throws Exception {
        DataBatch<StateData> stateDataBatch = createDummyStateDataBatch(itemCount);
        String json = jsonStateDataSerializer.serialize(stateDataBatch);
        List<StateData> stateDataList = StateManager.getStateDataSince(0, stateDataBatch);
        String expected = createJsonString(stateDataBatch.getType(), stateDataBatch.getSubType(), stateDataBatch.getTimestamps(), StateManager.getValuesFromStateData(stateDataList));
        testJson(expected, json);
    }

    @Test
    public void serialize_filledFeatureList_createsCorrectJson() throws Exception {
        DataBatch<FeatureData> featureDataBatch = createDummyFeatureDataBatch(itemCount);
        String json = jsonFeatureDataSerializer.serialize(featureDataBatch);
        List<FeatureData> featureDataList = FeatureManager.getFeatureDataSince(0, featureDataBatch);
        String expected = createJsonString(featureDataBatch.getType(), featureDataBatch.getSubType(), featureDataBatch.getTimestamps(), FeatureManager.getValuesFromFeatureData(featureDataList));
        testJson(expected, json);
    }

    @Test
    public void serialize_filledClassificationList_createsCorrectJson() throws Exception {
        DataBatch<ClassificationData> classificationDataBatch = createDummyClassificationDataBatch(itemCount);
        String json = jsonClassificationDataSerializer.serialize(classificationDataBatch);
        List<ClassificationData> classificationDataList = ClassificationManager.getClassificationDataSince(0, classificationDataBatch);
        String expected = createJsonString(classificationDataBatch.getType(), classificationDataBatch.getSubType(), classificationDataBatch.getTimestamps(), ClassificationManager.getValuesFromClassificationData(classificationDataList));
        testJson(expected, json);
    }

    @Test
    public void serialize_nullObject_createsCorrectJson() throws Exception {
        String json = jsonClassificationDataSerializer.serialize(null);
        assertTrue("Incorrect json created.", json.equals("null"));
    }

    private void testJson(String expected, String actual) {
        assertFalse("Json is empty", actual.isEmpty());
        assertTrue("Json was incorrect", expected.equals(actual));
    }
    */

}
