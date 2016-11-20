package com.zurich.behaviourauthentication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zurich.authenticator.service.message.MessageBuilder;

public class WearActivity extends WearableActivity {

    private static final String TAG = WearActivity.class.getSimpleName();

    private WearApplication app;

    private BoxInsetLayout containerView;
    private TextView debugTextView;
    private Button debugButton1;
    private Button debugButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        setAmbientEnabled();
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");

        app = (WearApplication) getApplication();
        setupUi();
    }

    private void setupUi() {
        containerView = (BoxInsetLayout) findViewById(R.id.container);
        debugTextView = (TextView) findViewById(R.id.debugText);
        debugButton1 = (Button) findViewById(R.id.debugButton1);
        debugButton2 = (Button) findViewById(R.id.debugButton2);

        debugButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    app.getServiceConnection().sendMessage(new MessageBuilder()
                            .withId(MessageBuilder.ID_ECHO)
                            .build());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            containerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            debugTextView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            containerView.setBackground(null);
            debugTextView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}
