package com.zurich.authenticator.util;

import android.content.Context;
import android.os.Looper;

import com.zurich.authenticator.data.aggregation.TimedDataAggregator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.classification.aggregator.ClassificationAggregators;
import com.zurich.authenticator.data.classification.manager.ClassificationManager;
import com.zurich.authenticator.data.feature.aggregator.FeatureAggregators;
import com.zurich.authenticator.data.feature.manager.FeatureManager;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersister;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.PersisterManager;
import com.zurich.authenticator.data.persister.memory.MemoryPersister;
import com.zurich.authenticator.data.persister.memory.MultiTypeMemoryPersister;
import com.zurich.authenticator.data.persister.memory.SingleTypeMemoryPersister;
import com.zurich.authenticator.data.recorder.Record;
import com.zurich.authenticator.data.recorder.SensorDataRecorder;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.sensor.persister.SensorEventMemoryPersister;
import com.zurich.authenticator.data.state.aggregator.StateAggregators;
import com.zurich.authenticator.data.state.manager.StateManager;
import com.zurich.authenticator.data.trustlevel.aggregator.TrustLevelAggregators;
import com.zurich.authenticator.data.trustlevel.manager.TrustLevelManager;
import com.zurich.authenticator.util.logging.Logger;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowSystemClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.robolectric.Shadows.shadowOf;

public class Recalculator {

    private static final String TAG = Recalculator.class.getSimpleName();

    private Context context = mock(Context.class);

    private Record record;

    private long mockedStartTimestamp;

    public Recalculator(Record record) {
        this.record = record;
        initialize();
    }

    protected void initialize() {
        PersisterManager.initialize(context);
        clearPersistedData();
        mockSystemClock();
        mockSensorEventMemoryPersister();
        setupDataManagers();
    }

    protected void setupDataManagers() {
        FeatureManager.initialize(context);
        StateManager.initialize(context);
        ClassificationManager.initialize(context);
        TrustLevelManager.initialize(context);
    }

