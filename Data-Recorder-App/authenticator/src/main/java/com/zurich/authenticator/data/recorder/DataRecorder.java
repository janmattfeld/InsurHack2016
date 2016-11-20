package com.zurich.authenticator.data.recorder;

import android.content.Context;
import android.os.Bundle;

import com.zurich.authenticator.data.aggregation.DataAggregationObserver;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersister;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.PersisterManager;
import com.zurich.authenticator.data.persister.file.FilePersister;
import com.zurich.authenticator.util.logging.Logger;

public abstract class DataRecorder<T extends Data> extends Recorder implements DataAggregationObserver<T> {

    transient DataPersister dataPersister;
    transient Context context;

    public DataRecorder() {
    }

    public DataRecorder(DataRecorderBuilder dataRecorderBuilder) {
        this();
        if (dataRecorderBuilder.getRecordingObserver() != null) {
            addRecordingObserver(dataRecorderBuilder.getRecordingObserver());
        }

        context = dataRecorderBuilder.getContext();
        startTimestamp = dataRecorderBuilder.getStartTimestamp();
        stopTimestamp = dataRecorderBuilder.getStopTimestamp();
    }

    @Override
    public void startRecording() {
        // reset & create new DataPersister
        dataPersister = null;
        dataPersister = getDataPersister();
        super.startRecording();
    }

    @Override
    public void stopRecording() {
        super.stopRecording();
    }

    @Override
    public void onRecordingStarted(Recorder recorder) {
        startAggregators();
        super.onRecordingStarted(recorder);
    }

    @Override
    public void onRecordingStopped(Recorder recorder) {
        stopAggregators();
        super.onRecordingStopped(recorder);
    }

    @Override
    public void onDataAggregated(T data, DataAggregator<T> dataAggregator) {
        try {
            dataPersister.persist(data);
        } catch (DataPersistingException e) {
            Logger.w(DataRecorder.class.getSimpleName(), "onDataAggregated: ", e);
        }
    }

    public void exportData(Bundle configuration) throws DataPersistingException {
        try {
            Record record = toRecord(configuration);
            FilePersister filePersister = (FilePersister) PersisterManager.getDataPersister(PersisterManager.STRATEGY_FILE);
            filePersister.persist(record);
        } catch (RecorderException e) {
            throw new DataPersistingException("Unable to get Record", e);
        }
    }

    public abstract DataPersister getDataPersister();

    public abstract void startAggregators();

    public abstract void stopAggregators();

    public abstract Record toRecord(Bundle configuration) throws RecorderException;

}
