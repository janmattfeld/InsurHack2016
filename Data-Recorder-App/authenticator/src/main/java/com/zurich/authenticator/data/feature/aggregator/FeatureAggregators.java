package com.zurich.authenticator.data.feature.aggregator;

import com.zurich.authenticator.data.aggregation.DataAggregatorManager;
import com.zurich.authenticator.data.generic.Data;

public class FeatureAggregators extends DataAggregatorManager<FeatureAggregator> {

    private static FeatureAggregators instance;

    public FeatureAggregators() {
        super(Data.TYPE_FEATURE);
    }

    public static FeatureAggregators getInstance() {
        if (instance == null) {
            instance = new FeatureAggregators();
        }
        return instance;
    }

}
