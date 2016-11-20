package com.zurich.authenticator.data.sensor.aggregator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.HandlerThread;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.SensorUnavailableException;
import com.zurich.authenticator.data.sensor.manager.DeviceSensorManager;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class SensorEventAggregator extends DataAggregator<SensorEventData> implements SensorEventListener {

    private static final String TAG = SensorEventAggregator.class.getSimpleName();

    private Handler aggregationHandler;
    private HandlerThread aggregationThread;

    private List<Integer> requestedSensorTypes = new ArrayList<>();
    private List<Integer> registeredSensorTypes = new ArrayList<>();

    public SensorEventAggregator() {
        this(DataAggregator.TYPE_DEFAULT);
    }

    public SensorEventAggregator(int aggregatorType) {
        super(Data.TYPE_SENSOR_EVENT, aggregatorType);
    }

    @Override
    public void startAggregation() {
        aggregationThread = new HandlerThread(this.toString());
        aggregationThread.start();
        aggregationHandler = new Handler(aggregationThread.getLooper());
        registerRequestedSensors();
        isAggregating = true;
    }

    @Override
    public SensorEventData aggregate() throws DataAggregationException {
        // Data will be aggregated through the onSensorChanged callback
        // method from the SensorEventListener interface
        return null;
    }

    @Override
    public void stopAggregation() {
        unregisterRequestedSensors();
        aggregationThread.quitSafely();
        aggregationThread = null;
        isAggregating = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorEventData sensorEventData = new SensorEventData(event);
        onDataAggregated(sensorEventData, this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // we don't care
    }

    public void registerRequestedSensors() {
        for (Integer requestedSensorType : requestedSensorTypes) {
            registerSensor(requestedSensorType);
        }
    }

    public void unregisterRequestedSensors() {
        for (Integer requestedSensorType : requestedSensorTypes) {
            unregisterSensor(requestedSensorType);
        }
    }

    protected void registerSensor(int type) {
        try {
            DeviceSensorManager.registerSensorEventListener(type, this, aggregationHandler);
            registeredSensorTypes.add(type);
        } catch (SensorUnavailableException exception) {
            Logger.e(TAG, "Unable to register listener for requested sensor:", exception);
        }
    }

    protected void unregisterSensor(int type) {
        try {
            DeviceSensorManager.unregisterSensorEventListener(type, this);
            registeredSensorTypes.remove(registeredSensorTypes.indexOf(type));
        } catch (SensorUnavailableException exception) {
            Logger.e(TAG, "Unable to unregister listener for requested sensor:", exception);
        }
    }

    public void startRequestingSensor(int type) {
        if (!requestedSensorTypes.contains(type)) {
            requestedSensorTypes.add(type);
        }
        if (registeredSensorTypes.contains(type)) {
            return;
        }
        registerSensor(type);
    }

    public void stopRequestingSensor(int type) {
        if (requestedSensorTypes.contains(type)) {
            requestedSensorTypes.remove(requestedSensorTypes.indexOf(type));
        }
        if (!registeredSensorTypes.contains(type)) {
            return;
        }
        unregisterSensor(type);
    }

    public List<Integer> getRequestedSensorTypes() {
        return requestedSensorTypes;
    }

    public void setRequestedSensorTypes(List<Integer> requestedSensorTypes) {
        this.requestedSensorTypes = requestedSensorTypes;
    }

    public List<Integer> getRegisteredSensorTypes() {
        return registeredSensorTypes;
    }

    public void setRegisteredSensorTypes(List<Integer> registeredSensorTypes) {
        this.registeredSensorTypes = registeredSensorTypes;
    }

}
