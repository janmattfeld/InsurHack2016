package com.zurich.authenticator.data.classification.aggregator;

import com.zurich.authenticator.classifier.Classifier;
import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.TimedDataAggregator;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.classification.ClassificationException;
import com.zurich.authenticator.data.generic.Data;

public abstract class ClassificationAggregator extends TimedDataAggregator<ClassificationData> {

    protected Classifier classifier;

    public ClassificationAggregator(int aggregatorType, long aggregationInterval, Classifier classifier) {
        super(Data.TYPE_CLASSIFICATION, aggregatorType, aggregationInterval);
        this.classifier = classifier;
    }

    @Override
    public ClassificationData aggregate() throws DataAggregationException {
        try {
            return classifier.classify();
        } catch (ClassificationException ex) {
            throw new DataAggregationException(ex);
        }
    }

    public Classifier getClassifier() {
        return classifier;
    }

}
