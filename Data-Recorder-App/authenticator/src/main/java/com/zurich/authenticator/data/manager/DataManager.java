package com.zurich.authenticator.data.manager;

import android.content.Context;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.util.logging.Logger;

import java.util.List;

public abstract class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    public void initializeManager(Context context) {
        try {
            setupDataAggregators(context);
        } catch (DataAggregationException ex) {
            Logger.e(TAG, "initializeManager: ", ex);
        }
    }

    public abstract void setupDataAggregators(Context context) throws DataAggregationException;

    public abstract List<? extends DataAggregator> getDefaultDataAggregators(Context context);

}