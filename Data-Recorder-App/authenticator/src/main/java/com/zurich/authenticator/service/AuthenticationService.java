package com.zurich.authenticator.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.zurich.authenticator.data.classification.aggregator.ClassificationAggregators;
import com.zurich.authenticator.data.classification.manager.ClassificationManager;
import com.zurich.authenticator.data.feature.aggregator.FeatureAggregators;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.PersisterManager;
import com.zurich.authenticator.data.recorder.DataRecorder;
import com.zurich.authenticator.data.recorder.DataRecorderBuilder;
import com.zurich.authenticator.data.recorder.Record;
import com.zurich.authenticator.data.recorder.RecorderException;
import com.zurich.authenticator.data.recorder.RecordingObserver;
import com.zurich.authenticator.data.recorder.SensorDataRecorder;
import com.zurich.authenticator.data.sensor.aggregator.SensorEventAggregators;
import com.zurich.authenticator.data.sensor.manager.DeviceSensorManager;
import com.zurich.authenticator.data.sensor.manager.SensorEventManager;
import com.zurich.authenticator.data.state.aggregator.StateAggregators;
import com.zurich.authenticator.data.state.manager.StateManager;
import com.zurich.authenticator.data.trustlevel.aggregator.TrustLevelAggregators;
import com.zurich.authenticator.data.trustlevel.manager.TrustLevelManager;
import com.zurich.authenticator.service.message.MessageBuilder;
import com.zurich.authenticator.util.logging.Logger;

import java.util.concurrent.TimeUnit;

public abstract class AuthenticationService extends Service implements MessageApi.MessageListener {

    private static final String TAG = AuthenticationService.class.getSimpleName();

    protected ApplicationConnection applicationConnection;
    protected GoogleApiConnection googleApiConnection;
    protected final Messenger serviceMessenger = createMessenger();
    protected PowerManager.WakeLock wakeLock;

