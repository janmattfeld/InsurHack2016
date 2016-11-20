package com.zurich.authenticator.data.classification.aggregator;

import com.zurich.authenticator.classifier.generic.GenericWalkingClassifier;
import com.zurich.authenticator.data.classification.manager.ClassificationManager;

public class WalkingClassificationAggregator extends ClassificationAggregator {

    public WalkingClassificationAggregator(long aggregationInterval) {
        super(ClassificationManager.CLASSIFICATION_WALKING, aggregationInterval, new GenericWalkingClassifier());
    }

}
