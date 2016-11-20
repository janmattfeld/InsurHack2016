package com.zurich.authenticator.data.sensor;

import android.hardware.SensorEvent;

import com.google.gson.annotations.Expose;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.sensor.manager.SensorEventManager;
import com.zurich.authenticator.util.StringUtils;
import com.zurich.authenticator.util.TimeUtils;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.table.TableRow;

import java.util.ArrayList;
import java.util.List;

public class SensorEventData extends Data {

    protected int sensorType = Data.TYPE_NOT_SET;
    @Expose
    protected float[] values;

    public SensorEventData() {
        super(Data.TYPE_SENSOR_EVENT);
    }

    // for testing
    public SensorEventData(int sensorType) {
        this();
        this.sensorType = sensorType;
    }

    public SensorEventData(SensorEvent sensorEvent) {
        this();
        sensorType = sensorEvent.sensor.getType();
        timestamp = System.currentTimeMillis();
        values = new float[sensorEvent.values.length];
        System.arraycopy(sensorEvent.values, 0, values, 0, sensorEvent.values.length);
    }

    @Override
    public String toString() {
        return toMarkdownElement().toString().replace("|", "∙");
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        List<Object> columns = new ArrayList<>();
        columns.add("[ " + StringUtils.serializeToCsv(values) + " ]");
        columns.add(SensorEventManager.getReadableSensorType(sensorType));
        columns.add(TimeUtils.getReadableTimeSince(timestamp));
        return new TableRow(columns);
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

}
