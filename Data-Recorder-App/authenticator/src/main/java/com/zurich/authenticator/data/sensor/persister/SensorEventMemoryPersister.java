package com.zurich.authenticator.data.sensor.persister;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.memory.MultiTypeMemoryPersister;
import com.zurich.authenticator.data.sensor.SensorEventData;

public class SensorEventMemoryPersister extends MultiTypeMemoryPersister {

    public SensorEventMemoryPersister() {
        super(Data.TYPE_SENSOR_EVENT);
    }

    public SensorEventMemoryPersister(int capacity) {
        super(Data.TYPE_SENSOR_EVENT, capacity);
    }

    @Override
    public int getDataType(Data data) {
        return ((SensorEventData) data).getSensorType();
    }

}
