package com.zurich.authenticator.data.recorder;

import android.content.Context;

import com.zurich.authenticator.data.generic.Data;

import java.util.ArrayList;
import java.util.List;

public class DataRecorderBuilder {

    private int dataType;
    private long startTimestamp;
    private long stopTimestamp;
    private List<Integer> types = new ArrayList<>();
    private boolean useAllAvailableTypes;
    private Context context;
    private RecordingObserver recordingObserver;

    private DataRecorderBuilder() {
    }

    /**
     * Sets the type of {@link Data} to be recorded.
     *
     * @param dataType a TYPE constant from {@link Data}
     * @return the {@link DataRecorderBuilder} instance
     */
    private static DataRecorderBuilder forData(Context context, int dataType) {
        DataRecorderBuilder instance = new DataRecorderBuilder();
        instance.dataType = dataType;
        instance.context = context;
        return instance;
    }

    /**
     * Convenience method for forData(Data.TYPE_SENSOR_EVENT).
     *
     * @return the {@link DataRecorderBuilder} instance
     */
    public static DataRecorderBuilder forSensorEventData(Context context) {
        return forData(context, Data.TYPE_SENSOR_EVENT);
    }

    /**
     * Sets the data (sub-) types to record.
     *
     * @param types the data (sub-) types
     * @return the {@link DataRecorderBuilder} instance
     */
    public DataRecorderBuilder withTypes(List<Integer> types) {
        this.types = types;
        return this;
    }

    /**
     * Indicates that the recorder should record data
     * from all available types.
     *
     * @return the {@link DataRecorderBuilder} instance
     */
    public DataRecorderBuilder withAllAvailableTypes() {
        useAllAvailableTypes = true;
        return this;
    }

    public DataRecorderBuilder withObserver(RecordingObserver recordingObserver) {
        this.recordingObserver = recordingObserver;
        return this;
    }

    /**
     * Sets the start timestamp for the recorder. If this timestamp
     * is in the past, the recorder may start recording instantly.
     *
     * @param startTimestamp the start timestamp
     * @return the {@link DataRecorderBuilder} instance
     */
    public DataRecorderBuilder startingAt(long startTimestamp) {
        this.startTimestamp = startTimestamp;
        return this;
    }

    /**
     * Convenience method that sets the start timestamp to the
     * current timestamp.
     *
     * @return the {@link DataRecorderBuilder} instance
     */
    public DataRecorderBuilder startingNow() {
        return startingAt(System.currentTimeMillis());
    }

    /**
     * Convenience method that sets the start timestamp to the
     * current timestamp plus the specified milliseconds.
     *
     * @param milliseconds the offset from now in milliseconds
     * @return the {@link DataRecorderBuilder} instance
     */
    public DataRecorderBuilder startingIn(long milliseconds) {
        return startingAt(System.currentTimeMillis() + milliseconds);
    }

    /**
     * Sets the stop timestamp for the recorder. Should be greater
     * than the start timestamp.
     *
     * @param stopTimestamp the stop timestamp
     * @return the {@link DataRecorderBuilder} instance
     */
    public DataRecorderBuilder stoppingAt(long stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
        return this;
    }

    /**
     * Sets the stop timestamp for the recorder to the start
     * timestamp plus the specified duration. Should be called
     * after setting the start timestamp.
     *
     * @param duration the recording duration
     * @return the {@link DataRecorderBuilder} instance
     */
    public DataRecorderBuilder stoppingAfter(long duration) {
        this.stopTimestamp = startTimestamp + duration;
        return this;
    }

    /**
     * Actually instantiates a {@link DataRecorder} for the
     * specified data type.
     *
     * @return a {@link DataRecorder} instance
     * @throws RecorderException if parameters are not valid
     */
    public DataRecorder build() throws RecorderException {
        validateConfiguration();
        switch (dataType) {
            case Data.TYPE_SENSOR_EVENT: {
                return new SensorDataRecorder(this);
            }
            default: {
                throw new RecorderException("No recorder available for type: " + dataType);
            }
        }
    }

    /**
     * Checks if the builder can build a functional {@link DataRecorder}
     * with the current configuration.
     *
     * @throws RecorderException if an invalid configuration was detected
     */
    private void validateConfiguration() throws RecorderException {
        if (stopTimestamp > 0 && stopTimestamp <= System.currentTimeMillis()) {
            throw new RecorderException("Stop timestamp should be in the future");
        }

        if (stopTimestamp > 0 && stopTimestamp < startTimestamp) {
            throw new RecorderException("Stop timestamp should be larger than the start timestamp");
        }

        if (!useAllAvailableTypes && types == null) {
            throw new RecorderException("No types specified");
        }
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getStopTimestamp() {
        return stopTimestamp;
    }

    public List<Integer> getTypes() {
        return types;
    }

    public boolean shouldUseAllAvailableTypes() {
        return useAllAvailableTypes;
    }

    public Context getContext() {
        return context;
    }

    public RecordingObserver getRecordingObserver() {
        return recordingObserver;
    }

}
