package com.zurich.authenticator.data.recorder;

import com.google.gson.annotations.Expose;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.generic.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Record {

    public static final int VERSION_1 = 1;

    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_LABEL = "label";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_DELAY = "delay";
    public static final String KEY_DURATION = "duration";

    @Expose
    private String user;
    @Expose
    private String label;
    @Expose
    private String comment;
    @Expose
    private String deviceName;
    @Expose
    private String deviceId;
    @Expose
    private int version = VERSION_1;
    @Expose
    private DataRecorder recorder;
    @Expose
    private List<DataBatch> dataBatches;

    public Record() {
    }

    public Record(Record record) {
        user = record.getUser();
        label = record.getLabel();
        comment = record.getComment();
        deviceName = record.getDeviceName();
        deviceId = record.getDeviceId();
        version = record.getVersion();
        recorder = record.getRecorder();
        dataBatches = new ArrayList<>(record.getDataBatches());
    }

    public Record(RecordBuilder recordBuilder) {
        user = recordBuilder.getUser();
        label = recordBuilder.getLabel();
        comment = recordBuilder.getComment();
        deviceName = recordBuilder.getDeviceName();
        deviceId = recordBuilder.getDeviceId();
        version = recordBuilder.getVersion();
        recorder = recordBuilder.getRecorder();
        dataBatches = recordBuilder.getDataBatches();
    }

    /**
     * Creates a {@link HashMap} with all {@link DataBatch}es containing
     * {@link com.zurich.authenticator.data.sensor.SensorEventData}, mapped by sensor type.
     *
     * @return
     */
    public Map<Integer, DataBatch> getSensorEventDataBatchMap() {
        Map<Integer, DataBatch> sensorEventDataBatchMap = new HashMap<>();
        for (DataBatch recordedDataBatch : dataBatches) {
            if (recordedDataBatch.getType() != Data.TYPE_SENSOR_EVENT) {
                continue;
            }
            sensorEventDataBatchMap.put(recordedDataBatch.getSubType(), recordedDataBatch);
        }
        return sensorEventDataBatchMap;
    }

    public String getFileName() {
        StringBuilder sb = new StringBuilder("record");

        // device
        if (deviceName != null && deviceName.length() > 0) {
            sb.append("_").append(deviceName);
        }

        // label
        if (label != null && label.length() > 0) {
            sb.append("_").append(label);
        }

        // comment
        if (comment != null && comment.length() > 0) {
            sb.append("_").append(comment);
        }

        // user
        if (user != null && user.length() > 0) {
            sb.append("_").append(user);
        }

        // timestamp
        sb.append("_").append(recorder.getStartTimestamp());

        // extension
        sb.append(".json");

        return sb.toString().toLowerCase().replace(" ", "-");
    }

    @Override
    public String toString() {
        return getFileName();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DataRecorder getRecorder() {
        return recorder;
    }

    public void setRecorder(DataRecorder recorder) {
        this.recorder = recorder;
    }

    public List<DataBatch> getDataBatches() {
        return dataBatches;
    }

    public void setDataBatches(List<DataBatch> dataBatches) {
        this.dataBatches = dataBatches;
    }
}