    /**
     * Clears all data currently sored in any memory persister
     */
    protected void clearPersistedData() {
        Logger.v(TAG, "Clearing persisted data");
        try {
            MemoryPersister memoryPersister = (MemoryPersister) PersisterManager.getDataPersister(PersisterManager.STRATEGY_MEMORY);
            for (Map.Entry<Integer, DataPersister> dataPersisterEntry : memoryPersister.getDataPersisterMap().entrySet()) {
                dataPersisterEntry.getValue().clearPersistedData();
            }
        } catch (DataPersistingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adjusts the System clock to return mocked results.
     */
    private void mockSystemClock() {
        mockedStartTimestamp = record.getRecorder().getStartTimestamp();
        ShadowSystemClock.setCurrentTimeMillis(mockedStartTimestamp);
    }

    /**
     * Adjusts the {@link SensorEventMemoryPersister} in order to let it return
     * {@link SensorEventData} from the current {@link Record} instead of the
     * actual available data.
     */
    private void mockSensorEventMemoryPersister() {
        try {
            // get a reference to the sensor event memory persister, as we need
            // to modify the data batches that it holds
            MemoryPersister memoryPersister = (MemoryPersister) PersisterManager.getDataPersister(PersisterManager.STRATEGY_MEMORY);
            SensorEventMemoryPersister sensorEventMemoryPersister = (SensorEventMemoryPersister) memoryPersister.getPersister(Data.TYPE_SENSOR_EVENT);

            // invoke creation of data batches for every recorded sensor type
            for (Integer sensorType : ((SensorDataRecorder) record.getRecorder()).getSensorTypes()) {
                // invoke lazy initialization
                DataBatch dataBatch = sensorEventMemoryPersister.getDataBatch(sensorType);

                // set type and subtype, as we'll never add real data
                dataBatch.setType(Data.TYPE_SENSOR_EVENT);
                dataBatch.setSubType(sensorType);
            }

            // get a map of recorded data batches by sensor type
            final Map<Integer, DataBatch> recordedSensorDataBatches = record.getSensorEventDataBatchMap();

            // mock all available data batches
            Map<Integer, DataBatch> mockedDataBatches = new HashMap<>();
            for (Map.Entry<Integer, DataBatch> dataBatchEntry : sensorEventMemoryPersister.getDataBatchMap().entrySet()) {
                DataBatch mockedDataBatch = Mockito.spy(dataBatchEntry.getValue());

                // let each data batch return the requested data from the record
                Answer<List<SensorEventData>> mockedAnswer = createMockedDataBatchAnswer(recordedSensorDataBatches.get(dataBatchEntry.getKey()));
                doAnswer(mockedAnswer).when(mockedDataBatch).getDataSince(anyLong());

                mockedDataBatches.put(dataBatchEntry.getKey(), mockedDataBatch);
            }

            // overwrite real data batches with mocked data batches
            for (Map.Entry<Integer, DataBatch> mockedDataBatchEntry : mockedDataBatches.entrySet()) {
                sensorEventMemoryPersister.getDataBatchMap().put(mockedDataBatchEntry.getKey(), mockedDataBatchEntry.getValue());
            }
        } catch (DataPersistingException e) {
            e.printStackTrace();
        }
    }

    private Answer<List<SensorEventData>> createMockedDataBatchAnswer(final DataBatch recordedDataBatch) {
        return new Answer<List<SensorEventData>>() {
            @Override
            public List<SensorEventData> answer(InvocationOnMock invocation) throws Throwable {
                // get mapped timestamp
                long requestedTimestamp = (long) invocation.getArguments()[0];
                long mappedStartTimestamp = getMappedTimestamp(requestedTimestamp);
                long mappedEndTimestamp = getMappedTimestamp(ShadowSystemClock.currentTimeMillis());

                // return matching data from the recorded data batches
                List<SensorEventData> dataList = new ArrayList<>();
                if (recordedDataBatch != null) {
                    List<SensorEventData> recordedDataList = recordedDataBatch.getDataBetween(mappedStartTimestamp, mappedEndTimestamp);
                    dataList.addAll(recordedDataList);
                }
                return dataList;
            }
        };
    }

    public void triggerAllAggregators() {
        // create aggregator map with every available aggregator
        Map<Integer, TimedDataAggregator> aggregatorMap = new HashMap<>();
        aggregatorMap.putAll(FeatureAggregators.getInstance().getAggregatorMap());
        aggregatorMap.putAll(StateAggregators.getInstance().getAggregatorMap());
        aggregatorMap.putAll(ClassificationAggregators.getInstance().getAggregatorMap());
        aggregatorMap.putAll(TrustLevelAggregators.getInstance().getAggregatorMap());

        // create shadow loopers for all aggregators
        Map<Integer, ShadowLooper> shadowLooperMap = createShadowLoopers(aggregatorMap);

        // set duration according to record
        long duration = record.getRecorder().getStopTimestamp() - record.getRecorder().getStartTimestamp();

        // trigger all aggregators
        triggerAggregators(aggregatorMap, shadowLooperMap, duration);
    }

    public static void triggerAggregators(Map<Integer, TimedDataAggregator> aggregatorMap, Map<Integer, ShadowLooper> shadowLooperMap, long duration) {
        long mockingInterval = 5;
        for (long milliseconds = 0; milliseconds <= duration; milliseconds += mockingInterval) {

            // advance the system clock
            advanceSystemClock(mockingInterval);

            // trigger aggregators if required
            for (Map.Entry<Integer, TimedDataAggregator> aggregatorEntry : aggregatorMap.entrySet()) {
                TimedDataAggregator aggregator = aggregatorEntry.getValue();
                if (shouldAggregate(aggregator)) {
                    shadowLooperMap.get(aggregatorEntry.getKey()).runOneTask();
                }
            }
        }
    }

    /**
     * Checks if the specified aggregator should be triggered based on
     * the currently mocked timestamp.
     *
     * @param aggregator
     * @return
     */
    private static boolean shouldAggregate(TimedDataAggregator aggregator) {
        long nextScheduledAggregation = aggregator.getLastAggregationTimestamp() + aggregator.getAggregationInterval();
        return nextScheduledAggregation < ShadowSystemClock.currentTimeMillis();
    }

    /**
     * Creates a ShadowLooper for each {@link TimedDataAggregator} from the
     * {@link Looper} of the aggregators {@link android.os.HandlerThread}.
     * This will be used to adjust the job runner.
     *
     * @param aggregatorMap
     * @return
     * @see <a href="http://robolectric.org/javadoc/3.0/org/robolectric/shadows/ShadowLooper.html">ShadowLooper</a>
     */
    public static Map<Integer, ShadowLooper> createShadowLoopers(Map<Integer, TimedDataAggregator> aggregatorMap) {
        Map<Integer, ShadowLooper> shadowLooperMap = new HashMap<>();
        for (Map.Entry<Integer, TimedDataAggregator> aggregatorEntry : aggregatorMap.entrySet()) {
            TimedDataAggregator aggregator = aggregatorEntry.getValue();
            Looper looper = ShadowLooper.getLooperForThread(aggregator.getAggregationThread());
            ShadowLooper shadowLooper = shadowOf(looper);
            shadowLooperMap.put(aggregator.getAggregatorType(), shadowLooper);
        }
        return shadowLooperMap;
    }

    /**
     * Advances the system clock by the specified amount of milliseconds
     *
     * @param milliseconds
     */
    private static void advanceSystemClock(long milliseconds) {
        ShadowSystemClock.setCurrentTimeMillis(ShadowSystemClock.currentTimeMillis() + milliseconds);
    }

    /**
     * Maps the mocked start timestamp (~ now) to the {@link Record}
     * start timestamp (whenever the data has been recorded).
     *
     * @param mockedTimestamp the timestamp from the mocked system clock
     * @return the mapped timestamp from the {@link Record}
     */
    private long getMappedTimestamp(long mockedTimestamp) {
        return record.getRecorder().getStartTimestamp() + (mockedTimestamp - mockedStartTimestamp);
    }

    public Record getRecalculatedRecord() {
        Logger.i(TAG, "Recalculating record: " + record.getFileName());

        triggerAllAggregators();

        Record recalculatedRecord = new Record(record);

        // remove all data batches from record (except sensor events)
        List<DataBatch> sensorEventDataBatches = new ArrayList<>();
        for (DataBatch dataBatch : recalculatedRecord.getDataBatches()) {
            if (dataBatch.getType() == Data.TYPE_SENSOR_EVENT) {
                sensorEventDataBatches.add(dataBatch);
            }
        }
        recalculatedRecord.setDataBatches(sensorEventDataBatches);

        try {
            // add currently persisted data to record (except sensor events)
            MemoryPersister memoryPersister = (MemoryPersister) PersisterManager.getDataPersister(PersisterManager.STRATEGY_MEMORY);
            for (Map.Entry<Integer, DataPersister> dataPersisterEntry : memoryPersister.getDataPersisterMap().entrySet()) {
                if (dataPersisterEntry.getKey() == Data.TYPE_SENSOR_EVENT) {
                    // sensor events are already available
                    continue;
                } else if (dataPersisterEntry.getKey() == Data.TYPE_TRUST_LEVEL) {
                    SingleTypeMemoryPersister persister = (SingleTypeMemoryPersister) dataPersisterEntry.getValue();
                    recalculatedRecord.getDataBatches().add(persister.getDataBatch());
                } else {
                    MultiTypeMemoryPersister persister = (MultiTypeMemoryPersister) dataPersisterEntry.getValue();
                    for (Map.Entry<Integer, DataBatch> dataBatchEntry : persister.getDataBatchMap().entrySet()) {
                        recalculatedRecord.getDataBatches().add(dataBatchEntry.getValue());
                    }
                }
            }
        } catch (DataPersistingException e) {
            e.printStackTrace();
        }

        return recalculatedRecord;
    }

}
