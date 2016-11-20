package com.zurich.authenticator.data.trustlevel.aggregator;

import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.aggregation.TimedDataAggregator;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.trustlevel.TrustLevelData;

public abstract class TrustLevelAggregator extends TimedDataAggregator<TrustLevelData> {

    public TrustLevelAggregator(long aggregationInterval) {
        this(DataAggregator.TYPE_DEFAULT, aggregationInterval);
    }

    public TrustLevelAggregator(int aggregatorType, long aggregationInterval) {
        super(Data.TYPE_TRUST_LEVEL, aggregatorType, aggregationInterval);
    }
}
