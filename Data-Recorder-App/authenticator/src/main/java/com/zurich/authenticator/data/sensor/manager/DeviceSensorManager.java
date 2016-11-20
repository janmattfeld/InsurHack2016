package com.zurich.authenticator.data.sensor.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import com.zurich.authenticator.data.sensor.SensorUnavailableException;
import com.zurich.authenticator.util.logging.Logger;

public final class DeviceSensorManager {

    private static final String TAG = DeviceSensorManager.class.getSimpleName();

    private static DeviceSensorManager instance;
    private static final int SENSOR_DELAY_DEFAULT = SensorManager.SENSOR_DELAY_UI;

    private SensorManager sensorManager;

    private DeviceSensorManager() {

    }

    public static DeviceSensorManager getInstance() {
        if (instance == null) {
            instance = new DeviceSensorManager();
        }
        return instance;
    }

    public static void initialize(Context context) {
        Logger.d(TAG, "initializeManager() called with: context = [" + context + "]");
        DeviceSensorManager instance = getInstance();

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        instance.setSensorManager(sensorManager);
    }

    public static boolean hasSensor(int sensorType) {
        try {
            getSensor(sensorType);
            return true;
        } catch (SensorUnavailableException exception) {
            return false;
        }
    }

    public static Sensor getSensor(int type) throws SensorUnavailableException {
        SensorManager sensorManager = getInitializedSensorManager();
        Sensor sensor = sensorManager.getDefaultSensor(type);
        if (sensor == null) {
            throw new SensorUnavailableException("No default sensor available for dataType " + type);
        }
        return sensor;
    }

    public static SensorManager getInitializedSensorManager() throws IllegalStateException {
        DeviceSensorManager instance = getInstance();
        if (instance.getSensorManager() == null) {
            throw new IllegalStateException("Sensor manager has not been initialized");
        }
        return instance.getSensorManager();
    }

    public static void registerSensorEventListener(int type, SensorEventListener listener, Handler handler) throws SensorUnavailableException {
        Sensor sensor = getSensor(type);
        getInitializedSensorManager().registerListener(listener, sensor, SENSOR_DELAY_DEFAULT, handler);
        Logger.i(TAG, "Registered sensor listener for: " + sensor.getName() + " (dataType " + sensor.getType() + ")");
    }

    public static void unregisterSensorEventListener(int type, SensorEventListener listener) throws SensorUnavailableException {
        Sensor sensor = getSensor(type);
        getInitializedSensorManager().unregisterListener(listener, sensor);
        Logger.i(TAG, "Unregistered sensor listener for: " + sensor.getName() + " (dataType " + sensor.getType() + ")");
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public void setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }
}
