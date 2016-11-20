package com.zurich.behaviourauthentication;

import android.content.Intent;
import android.util.Log;

import com.zurich.authenticator.AuthenticationApplication;
import com.zurich.authenticator.service.ApplicationConnection;

public class PhoneApplication extends AuthenticationApplication {

    private static final String TAG = PhoneApplication.class.getSimpleName();

    public PhoneApplication() {
        Log.d(TAG, "PhoneApplication() called");
    }

    @Override
    protected void bindServices() {
        Log.d(TAG, "bindServices() called");

        // authentication service
        Intent serviceIntent = new Intent(this, PhoneService.class);
        serviceIntent.putExtra(ApplicationConnection.KEY_MESSENGER, applicationMessenger);
        serviceConnection.bind(this, serviceIntent);
    }

    @Override
    protected void unbindServices() {
        Log.d(TAG, "unbindServices() called");
        serviceConnection.unBind(this);
    }

}
