package com.zurich.authenticator.data.generic;

import com.google.gson.annotations.Expose;
import com.zurich.authenticator.util.markdown.MarkdownSerializable;

public abstract class Data implements MarkdownSerializable {

    public static final int TYPE_SENSOR_EVENT = 100;
    public static final int TYPE_STATE = 200;
    public static final int TYPE_FEATURE = 300;
    public static final int TYPE_CLASSIFICATION = 400;
    public static final int TYPE_TRUST_LEVEL = 500;

    public static final int TYPE_NOT_SET = -1;

    protected int type = TYPE_NOT_SET;
    @Expose
    protected long timestamp;

    public Data(int type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return getReadableType(type);
    }

    public static String getReadableType(int type) {
        switch (type) {
            case TYPE_SENSOR_EVENT:
                return "Sensor Event";
            case TYPE_STATE:
                return "State";
            case TYPE_FEATURE:
                return "Feature";
            case TYPE_CLASSIFICATION:
                return "Classification";
            case TYPE_TRUST_LEVEL:
                return "Trust Level";
            default:
                return "Data";
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
