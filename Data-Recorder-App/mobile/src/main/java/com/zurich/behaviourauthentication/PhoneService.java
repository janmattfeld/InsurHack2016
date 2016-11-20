package com.zurich.behaviourauthentication;

import android.hardware.Sensor;
import android.os.Message;
import android.util.Log;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.sensor.aggregator.SensorEventAggregator;
import com.zurich.authenticator.data.sensor.aggregator.SensorEventAggregators;
import com.zurich.authenticator.service.AuthenticationService;
import com.zurich.authenticator.service.message.MessageBuilder;

import java.util.Arrays;
import java.util.List;

public class PhoneService extends AuthenticationService {

    private static final String TAG = PhoneService.class.getSimpleName();

    public PhoneService() {
        Log.d(TAG, "PhoneService() called");
    }

    @Override
    protected void initialize() {
        super.initialize();
        startLoggingPersistedData();
    }

    @Override
    protected void setupSensorEventManager() {
        super.setupSensorEventManager();
        try {
            List<Integer> sensorTypes = Arrays.asList(
                    Sensor.TYPE_ACCELEROMETER,
                    Sensor.TYPE_LIGHT,
                    Sensor.TYPE_MAGNETIC_FIELD);

            SensorEventAggregator sensorEventAggregator = SensorEventAggregators.getDefaultAggregator();
            sensorEventAggregator.setRequestedSensorTypes(sensorTypes);
            sensorEventAggregator.registerRequestedSensors();
        } catch (DataAggregationException ex) {
            Log.e(TAG, "setupSensorEventManager: ", ex);
        }
    }

    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        switch (message.what) {
            case MessageBuilder.ID_START_RECORDING: {
                // intended fall-through to next case
            }
            case MessageBuilder.ID_STOP_RECORDING: {
                // forward to wearable
                Message forward = new MessageBuilder()
                        .withId(message.what)
                        .withData(message.getData())
                        .build();
                googleApiConnection.sendMessageToNearbyNodes(forward);
                break;
            }
        }
    }


}
