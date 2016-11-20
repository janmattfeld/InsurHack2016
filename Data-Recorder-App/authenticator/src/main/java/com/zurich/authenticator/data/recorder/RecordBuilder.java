package com.zurich.authenticator.data.recorder;

import android.os.Build;

import com.zurich.authenticator.data.batch.DataBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecordBuilder {

    private String user = "";
    private String label = "";
    private String comment = "";
    private String deviceName = Build.MODEL;
    private String deviceId = Build.ID;
    private int version = Record.VERSION_1;
    private DataRecorder recorder;
    private List<DataBatch> dataBatches;

    public RecordBuilder() {
    }

    public RecordBuilder(DataRecorder recorder) {
        this.recorder = recorder;
    }

    public RecordBuilder withData(List<DataBatch> dataBatches) {
        this.dataBatches = dataBatches;
        return this;
    }

    public RecordBuilder withData(Map<Integer, DataBatch> dataBatchMap) {
        return withData(new ArrayList<>(dataBatchMap.values()));
    }

    public RecordBuilder fromUser(String user) {
        this.user = user;
        return this;
    }

    public RecordBuilder withLabel(String label) {
        this.label = label;
        return this;
    }

    public RecordBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Record build() {
        return new Record(this);
    }

    public String getUser() {
        return user;
    }

    public String getLabel() {
        return label;
    }

    public String getComment() {
        return comment;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getVersion() {
        return version;
    }

    public DataRecorder getRecorder() {
        return recorder;
    }

    public List<DataBatch> getDataBatches() {
        return dataBatches;
    }
}
