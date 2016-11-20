package com.zurich.authenticator.data.aggregation;

import android.os.Handler;
import android.os.HandlerThread;

import com.zurich.authenticator.util.logging.Logger;

public abstract class TimedDataAggregator<Data> extends DataAggregator<Data> {

    protected long aggregationInterval;
    protected Handler aggregationHandler;
    protected HandlerThread aggregationThread;
    protected long lastAggregationTimestamp;

    public TimedDataAggregator(int dataType, int aggregatorType, long aggregationInterval) {
        super(dataType, aggregatorType);
        this.aggregationInterval = aggregationInterval;
    }

    @Override
    public void startAggregation() {
        try {
            aggregationThread = new HandlerThread(this.toString());
            aggregationThread.start();
            aggregationHandler = new Handler(aggregationThread.getLooper());
            isAggregating = true;
            aggregationHandler.post(getAggregationRunnable());
        } catch (Exception ex) {
            isAggregating = false;
        }
    }

    public Runnable getAggregationRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Data aggregatedData = aggregate();
                    onDataAggregated(aggregatedData, TimedDataAggregator.this);
                } catch (DataAggregationException dataAggregationException) {
                    Logger.v(this.toString(), "Data aggregation failed: " + dataAggregationException.getMessage());
                } finally {
                    lastAggregationTimestamp = System.currentTimeMillis();
                    if (isAggregating) {
                        aggregationHandler.postDelayed(this, aggregationInterval);
                    }
                }
            }
        };
    }

    @Override
    public void stopAggregation() {
        isAggregating = false;
        aggregationThread.quitSafely();
        aggregationThread = null;
    }

    public long getAggregationInterval() {
        return aggregationInterval;
    }

    public void setAggregationInterval(long aggregationInterval) {
        this.aggregationInterval = aggregationInterval;
    }

    public long getLastAggregationTimestamp() {
        return lastAggregationTimestamp;
    }

    public void setLastAggregationTimestamp(long lastAggregationTimestamp) {
        this.lastAggregationTimestamp = lastAggregationTimestamp;
    }

    public Handler getAggregationHandler() {
        return aggregationHandler;
    }

    public HandlerThread getAggregationThread() {
        return aggregationThread;
    }
}
