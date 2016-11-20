package com.zurich.authenticator.data.persister.memory;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.util.logging.Logger;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.list.UnorderedList;
import com.zurich.authenticator.util.markdown.text.NormalText;

import java.util.HashMap;
import java.util.Map;

public abstract class MultiTypeMemoryPersister extends GenericMemoryPersister {

    private static final String TAG = MultiTypeMemoryPersister.class.getSimpleName();

    protected Map<Integer, DataBatch> dataBatchMap = new HashMap<>();

    public MultiTypeMemoryPersister(int dataType) {
        super(dataType);
    }

    public MultiTypeMemoryPersister(int dataType, int capacity) {
        super(dataType, capacity);
    }

    public abstract int getDataType(Data data);

    @Override
    public DataBatch getDataBatch(Data data) throws DataPersistingException {
        return getDataBatch(getDataType(data));
    }

    public DataBatch getDataBatch(int type) throws DataPersistingException {
        if (!dataBatchMap.containsKey(type)) {
            dataBatchMap.put(type, new DataBatch(capacity));
        }
        return dataBatchMap.get(type);
    }

    @Override
    public void clearPersistedData() {
        dataBatchMap = new HashMap<>();
    }

    @Override
    public String toString() {
        return toOverviewString();
    }

    public String toOverviewString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName())
                .append(":\n");

        UnorderedList list = new UnorderedList();
        for (Map.Entry<Integer, DataBatch> dataBatchEntry : dataBatchMap.entrySet()) {
            list.getItems().add(dataBatchEntry.getValue().toOverviewString());
        }
        sb.append(list.serialize());
        return sb.toString();
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder(toOverviewString())
                .append("\n\n");

        for (Map.Entry<Integer, DataBatch> dataBatchEntry : dataBatchMap.entrySet()) {
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            sb.append(dataBatchEntry.getValue().toDetailedString());
        }
        return sb.toString();
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        return new NormalText(toDetailedString());
    }

    @Override
    public void logPersistedData() {
        Logger.v(TAG, toOverviewString());
        MarkdownElement markdown;
        for (Map.Entry<Integer, DataBatch> dataBatchEntry : dataBatchMap.entrySet()) {
            markdown = dataBatchEntry.getValue().toMarkdownElement();
            Logger.v(TAG, markdown.toString());
        }
    }

    public Map<Integer, DataBatch> getDataBatchMap() {
        return dataBatchMap;
    }
}
