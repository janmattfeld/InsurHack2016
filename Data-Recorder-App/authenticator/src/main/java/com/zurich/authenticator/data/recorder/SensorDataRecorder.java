package com.zurich.authenticator.data.recorder;

import android.hardware.Sensor;
import android.os.Bundle;

import com.google.gson.annotations.Expose;
import com.zurich.authenticator.data.aggregation.DataAggregationObserver;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.persister.DataPersister;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.database.DataBasePersister;
import com.zurich.authenticator.data.persister.database.DataBaseQueryBuilder;
import com.zurich.authenticator.data.persister.database.SqlDataBaseHelper;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.aggregator.SensorEventAggregator;
import com.zurich.authenticator.data.sensor.manager.DeviceSensorManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorDataRecorder extends DataRecorder<SensorEventData> implements DataAggregationObserver<SensorEventData> {

    private transient SensorEventAggregator sensorEventAggregator;
    @Expose
    private List<Integer> sensorTypes = new ArrayList<>();

    public SensorDataRecorder() {
    }

    public SensorDataRecorder(DataRecorderBuilder dataRecorderBuilder) {
        super(dataRecorderBuilder);
        if (dataRecorderBuilder.shouldUseAllAvailableTypes()) {
            sensorTypes = getAllAvailableSensorTypes();
        } else {
            sensorTypes = dataRecorderBuilder.getTypes();
        }
        setupAutoStart();
    }

    @Override
    public void startAggregators() {
        sensorEventAggregator = new SensorEventAggregator(DataAggregator.TYPE_RECORDER);
        sensorEventAggregator.setRequestedSensorTypes(sensorTypes);
        sensorEventAggregator.addAggregationObserver(this);
        sensorEventAggregator.startAggregation();
    }

    @Override
    public void stopAggregators() {
        sensorEventAggregator.stopAggregation();
        sensorEventAggregator = null;
    }

    @Override
    public Record toRecord(Bundle configuration) throws RecorderException {
        List<DataBatch> dataBatchList;
        try {
            dataBatchList = new DataBaseQueryBuilder((DataBasePersister) dataPersister)
                    .forSensorEvents()
                    .withTableTag(SqlDataBaseHelper.TAG_RECORD)
                    .getDataBetween(startTimestamp, stopTimestamp)
                    .asDataBatches();
        } catch (DataPersistingException ex) {
            throw new RecorderException("Could not query Data: " + ex.getMessage());
        }
        Record record = new RecordBuilder(this)
                .withData(dataBatchList)
                .fromUser(configuration.getString(Record.KEY_USER_NAME))
                .withLabel(configuration.getString(Record.KEY_LABEL))
                .withComment(configuration.getString(Record.KEY_COMMENT))
                .build();
        return record;
    }

    @Override
    public DataPersister getDataPersister() {
        if (dataPersister == null) {
            dataPersister = new DataBasePersister(context, SqlDataBaseHelper.TAG_RECORD);
        }
        return dataPersister;
    }

    public static List<Integer> getAllAvailableSensorTypes() {
        List<Integer> availableSensorTypes = new ArrayList<>();
        List<Integer> allSensorTypes = getAllSensorTypes();
        for (Integer sensorType : allSensorTypes) {
            if (DeviceSensorManager.hasSensor(sensorType)) {
                availableSensorTypes.add(sensorType);
            }
        }
        return availableSensorTypes;
    }

    public static List<Integer> getAllSensorTypes() {
        return Arrays.asList(
                Sensor.TYPE_ACCELEROMETER,
                Sensor.TYPE_MAGNETIC_FIELD,
                Sensor.TYPE_GYROSCOPE,
                Sensor.TYPE_LIGHT,
                Sensor.TYPE_PRESSURE,
                Sensor.TYPE_PROXIMITY,
                Sensor.TYPE_GRAVITY,
                Sensor.TYPE_LINEAR_ACCELERATION,
                Sensor.TYPE_ROTATION_VECTOR,
                Sensor.TYPE_RELATIVE_HUMIDITY,
                Sensor.TYPE_AMBIENT_TEMPERATURE
        );
    }

    public List<Integer> getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(List<Integer> sensorTypes) {
        this.sensorTypes = sensorTypes;
    }
}
