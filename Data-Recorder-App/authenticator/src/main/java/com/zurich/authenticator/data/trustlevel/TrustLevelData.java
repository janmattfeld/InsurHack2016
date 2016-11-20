package com.zurich.authenticator.data.trustlevel;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.util.TimeUtils;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.table.TableRow;

import java.util.ArrayList;
import java.util.List;

public class TrustLevelData extends Data {

    private float value;
    private float confidence;

    public TrustLevelData() {
        super(Data.TYPE_TRUST_LEVEL);
    }

    public TrustLevelData(float value, float confidence) {
        this();
        this.value = value;
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return toMarkdownElement().toString().replace("|", "∙");
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        List<Object> columns = new ArrayList<>();
        columns.add(String.format(java.util.Locale.US, "%.4f", value));
        columns.add(String.format(java.util.Locale.US, "%.4f", confidence));
        columns.add(Data.getReadableType(type));
        columns.add(TimeUtils.getReadableTimeSince(timestamp));
        return new TableRow(columns);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

}
