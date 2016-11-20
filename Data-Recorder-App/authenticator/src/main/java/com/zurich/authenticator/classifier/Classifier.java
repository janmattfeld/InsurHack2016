package com.zurich.authenticator.classifier;

import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.classification.ClassificationException;

public abstract class Classifier {

    protected int classificationType;

    public Classifier(int classificationType) {
        this.classificationType = classificationType;
    }

    public abstract boolean canClassify();

    public abstract ClassificationData classify() throws ClassificationException;

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public int getClassificationType() {
        return classificationType;
    }
}
