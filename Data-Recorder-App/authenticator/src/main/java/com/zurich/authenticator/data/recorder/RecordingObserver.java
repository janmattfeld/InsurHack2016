package com.zurich.authenticator.data.recorder;

public interface RecordingObserver<T extends Recorder> {

    public void onRecordingStarted(T recorder);

    public void onRecordingStopped(T recorder);

}
