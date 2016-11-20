package com.zurich.authenticator.data.aggregation;

import java.util.ArrayList;
import java.util.List;

public abstract class DataAggregator<Data> implements DataAggregationObserver<Data> {

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_RECORDER = 1;

    protected int dataType;
    protected int aggregatorType = TYPE_DEFAULT;
    protected boolean isAggregating = false;
    protected List<DataAggregationObserver> aggregationObservers = new ArrayList<>();

    public DataAggregator(int dataType, int aggregatorType) {
        this.dataType = dataType;
        this.aggregatorType = aggregatorType;
    }

    public abstract void startAggregation();

    public abstract void stopAggregation();

    public abstract Data aggregate() throws DataAggregationException;

    public void addAggregationObserver(DataAggregationObserver<Data> aggregationObserver) {
        if (!aggregationObservers.contains(aggregationObserver)) {
            aggregationObservers.add(aggregationObserver);
        }
    }

    public void removeAggregationObserver(DataAggregationObserver<Data> aggregationObserver) {
        if (aggregationObservers.contains(aggregationObserver)) {
            aggregationObservers.remove(aggregationObserver);
        }
    }

    @Override
    public void onDataAggregated(Data data, DataAggregator<Data> dataDataAggregator) {
        for (DataAggregationObserver dataAggregationObserver : aggregationObservers) {
            dataAggregationObserver.onDataAggregated(data, dataDataAggregator);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName())
                .append(" for ")
                .append(com.zurich.authenticator.data.generic.Data.getReadableType(dataType))
                .append(" (").append(aggregatorType).append(")");

        return sb.toString();
    }

    public boolean isAggregating() {
        return isAggregating;
    }

    public int getDataType() {
        return dataType;
    }

    public int getAggregatorType() {
        return aggregatorType;
    }
}
