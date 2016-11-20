package com.zurich.authenticator.data.classification;

import com.google.gson.annotations.Expose;
import com.zurich.authenticator.data.classification.manager.ClassificationManager;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.util.TimeUtils;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.table.TableRow;

import java.util.ArrayList;
import java.util.List;

public class ClassificationData extends Data {

    @Expose
    private float value;
    private int classificationType = Data.TYPE_NOT_SET;
    @Expose
    private int positivesCount = 0;
    @Expose
    private int negativesCount = 0;

    public ClassificationData() {
        super(Data.TYPE_CLASSIFICATION);
    }

    public ClassificationData(int classificationType) {
        this();
        this.classificationType = classificationType;
    }

    public ClassificationData(ClassificationData data) {
        this();
        classificationType = data.getClassificationType();
        positivesCount = data.getPositivesCount();
        negativesCount = data.getNegativesCount();
        value = calculateValue();
    }

    public void addClassification(boolean classified) {
        if (classified) {
            incrementPositivesCount();
        } else {
            incrementNegativesCount();
        }
    }

    public float calculateValue() throws ArithmeticException {
        int total = positivesCount + negativesCount;
        if (total == 0) {
            throw new ArithmeticException("No classifications available");
        }
        value = (float) positivesCount / total;
        return value;
    }

    @Override
    public String toString() {
        return toMarkdownElement().toString().replace("|", "∙");
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        List<Object> columns = new ArrayList<>();
        columns.add(String.format(java.util.Locale.US, "%.4f", value));
        columns.add(ClassificationManager.getReadableClassificationType(classificationType));
        columns.add(TimeUtils.getReadableTimeSince(timestamp));
        return new TableRow(columns);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getClassificationType() {
        return classificationType;
    }

    public void setClassificationType(int classificationType) {
        this.classificationType = classificationType;
    }

    public int getPositivesCount() {
        return positivesCount;
    }

    public void setPositivesCount(int positivesCount) {
        this.positivesCount = positivesCount;
    }

    public void incrementPositivesCount() {
        positivesCount++;
    }

    public int getNegativesCount() {
        return negativesCount;
    }

    public void setNegativesCount(int negativesCount) {
        this.negativesCount = negativesCount;
    }

    public void incrementNegativesCount() {
        negativesCount++;
    }

}
