package com.zurich.authenticator.data.aggregation;

public interface DataAggregationObserver<Data> {

    void onDataAggregated(Data data, DataAggregator<Data> dataDataAggregator);

}
