package com.zurich.authenticator.data.serialization.json;

import android.hardware.Sensor;

import com.zurich.authenticator.data.DummyData.DummyDataBatch;
import com.zurich.authenticator.data.DummyData.DummyDataBuilder;
import com.zurich.authenticator.data.DummyData.DummyDataException;
import com.zurich.authenticator.data.DummyData.DummyDataManipulator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.recorder.DataRecorder;
import com.zurich.authenticator.data.recorder.Record;
import com.zurich.authenticator.data.recorder.SensorDataRecorder;
import com.zurich.authenticator.data.sensor.SensorEventData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

public class JsonSerializerTest {

    private Record record = createDummySensorDataRecord();

    @Test
    public void serialize_validRecord_validJson() throws Exception {
        String actual = JsonSerializer.serialize(record);
        String expected = "{\n" +
                "  \"user\": \"User\",\n" +
                "  \"label\": \"Label\",\n" +
                "  \"comment\": \"Comment\",\n" +
                "  \"deviceName\": \"Device Name\",\n" +
                "  \"deviceId\": \"Device ID\",\n" +
                "  \"version\": 1,\n" +
                "  \"recorder\": {\n" +
                "    \"sensorTypes\": [\n" +
                "      1,\n" +
                "      2,\n" +
                "      4,\n" +
                "      5,\n" +
                "      6,\n" +
                "      8,\n" +
                "      9,\n" +
                "      10,\n" +
                "      11,\n" +
                "      12,\n" +
                "      13\n" +
                "    ],\n" +
                "    \"startTimestamp\": 2592000000,\n" +
                "    \"stopTimestamp\": 2592005000\n" +
                "  },\n" +
                "  \"dataBatches\": [\n" +
                "    {\n" +
                "      \"dataList\": [\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            0.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592000000\n" +
                "        },\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            1.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592001000\n" +
                "        },\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            2.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592002000\n" +
                "        },\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            3.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592003000\n" +
                "        },\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            4.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592004000\n" +
                "        }\n" +
                "      ],\n" +
                "      \"type\": 100,\n" +
                "      \"subType\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"dataList\": [\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            0.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592000000\n" +
                "        },\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            9.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592001000\n" +
                "        },\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            18.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592002000\n" +
                "        },\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            27.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592003000\n" +
                "        },\n" +
                "        {\n" +
                "          \"values\": [\n" +
                "            36.0\n" +
                "          ],\n" +
                "          \"timestamp\": 2592004000\n" +
                "        }\n" +
                "      ],\n" +
                "      \"type\": 100,\n" +
                "      \"subType\": 9\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        assertEquals("JSON not as expected", expected, actual);
    }

    public static Record createDummySensorDataRecord() {
        List<DataBatch> dataBatches = new ArrayList<>();
        dataBatches.add(createDummySensorDataBatch(Sensor.TYPE_ACCELEROMETER));
        dataBatches.add(createDummySensorDataBatch(Sensor.TYPE_GRAVITY));

        Record record = new Record();
        record.setUser("User");
        record.setLabel("Label");
        record.setComment("Comment");
        record.setDeviceName("Device Name");
        record.setDeviceId("Device ID");
        record.setDataBatches(dataBatches);
        record.setRecorder(createDummySensorEventRecorder());

        return record;
    }

    public static DataRecorder createDummySensorEventRecorder() {
        SensorDataRecorder recorder = new SensorDataRecorder();
        recorder.setStartTimestamp(TimeUnit.DAYS.toMillis(30));
        recorder.setStopTimestamp(recorder.getStartTimestamp() + (5 * TimeUnit.SECONDS.toMillis(1)));
        recorder.setSensorTypes(SensorDataRecorder.getAllSensorTypes());
        return recorder;
    }

    public static DataBatch createDummySensorDataBatch(final int subType) {
        try {
            DummyDataBatch dummyDataBatch = DummyDataBuilder.forSensorEventData()
                    .withSubType(subType)
                    .withItemCount(5)
                    .withStartTimestamp(TimeUnit.DAYS.toMillis(30))
                    .withTimestampDelta(TimeUnit.SECONDS.toMillis(1))
                    .withDummyDataManipulator(new DummyDataManipulator<SensorEventData>() {
                        @Override
                        public SensorEventData manipulateData(int index, SensorEventData data) {
                            data.setValues(new float[]{subType * index});
                            return data;
                        }
                    })
                    .build();
            return dummyDataBatch.getDataBatch();
        } catch (DummyDataException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DataBatch createDummyFeatureDataBatch(final int subType) {
        try {
            DummyDataBatch dummyDataBatch = DummyDataBuilder.forFeatureData()
                    .withSubType(subType)
                    .withItemCount(5)
                    .withStartTimestamp(TimeUnit.DAYS.toMillis(30))
                    .withTimestampDelta(TimeUnit.SECONDS.toMillis(1))
                    .withDummyDataManipulator(new DummyDataManipulator<FeatureData>() {
                        @Override
                        public FeatureData manipulateData(int index, FeatureData data) {
                            data.setValues(new float[]{subType + index});
                            return data;
                        }
                    })
                    .build();
            return dummyDataBatch.getDataBatch();
        } catch (DummyDataException e) {
            e.printStackTrace();
            return null;
        }
    }

}