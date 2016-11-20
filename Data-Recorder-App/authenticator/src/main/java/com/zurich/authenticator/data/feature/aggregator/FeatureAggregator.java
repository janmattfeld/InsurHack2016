package com.zurich.authenticator.data.feature.aggregator;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.TimedDataAggregator;
import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.calculator.DataCalculator;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.generic.Data;

public abstract class FeatureAggregator extends TimedDataAggregator<FeatureData> {

    protected DataCalculator<FeatureData> dataCalculator;

    public FeatureAggregator(int aggregatorType, long aggregationInterval) {
        super(Data.TYPE_FEATURE, aggregatorType, aggregationInterval);
    }

    @Override
    public FeatureData aggregate() throws DataAggregationException {
        if (dataCalculator == null) {
            throw new DataAggregationException("Data calculator not set");
        }
        if (!dataCalculator.canCalculate()) {
            throw new DataAggregationException(dataCalculator + " can't calculate data");
        }
        try {
            FeatureData featureData = dataCalculator.calculate();
            featureData.setFeatureType(aggregatorType);
            return featureData;
        } catch (DataCalculationException e) {
            throw new DataAggregationException(e);
        }
    }

    public DataCalculator<FeatureData> getDataCalculator() {
        return dataCalculator;
    }

    public void setDataCalculator(DataCalculator<FeatureData> dataCalculator) {
        this.dataCalculator = dataCalculator;
    }

}
