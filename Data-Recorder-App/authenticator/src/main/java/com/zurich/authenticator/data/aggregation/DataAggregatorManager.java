package com.zurich.authenticator.data.aggregation;

import com.zurich.authenticator.util.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class DataAggregatorManager<T extends DataAggregator> {

    protected int dataType;
    protected Map<Integer, T> aggregatorMap = new HashMap<>();

    public DataAggregatorManager(int dataType) {
        this.dataType = dataType;
    }

    /**
     * Returns a {@link DataAggregator} for the given aggregatorType
     * if available, if not throws an exception
     *
     * @param aggregatorType
     * @return a {@link DataAggregator}
     */
    public T getAggregator(int aggregatorType) throws DataAggregationException {
        if (!aggregatorMap.containsKey(aggregatorType)) {
            throw new DataAggregationException("No aggregator available for type " + aggregatorType);
        }
        return aggregatorMap.get(aggregatorType);
    }

    public void addDataAggregator(T dataAggregator) {
        if (dataAggregator.getDataType() != dataType) {
            Logger.w(this.toString(), "Not adding data aggregator, type mismatch! " + dataAggregator);
            return;
        }
        aggregatorMap.put(dataAggregator.getAggregatorType(), dataAggregator);
        Logger.d(this.toString(), "Added data aggregator: " + dataAggregator);
    }

    public void removeAggregator(T dataAggregator) {
        aggregatorMap.remove(dataAggregator.getAggregatorType());
        Logger.d(this.toString(), "Removed data aggregator: " + dataAggregator);
    }

    public void startAggregators() {
        for (Map.Entry<Integer, T> aggregatorEntry : aggregatorMap.entrySet()) {
            aggregatorEntry.getValue().startAggregation();
        }
    }

    public void stopAggregators() {
        for (Map.Entry<Integer, T> aggregatorEntry : aggregatorMap.entrySet()) {
            aggregatorEntry.getValue().stopAggregation();
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public int getDataType() {
        return dataType;
    }

    public Map<Integer, T> getAggregatorMap() {
        return aggregatorMap;
    }

}
