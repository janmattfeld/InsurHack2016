package com.zurich.behaviourauthentication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zurich.authenticator.service.message.MessageBuilder;

public class PhoneActivity extends AppCompatActivity {

    private static final String TAG = PhoneActivity.class.getSimpleName();

    private PhoneApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");

        app = (PhoneApplication) getApplication();
        setupUi();
    }

    private void setupUi() {
        Button startServicesButton = (Button) findViewById(R.id.startServicesButton);
        startServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    app.getServiceConnection().sendMessage(new MessageBuilder()
                            .withId(MessageBuilder.ID_START_SERVICE)
                            .build());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        Button stopServicesButton = (Button) findViewById(R.id.stopServicesButton);
        stopServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    app.getServiceConnection().sendMessage(new MessageBuilder()
                            .withId(MessageBuilder.ID_STOP_SERVICE)
                            .build());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        Button recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecordingDialog();
            }
        });

        Button debugButton1 = (Button) findViewById(R.id.debugButton1);
        debugButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        Button debugButton2 = (Button) findViewById(R.id.debugButton2);
        debugButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Debug stuff
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

    private void showRecordingDialog() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // remove previous dialog
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag(RecordingFragment.TAG);
        if (previousFragment != null) {
            transaction.remove(previousFragment);
        }
        transaction.addToBackStack(null);

        // show new dialog
        DialogFragment newFragment = new RecordingFragment();
        newFragment.show(transaction, RecordingFragment.TAG);
    }

}
