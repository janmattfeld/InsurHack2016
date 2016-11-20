package com.zurich.behaviourauthentication;

import android.content.Intent;
import android.util.Log;

import com.zurich.authenticator.AuthenticationApplication;
import com.zurich.authenticator.service.ApplicationConnection;

public class WearApplication extends AuthenticationApplication {

    private static final String TAG = WearApplication.class.getSimpleName();

    public WearApplication() {
        Log.d(TAG, "WearApplication() called");
    }

    @Override
    protected void bindServices() {
        Log.d(TAG, "bindServices() called");

        // authentication service
        Intent authenticationServiceIntent = new Intent(this, WearService.class);
        authenticationServiceIntent.putExtra(ApplicationConnection.KEY_MESSENGER, applicationMessenger);
        serviceConnection.bind(this, authenticationServiceIntent);
    }

    @Override
    protected void unbindServices() {
        Log.d(TAG, "unbindServices() called");
        serviceConnection.unBind(this);
    }

}
