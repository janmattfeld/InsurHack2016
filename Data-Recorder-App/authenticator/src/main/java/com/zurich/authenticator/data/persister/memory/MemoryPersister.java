package com.zurich.authenticator.data.persister.memory;

import com.zurich.authenticator.data.classification.persister.ClassificationMemoryPersister;
import com.zurich.authenticator.data.feature.persister.FeatureMemoryPersister;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersister;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.sensor.persister.SensorEventMemoryPersister;
import com.zurich.authenticator.data.state.persister.StateMemoryPersister;
import com.zurich.authenticator.data.trustlevel.persister.TrustLevelMemoryPersister;
import com.zurich.authenticator.util.logging.Logger;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.list.UnorderedList;
import com.zurich.authenticator.util.markdown.text.NormalText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryPersister extends DataPersister {

    private static final String TAG = MemoryPersister.class.getSimpleName();

    private Map<Integer, DataPersister> dataPersisterMap = new HashMap<>();

    public MemoryPersister() {
        dataPersisterMap.put(Data.TYPE_SENSOR_EVENT, new SensorEventMemoryPersister());
        dataPersisterMap.put(Data.TYPE_FEATURE, new FeatureMemoryPersister());
        dataPersisterMap.put(Data.TYPE_STATE, new StateMemoryPersister());
        dataPersisterMap.put(Data.TYPE_CLASSIFICATION, new ClassificationMemoryPersister());
        dataPersisterMap.put(Data.TYPE_TRUST_LEVEL, new TrustLevelMemoryPersister());
    }

    @Override
    public boolean canPersist(Data data) {
        try {
            getPersister(data);
            return true;
        } catch (DataPersistingException exception) {
            return false;
        }
    }

    @Override
    public void persist(Data data) throws DataPersistingException {
        getPersister(data).persist(data);
    }

    @Override
    public void persist(List<Data> dataList) throws DataPersistingException {
        if (dataList.size() < 1) {
            return;
        }
        getPersister(dataList.get(0)).persist(dataList);
    }

    public DataPersister getPersister(Data data) throws DataPersistingException {
        return getPersister(data.getType());
    }

    public DataPersister getPersister(int dataType) throws DataPersistingException {
        if (!dataPersisterMap.containsKey(dataType)) {
            throw new DataPersistingException("No persister available for " + dataType);
        }
        DataPersister dataPersister = dataPersisterMap.get(dataType);
        return dataPersister;
    }

    @Override
    public void clearPersistedData() {
        for (Map.Entry<Integer, DataPersister> dataPersisterEntry : dataPersisterMap.entrySet()) {
            dataPersisterEntry.getValue().clearPersistedData();
        }
    }

    @Override
    public String toString() {
        return toMarkdownElement().toString();
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName())
                .append("\n\n");

        UnorderedList list = new UnorderedList();
        for (Map.Entry<Integer, DataPersister> dataPersisterEntry : dataPersisterMap.entrySet()) {
            list.getItems().add(dataPersisterEntry.getValue().toMarkdownElement());
        }
        sb.append(list.serialize());

        return new NormalText(sb);
    }

    @Override
    public void logPersistedData() {
        try {
            getPersister(Data.TYPE_SENSOR_EVENT).logPersistedData();
            getPersister(Data.TYPE_FEATURE).logPersistedData();
            getPersister(Data.TYPE_STATE).logPersistedData();
            getPersister(Data.TYPE_CLASSIFICATION).logPersistedData();
            getPersister(Data.TYPE_TRUST_LEVEL).logPersistedData();
        } catch (DataPersistingException ex) {
            Logger.e(TAG, "logPersistedData: ", ex);
        }
    }

    public Map<Integer, DataPersister> getDataPersisterMap() {
        return dataPersisterMap;
    }

}
