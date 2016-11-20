package com.zurich.authenticator.data.state.aggregator;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

import com.zurich.authenticator.data.aggregation.DataAggregationException;
import com.zurich.authenticator.data.state.StateData;
import com.zurich.authenticator.data.state.manager.StateManager;

public class DisplayOnStateAggregator extends StateAggregator {

    public static final float VALUE_ON = 1f;
    public static final float VALUE_OFF = 0f;

    private Context context;

    public DisplayOnStateAggregator(long aggregationInterval, Context context) {
        super(StateManager.STATE_DISPLAY_ON, aggregationInterval);
        this.context = context;
    }

    @Override
    public StateData aggregate() throws DataAggregationException {
        float value = isDisplayOn(context) ? VALUE_ON : VALUE_OFF;
        return new StateData(StateManager.STATE_DISPLAY_ON, value);
    }

    public static boolean isDisplayOn(float value) {
        return value == VALUE_ON;
    }

    public static boolean isDisplayOn(Context context) {
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        if (dm == null) {
            return false;
        }
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                return true;
            }
        }
        return false;
    }

}
