package com.zurich.authenticator.data.state.aggregator;

import com.zurich.authenticator.data.aggregation.TimedDataAggregator;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.state.StateData;

public abstract class StateAggregator extends TimedDataAggregator<StateData> {

    public StateAggregator(int aggregatorType, long aggregationInterval) {
        super(Data.TYPE_STATE, aggregatorType, aggregationInterval);
    }

}
