package com.zurich.authenticator.data.trustlevel.manager;

import android.content.Context;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.DataAggregationObserver;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.manager.DataManager;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.PersisterManager;
import com.zurich.authenticator.data.persister.memory.MemoryPersister;
import com.zurich.authenticator.data.trustlevel.TrustLevelData;
import com.zurich.authenticator.data.trustlevel.aggregator.TrustLevelAggregator;
import com.zurich.authenticator.data.trustlevel.aggregator.TrustLevelAggregators;
import com.zurich.authenticator.data.trustlevel.persister.TrustLevelMemoryPersister;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class TrustLevelManager extends DataManager {

    private static final String TAG = TrustLevelManager.class.getSimpleName();

    private static TrustLevelManager instance;

    private TrustLevelMemoryPersister trustLevelMemoryPersister;

    private TrustLevelManager() {

    }

    public static TrustLevelManager getInstance() {
        if (instance == null) {
            instance = new TrustLevelManager();
        }
        return instance;
    }

    public static void initialize(Context context) {
        getInstance().initializeManager(context);
    }

    @Override
    public void setupDataAggregators(Context context) throws DataAggregationException {
        List<TrustLevelAggregator> trustLevelAggregators = getDefaultDataAggregators(context);
        for (TrustLevelAggregator trustLevelAggregator : trustLevelAggregators) {
            trustLevelAggregator.addAggregationObserver(new DataAggregationObserver<TrustLevelData>() {
                @Override
                public void onDataAggregated(TrustLevelData trustLevelData, DataAggregator<TrustLevelData> trustLevelDataDataAggregator) {
                    try {
                        PersisterManager.persist(trustLevelData, PersisterManager.STRATEGY_MEMORY);
                    } catch (DataPersistingException e) {
                        Logger.e(TAG, "onDataAggregated: ", e);
                    }
                }
            });
            trustLevelAggregator.startAggregation();
            TrustLevelAggregators.getInstance().addDataAggregator(trustLevelAggregator);
        }
    }

    @Override
    public List<TrustLevelAggregator> getDefaultDataAggregators(Context context) {
        List<TrustLevelAggregator> trustLevelAggregators = new ArrayList<>();
        //trustLevelAggregators.add(new DefaultTrustLevelAggregator(1000));
        return trustLevelAggregators;
    }

    public static TrustLevelMemoryPersister getMemoryPersister() throws DataPersistingException {
        TrustLevelManager instance = getInstance();
        if (instance.trustLevelMemoryPersister == null) {
            MemoryPersister memoryPersister = (MemoryPersister) PersisterManager.getDataPersister(PersisterManager.STRATEGY_MEMORY);
            instance.trustLevelMemoryPersister = (TrustLevelMemoryPersister) memoryPersister.getPersister(Data.TYPE_TRUST_LEVEL);
        }
        return instance.trustLevelMemoryPersister;
    }

    public static DataBatch<TrustLevelData> getMemoryDataBatch() throws DataPersistingException {
        return getMemoryPersister().getDataBatch(null);
    }

}
