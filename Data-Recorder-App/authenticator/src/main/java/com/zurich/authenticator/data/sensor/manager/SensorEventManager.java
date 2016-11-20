package com.zurich.authenticator.data.sensor.manager;

import android.content.Context;
import android.hardware.Sensor;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.DataAggregationObserver;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.manager.DataManager;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.PersisterManager;
import com.zurich.authenticator.data.persister.memory.MemoryPersister;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.SensorEventUnavailableException;
import com.zurich.authenticator.data.sensor.aggregator.SensorEventAggregator;
import com.zurich.authenticator.data.sensor.aggregator.SensorEventAggregators;
import com.zurich.authenticator.data.sensor.persister.SensorEventMemoryPersister;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SensorEventManager extends DataManager {

    private static final String TAG = SensorEventManager.class.getSimpleName();

    private static SensorEventManager instance;

    private static final Map<Integer, String> readableSensorTypes = getReadableSensorTypes();

    private SensorEventMemoryPersister sensorEventMemoryPersister;

    private SensorEventManager() {

    }

    public static SensorEventManager getInstance() {
        if (instance == null) {
            instance = new SensorEventManager();
        }
        return instance;
    }

    public static void initialize(Context context) {
        getInstance().initializeManager(context);
    }

    @Override
    public void setupDataAggregators(Context context) throws DataAggregationException {
        List<SensorEventAggregator> sensorEventAggregators = getDefaultDataAggregators(context);

        for (SensorEventAggregator aggregator : sensorEventAggregators) {
            aggregator.addAggregationObserver(new DataAggregationObserver<SensorEventData>() {
                @Override
                public void onDataAggregated(SensorEventData data, DataAggregator dataAggregator) {
                    try {
                        PersisterManager.persist(data, PersisterManager.STRATEGY_MEMORY);
                    } catch (DataPersistingException e) {
                        Logger.e(TAG, "onDataAggregated: ", e);
                    }
                }
            });
            aggregator.startAggregation();
            SensorEventAggregators.getInstance().addDataAggregator(aggregator);
        }
    }

    @Override
    public List<SensorEventAggregator> getDefaultDataAggregators(Context context) {
        List<SensorEventAggregator> aggregators = new ArrayList<>();
        aggregators.add(new SensorEventAggregator());
        // Add new aggregators here
        return aggregators;
    }

    public static float[][] getValuesFromSensorEventData(List<SensorEventData> sensorEventDataList) {
        float[][] values = new float[sensorEventDataList.size()][];
        for (int i = 0; i < sensorEventDataList.size(); i++) {
            values[i] = sensorEventDataList.get(i).getValues();
        }
        return values;
    }

    public static List<SensorEventData> getSensorEventDataSince(long timestamp, int sensoryType) throws SensorEventUnavailableException {
        try {
            DataBatch<SensorEventData> sensorEventDataDataBatch = getMemoryDataBatch(sensoryType);
            List<SensorEventData> sensorEventDataList = sensorEventDataDataBatch.getDataSince(timestamp);
            if (sensorEventDataList.size() == 0) {
                throw new SensorEventUnavailableException("No sensor events available since " + timestamp + " with type " + sensoryType);
            }
            return sensorEventDataList;
        } catch (DataPersistingException e) {
            throw new SensorEventUnavailableException("No persisted sensor events available with type " + sensoryType);
        }
    }

    public static List<SensorEventData> getSensorEventDataSince(long timestamp, DataBatch<SensorEventData> sensorEventDataBatch) throws SensorEventUnavailableException {
        List<SensorEventData> sensorEventDataList = sensorEventDataBatch.getDataSince(timestamp);
        if (sensorEventDataList.size() == 0) {
            throw new SensorEventUnavailableException("No sensor events available since " + timestamp);
        }
        return sensorEventDataList;
    }

    public static SensorEventMemoryPersister getMemoryPersister() throws DataPersistingException {
        SensorEventManager instance = getInstance();
        if (instance.sensorEventMemoryPersister == null) {
            MemoryPersister memoryPersister = (MemoryPersister) PersisterManager.getDataPersister(PersisterManager.STRATEGY_MEMORY);
            instance.sensorEventMemoryPersister = (SensorEventMemoryPersister) memoryPersister.getPersister(Data.TYPE_SENSOR_EVENT);
        }
        return instance.sensorEventMemoryPersister;
    }

    public static DataBatch<SensorEventData> getMemoryDataBatch(int sensorType) throws DataPersistingException {
        return getMemoryPersister().getDataBatch(sensorType);
    }

    public static String getReadableSensorType(int type) {
        if (!readableSensorTypes.containsKey(type)) {
            return "Unknown Sensor";
        }
        return readableSensorTypes.get(type);
    }

    private static Map<Integer, String> getReadableSensorTypes() {
        if (readableSensorTypes != null) {
            return readableSensorTypes;
        }
        Map<Integer, String> readableSensorTypes = new HashMap<>();
        readableSensorTypes.put(Sensor.TYPE_ACCELEROMETER, "Accelerometer");
        readableSensorTypes.put(Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature");
        readableSensorTypes.put(Sensor.TYPE_GAME_ROTATION_VECTOR, "Game Rotation Vector");
        readableSensorTypes.put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "Geomagnetic Rotation Vector");
        readableSensorTypes.put(Sensor.TYPE_GRAVITY, "Gravity");
        readableSensorTypes.put(Sensor.TYPE_GYROSCOPE, "Gyroscope");
        readableSensorTypes.put(Sensor.TYPE_HEART_RATE, "Heart Rate");
        readableSensorTypes.put(Sensor.TYPE_LIGHT, "Light");
        readableSensorTypes.put(Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration");
        readableSensorTypes.put(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic Field");
        readableSensorTypes.put(Sensor.TYPE_PRESSURE, "Pressure");
        readableSensorTypes.put(Sensor.TYPE_PROXIMITY, "Proximity");
        readableSensorTypes.put(Sensor.TYPE_RELATIVE_HUMIDITY, "Humidity");
        readableSensorTypes.put(Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector");
        return readableSensorTypes;
    }

}
