package com.zurich.authenticator.data.state.manager;

import android.content.Context;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.aggregation.DataAggregationObserver;
import com.zurich.authenticator.data.aggregation.DataAggregator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.manager.DataManager;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.PersisterManager;
import com.zurich.authenticator.data.state.StateData;
import com.zurich.authenticator.data.state.aggregator.DisplayOnStateAggregator;
import com.zurich.authenticator.data.state.aggregator.StateAggregator;
import com.zurich.authenticator.data.state.aggregator.StateAggregators;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StateManager extends DataManager {

    private static final String TAG = StateManager.class.getSimpleName();

    private static StateManager instance;

    public static final int STATE_DISPLAY_ON = 201;

    private static final Map<Integer, String> readableStateTypes = getReadableStateTypes();

    private StateManager() {

    }

    public static StateManager getInstance() {
        if (instance == null) {
            instance = new StateManager();
        }
        return instance;
    }

    public static void initialize(Context context) {
        getInstance().initializeManager(context);
    }

    @Override
    public void setupDataAggregators(Context context) throws DataAggregationException {
        List<StateAggregator> stateAggregators = getDefaultDataAggregators(context);

        for (StateAggregator stateAggregator : stateAggregators) {
            stateAggregator.addAggregationObserver(new DataAggregationObserver<StateData>() {
                @Override
                public void onDataAggregated(StateData stateData, DataAggregator<StateData> stateDataDataAggregator) {
                    try {
                        PersisterManager.persist(stateData, PersisterManager.STRATEGY_MEMORY);
                    } catch (DataPersistingException e) {
                        Logger.e(TAG, "onDataAggregated: ", e);
                    }
                }
            });
            stateAggregator.startAggregation();
            StateAggregators.getInstance().addDataAggregator(stateAggregator);
        }
    }

    @Override
    public List<StateAggregator> getDefaultDataAggregators(Context context) {
        List<StateAggregator> aggregators = new ArrayList<>();
        aggregators.add(new DisplayOnStateAggregator(1000, context));
        // Add new aggregators here
        return aggregators;
    }

    public static float[] getValuesFromStateData(List<StateData> stateDataList) {
        float[] values = new float[stateDataList.size()];
        for (int i = 0; i < stateDataList.size(); i++) {
            values[i] = stateDataList.get(i).getValue();
        }
        return values;
    }

    public static List<StateData> getStateDataSince(long timestamp, DataBatch<StateData> stateDataBatch) throws Exception {
        List<StateData> stateDataList = stateDataBatch.getDataSince(timestamp);
        if (stateDataList.size() == 0) {
            throw new Exception("No states available since " + timestamp);
        }
        return stateDataList;
    }

    public static String getReadableStateType(int type) {
        if (!readableStateTypes.containsKey(type)) {
            return "Unknown State";
        }
        return readableStateTypes.get(type);
    }

    private static Map<Integer, String> getReadableStateTypes() {
        if (readableStateTypes != null) {
            return readableStateTypes;
        }
        Map<Integer, String> readableSensorTypes = new HashMap<>();
        readableSensorTypes.put(STATE_DISPLAY_ON, "Display On");
        return readableSensorTypes;
    }

}
