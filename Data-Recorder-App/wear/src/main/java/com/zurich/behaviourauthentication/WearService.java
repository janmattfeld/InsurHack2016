package com.zurich.behaviourauthentication;

import android.util.Log;

import com.zurich.authenticator.service.AuthenticationService;

public class WearService extends AuthenticationService {

    private static final String TAG = WearService.class.getSimpleName();

    public WearService() {
        Log.d(TAG, "WearService() called");
    }

    @Override
    protected void setupDataManagers() {
        // only setup the sensor event aggregator for now
        setupSensorEventManager();
    }

}