    protected DataRecorder recorder;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        acquireWakeLock();
    }

    protected void initialize() {
        setupLogging();
        PersisterManager.initialize(this);
        setupDataManagers();
        applicationConnection = new ApplicationConnection();
        googleApiConnection = new GoogleApiConnection(this);
    }

    public static void setupLogging() {
        Logger.setLogCatEnabled(true);
        Logger.setConsoleEnabled(false);
        Logger.setDataBaseEnabled(true);
    }

    protected void setupDataManagers() {
        Logger.d(TAG, "setupDataManagers() called");
        setupSensorEventManager();
        setupFeatureManager();
        setupStateManager();
        setupClassificationManager();
        setupTrustLevelManager();
    }

    protected void setupSensorEventManager() {
        Logger.d(TAG, "setupSensorEventManager() called");
        DeviceSensorManager.initialize(this);
        SensorEventManager.initialize(this);
    }

    protected void setupFeatureManager() {
        Logger.d(TAG, "setupFeatureManager() called");
        FeatureManager.initialize(this);
    }

    protected void setupStateManager() {
        Logger.d(TAG, "setupStateManager() called");
        StateManager.initialize(this);
    }

    protected void setupClassificationManager() {
        Logger.d(TAG, "setupClassificationManager() called");
        ClassificationManager.initialize(this);
    }

    protected void setupTrustLevelManager() {
        Logger.d(TAG, "setupTrustLevelManager() called");
        TrustLevelManager.initialize(this);
    }

    private Messenger createMessenger() {
        return new Messenger(new Handler() {
            @Override
            public void handleMessage(Message message) {
                AuthenticationService.this.handleMessage(message);
            }
        });
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Logger.d(TAG, "onMessageReceived() called with: messageEvent = [" + messageEvent + "]");

        // convert MessageEvent to Message object
        Message message = new MessageBuilder()
                .withId(GoogleApiConnection.getMessageIdFromPath(messageEvent.getPath()))
                .withData(GoogleApiConnection.getBundleFromBytes(messageEvent.getData()))
                .build();

        handleMessage(message);
    }

    protected void handleMessage(Message message) {
        Logger.d(TAG, "handleMessage() called with: message = [" + message + "]");
        if (message.replyTo == null) {
            message.replyTo = applicationConnection.getMessenger();
        }

        try {
            switch (message.what) {
                case MessageBuilder.ID_ECHO: {
                    // reply with the same message
                    Message reply = new MessageBuilder()
                            .withData(message.getData())
                            .build();

                    message.replyTo.send(reply);

                    // forward echo to connected nodes
                    googleApiConnection.sendMessageToNearbyNodes(reply);
                    break;
                }
                case MessageBuilder.ID_START_SERVICE: {
                    startService();
                    break;
                }
                case MessageBuilder.ID_STOP_SERVICE: {
                    stopService();
                    break;
                }
                case MessageBuilder.ID_START_RECORDING: {
                    startRecording(message.getData());
                    break;
                }
                case MessageBuilder.ID_STOP_RECORDING: {
                    stopRecording();
                    break;
                }
                default: {
                    //throw new Exception("Unknown message ID: " + message.what);
                }
            }
        } catch (Exception ex) {
            Logger.w(TAG, "Unable to handle message: " + ex.getMessage());
        }
    }

    private void startService() {
        Logger.d(TAG, "startService() called");
        acquireWakeLock();
        SensorEventAggregators.getInstance().startAggregators();
        StateAggregators.getInstance().startAggregators();
        FeatureAggregators.getInstance().startAggregators();
        ClassificationAggregators.getInstance().startAggregators();
        TrustLevelAggregators.getInstance().startAggregators();
    }

    private void stopService() {
        Logger.d(TAG, "stopService() called");
        releaseWakeLock();
        SensorEventAggregators.getInstance().stopAggregators();
        StateAggregators.getInstance().stopAggregators();
        FeatureAggregators.getInstance().stopAggregators();
        ClassificationAggregators.getInstance().stopAggregators();
        TrustLevelAggregators.getInstance().stopAggregators();
    }

    public static void startLoggingPersistedData() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Logger.v(TAG, "Currently persisted data:");
                PersisterManager.logPersistedData();
                handler.postDelayed(this, TimeUnit.SECONDS.toMillis(10));
            }
        });
    }

    private void acquireWakeLock() {
        if (wakeLock != null) {
            return;
        }
        Logger.d(TAG, "Acquiring CPU wake lock");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (wakeLock == null) {
            return;
        }
        Logger.d(TAG, "Releasing CPU wake lock");
        wakeLock.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG, "onDestroy() called");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(TAG, "onBind() called with: intent = [" + intent + "]");
        Messenger applicationMessenger = (Messenger) intent.getExtras().get(ApplicationConnection.KEY_MESSENGER);
        applicationConnection.setMessenger(applicationMessenger);
        applicationConnection.setBound(true);
        return serviceMessenger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        Logger.d(TAG, "onRebind() called with: intent = [" + intent + "]");
        applicationConnection.setBound(true);
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d(TAG, "onUnbind() called with: intent = [" + intent + "]");
        applicationConnection.setBound(false);
        return super.onUnbind(intent);
    }

    @Override
    public void onLowMemory() {
        Logger.d(TAG, "onLowMemory() called");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Logger.d(TAG, "onTrimMemory() called with: level = [" + level + "]");
        super.onTrimMemory(level);
    }

    public void startRecording(final Bundle configuration) {
        try {
            if (recorder != null && recorder.isRecording()) {
                Log.w(TAG, "Recorder has been started already");
                return;
            }

            RecordingObserver recordingObserver = new RecordingObserver<SensorDataRecorder>() {
                @Override
                public void onRecordingStarted(SensorDataRecorder recorder) {
                    Log.d(TAG, "onRecordingStarted() called with: recorder = [" + recorder + "]");
                    Vibrator v = (Vibrator) AuthenticationService.this.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                }

                @Override
                public void onRecordingStopped(SensorDataRecorder recorder) {
                    Log.d(TAG, "onRecordingStopped() called with: recorder = [" + recorder + "]");
                    recorder.getDataPersister().logPersistedData();

                    Vibrator v = (Vibrator) AuthenticationService.this.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(1000);

                    try {
                        recorder.exportData(configuration);
                    } catch (DataPersistingException e) {
                        Log.w(TAG, "Unable to export recorded data: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            };

            recorder = DataRecorderBuilder.forSensorEventData(this)
                    .withAllAvailableTypes()
                    .withObserver(recordingObserver)
                    .startingIn(configuration.getLong(Record.KEY_DELAY))
                    .stoppingAfter(configuration.getLong(Record.KEY_DURATION))
                    .build();
        } catch (RecorderException e) {
            Log.e(TAG, "startRecording: ", e);
        }
    }

    public void stopRecording() {
        try {
            if (recorder == null || !recorder.isRecording()) {
                Log.w(TAG, "Recorder isn't active");
                return;
            }
            recorder.stopRecording();
        } catch (Exception ex) {
            Log.e(TAG, "stopRecording: ", ex);
        }
    }
}
