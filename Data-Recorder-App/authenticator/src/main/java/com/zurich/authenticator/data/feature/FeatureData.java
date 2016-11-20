package com.zurich.authenticator.data.feature;

import com.google.gson.annotations.Expose;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.util.StringUtils;
import com.zurich.authenticator.util.TimeUtils;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.table.TableRow;

import java.util.ArrayList;
import java.util.List;

public class FeatureData extends Data {

    int featureType = Data.TYPE_NOT_SET;
    @Expose
    float[] values;

    public FeatureData() {
        super(Data.TYPE_FEATURE);
    }

    public FeatureData(int featureType) {
        this();
        this.featureType = featureType;
    }

    public FeatureData(FeatureData featureData) {
        this();
        this.featureType = featureData.getFeatureType();
        values = new float[featureData.values.length];
        System.arraycopy(featureData.values, 0, values, 0, featureData.values.length);
    }

    @Override
    public String toString() {
        return toMarkdownElement().toString().replace("|", "∙");
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        List<Object> columns = new ArrayList<>();
        columns.add("[ " + StringUtils.serializeToCsv(values) + " ]");
        columns.add(FeatureManager.getReadableFeatureType(featureType));
        columns.add(TimeUtils.getReadableTimeSince(timestamp));
        return new TableRow(columns);
    }

    public int getFeatureType() {
        return featureType;
    }

    public void setFeatureType(int featureType) {
        this.featureType = featureType;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

}
