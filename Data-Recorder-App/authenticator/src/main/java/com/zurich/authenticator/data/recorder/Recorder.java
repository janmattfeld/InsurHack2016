package com.zurich.authenticator.data.recorder;

import com.google.gson.annotations.Expose;
import com.zurich.authenticator.util.TimeUtils;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Recorder implements RecordingObserver {

    private static final String TAG = Recorder.class.getSimpleName();

    protected boolean isRecording;
    @Expose
    protected long startTimestamp;
    @Expose
    protected long stopTimestamp;

    private transient Timer autoStopTimer;
    private transient Timer autoStartTimer;

    private transient List<RecordingObserver> recordingObservers = new ArrayList<>();

    public Recorder() {
    }

    public void startRecording() {
        if (isRecording) {
            Logger.w(TAG, "Recorder has been started already");
            return;
        }
        Logger.d(TAG, "Starting: " + this);
        isRecording = true;
        startTimestamp = System.currentTimeMillis();
        setupAutoStop();

        if (autoStartTimer != null) {
            autoStartTimer.cancel();
            autoStartTimer = null;
        }
        onRecordingStarted(this);
    }

    protected void setupAutoStart() {
        if (startTimestamp > System.currentTimeMillis()) {
            // recording should start in the future
            long startDelay = startTimestamp - System.currentTimeMillis();
            autoStartTimer = new Timer();
            autoStartTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startRecording();
                }
            }, startDelay);
            Logger.d(TAG, "Scheduled to start: " + this);
        } else if (startTimestamp > 0) {
            // recording should have started already
            startRecording();
        } else {
            // recording will be started manually
        }
    }

    public void stopRecording() {
        if (!isRecording) {
            Logger.w(TAG, "Recorder has been stopped already");
            return;
        }
        Logger.d(TAG, "Stopping: " + this);
        isRecording = false;
        stopTimestamp = System.currentTimeMillis();

        if (autoStopTimer != null) {
            autoStopTimer.cancel();
            autoStopTimer = null;
        }
        onRecordingStopped(this);
    }

    protected void setupAutoStop() {
        if (stopTimestamp > startTimestamp) {
            // recording should be stopped in the future
            long stopDelay = stopTimestamp - startTimestamp;
            autoStopTimer = new Timer();
            autoStopTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopRecording();
                }
            }, stopDelay);
            Logger.d(TAG, "Scheduled to stop: " + this);
        } else {
            stopTimestamp = 0;
        }
    }

    @Override
    public void onRecordingStarted(Recorder recorder) {
        for (RecordingObserver recordingObserver : recordingObservers) {
            try {
                recordingObserver.onRecordingStarted(recorder);
            } catch (Exception ex) {
                Logger.e(TAG, "onRecordingStarted: ", ex);
            }
        }
    }

    @Override
    public void onRecordingStopped(Recorder recorder) {
        for (RecordingObserver recordingObserver : recordingObservers) {
            try {
                recordingObserver.onRecordingStopped(recorder);
            } catch (Exception ex) {
                Logger.e(TAG, "onRecordingStopped: ", ex);
            }
        }
    }

    public void addRecordingObserver(RecordingObserver recordingObserver) {
        if (recordingObservers.contains(recordingObserver)) {
            return;
        }
        recordingObservers.add(recordingObserver);
    }

    public void removeRecordingObserver(RecordingObserver recordingObserver) {
        if (!recordingObservers.contains(recordingObserver)) {
            return;
        }
        recordingObservers.add(recordingObserver);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());

        // start
        if (startTimestamp > 0 && startTimestamp <= System.currentTimeMillis()) {
            sb.append(" started ").append(TimeUtils.getReadableTimeSince(startTimestamp));
        } else if (startTimestamp > System.currentTimeMillis()) {
            long startOffset = startTimestamp - System.currentTimeMillis();
            sb.append(" starting in ").append(TimeUtils.getReadableDuration(startOffset));
        }

        // stop
        if (stopTimestamp > 0) {
            long recordingDuration = stopTimestamp - startTimestamp;
            if (stopTimestamp > System.currentTimeMillis()) {
                sb.append(" and recording for ");
            } else {
                sb.append(" and recorded for ");
            }
            sb.append(TimeUtils.getReadableDuration(recordingDuration));
        }

        return sb.toString();
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(long stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public boolean isRecording() {
        return isRecording;
    }
}
