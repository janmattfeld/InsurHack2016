package com.zurich.authenticator.data.trustlevel.aggregator;

import com.zurich.authenticator.data.aggregation.DataAggregatorManager;
import com.zurich.authenticator.data.generic.Data;

public final class TrustLevelAggregators extends DataAggregatorManager<TrustLevelAggregator> {

    private static TrustLevelAggregators instance;

    private TrustLevelAggregators() {
        super(Data.TYPE_TRUST_LEVEL);
    }

    public static TrustLevelAggregators getInstance() {
        if (instance == null) {
            instance = new TrustLevelAggregators();
        }
        return instance;
    }

}
