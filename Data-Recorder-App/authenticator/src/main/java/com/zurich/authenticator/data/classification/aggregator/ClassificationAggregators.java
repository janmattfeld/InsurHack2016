package com.zurich.authenticator.data.classification.aggregator;

import com.zurich.authenticator.data.aggregation.DataAggregatorManager;
import com.zurich.authenticator.data.generic.Data;

public class ClassificationAggregators extends DataAggregatorManager<ClassificationAggregator> {

    private static ClassificationAggregators instance;

    public ClassificationAggregators() {
        super(Data.TYPE_CLASSIFICATION);
    }

    public static ClassificationAggregators getInstance() {
        if (instance == null) {
            instance = new ClassificationAggregators();
        }
        return instance;
    }

}
