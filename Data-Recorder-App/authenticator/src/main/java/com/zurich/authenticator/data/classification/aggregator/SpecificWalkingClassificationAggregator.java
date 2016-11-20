package com.zurich.authenticator.data.classification.aggregator;

import com.zurich.authenticator.classifier.specific.SpecificWalkingClassifier;
import com.zurich.authenticator.data.classification.manager.ClassificationManager;

public class SpecificWalkingClassificationAggregator extends ClassificationAggregator {

    public SpecificWalkingClassificationAggregator(long aggregationInterval) {
        super(ClassificationManager.CLASSIFICATION_WALKING_SPECIFIC, aggregationInterval, new SpecificWalkingClassifier());
    }

}
