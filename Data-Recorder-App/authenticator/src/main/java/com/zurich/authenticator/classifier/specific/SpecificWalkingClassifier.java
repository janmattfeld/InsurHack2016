package com.zurich.authenticator.classifier.specific;

import com.zurich.authenticator.data.classification.manager.ClassificationManager;
import com.zurich.authenticator.data.feature.manager.FeatureManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SpecificWalkingClassifier extends SpecificClassifier {

    public SpecificWalkingClassifier() {
        super(ClassificationManager.CLASSIFICATION_WALKING_SPECIFIC, ClassificationManager.CLASSIFICATION_WALKING);
    }

    @Override
    public Set<Integer> getFeatureTypes() {
        return new HashSet<>(Arrays.asList(
                FeatureManager.TYPE_VARIANCE_ACCELEROMETER,
                FeatureManager.TYPE_MEAN_ABSOLUTE_DIFFERENCE_ACCELEROMETER,
                FeatureManager.TYPE_STANDARD_DEVIATION_ACCELEROMETER
        ));
    }

}
