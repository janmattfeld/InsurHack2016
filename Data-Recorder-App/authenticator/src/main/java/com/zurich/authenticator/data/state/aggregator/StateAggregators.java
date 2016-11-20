package com.zurich.authenticator.data.state.aggregator;

import com.zurich.authenticator.data.aggregation.DataAggregatorManager;
import com.zurich.authenticator.data.generic.Data;

public class StateAggregators extends DataAggregatorManager<StateAggregator> {

    private static StateAggregators instance;

    public StateAggregators() {
        super(Data.TYPE_STATE);
    }

    public static StateAggregators getInstance() {
        if (instance == null) {
            instance = new StateAggregators();
        }
        return instance;
    }

}
