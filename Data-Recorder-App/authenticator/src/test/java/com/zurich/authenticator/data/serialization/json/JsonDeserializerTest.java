package com.zurich.authenticator.data.serialization.json;

import com.zurich.authenticator.data.recorder.Record;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonDeserializerTest {

    @Test
    public void deserializeRecord_validJson_validRecord() throws Exception {
        Record expected = JsonSerializerTest.createDummySensorDataRecord();
        String json = JsonSerializer.serialize(expected);
        Record actual = JsonDeserializer.deserializeRecord(json);

        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getLabel(), actual.getLabel());
        assertEquals(expected.getComment(), actual.getComment());
        assertEquals(expected.getDeviceName(), actual.getDeviceName());
        assertEquals(expected.getDeviceId(), actual.getDeviceId());

        assertNotNull(actual.getRecorder());
        assertEquals(expected.getRecorder().getStartTimestamp(), actual.getRecorder().getStartTimestamp());
        assertEquals(expected.getRecorder().getStopTimestamp(), actual.getRecorder().getStopTimestamp());

        assertNotNull(actual.getDataBatches());
        assertEquals(expected.getDataBatches().size(), actual.getDataBatches().size());
        assertEquals(expected.getDataBatches().get(0).getType(), actual.getDataBatches().get(0).getType());
        assertEquals(expected.getDataBatches().get(0).getDataList().size(), actual.getDataBatches().get(0).getDataList().size());
        assertEquals(expected.getDataBatches().get(0).getDataList().get(0).toString(), actual.getDataBatches().get(0).getDataList().get(0).toString());
    }

}