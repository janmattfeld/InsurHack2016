package com.zurich.authenticator.data.persister.memory;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.util.logging.Logger;
import com.zurich.authenticator.util.markdown.MarkdownElement;

public class SingleTypeMemoryPersister extends GenericMemoryPersister {

    private static final String TAG = SingleTypeMemoryPersister.class.getSimpleName();

    protected DataBatch dataBatch = new DataBatch();

    public SingleTypeMemoryPersister(int dataType) {
        super(dataType);
    }

    public SingleTypeMemoryPersister(int dataType, int capacity) {
        super(dataType, capacity);
    }

    public DataBatch getDataBatch() {
        return dataBatch;
    }

    @Override
    public DataBatch getDataBatch(Data data) throws DataPersistingException {
        return getDataBatch();
    }

    @Override
    public void clearPersistedData() {
        dataBatch = new DataBatch();
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        return dataBatch.toMarkdownElement();
    }

    @Override
    public void logPersistedData() {
        Logger.v(TAG, toMarkdownElement().toString());
    }

}
