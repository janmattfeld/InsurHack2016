package com.zurich.authenticator.data.persister;

import android.content.Context;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.database.DataBasePersister;
import com.zurich.authenticator.data.persister.file.FilePersister;
import com.zurich.authenticator.data.persister.memory.MemoryPersister;
import com.zurich.authenticator.util.logging.Logger;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.MarkdownSerializable;
import com.zurich.authenticator.util.markdown.text.NormalText;

import java.util.List;

public final class PersisterManager implements MarkdownSerializable {

    private static final String TAG = PersisterManager.class.getSimpleName();

    public static final int STRATEGY_MEMORY = 1;
    public static final int STRATEGY_DATABASE = 2;
    public static final int STRATEGY_FILE = 3;

    private static PersisterManager instance;
    private boolean initialized = false;

    private DataBasePersister dataBasePersister;
    private MemoryPersister memoryPersister;
    private FilePersister filePersister;

    private PersisterManager() {

    }

    public static PersisterManager getInstance() {
        if (instance == null) {
            instance = new PersisterManager();
        }
        return instance;
    }

    public static void initialize(Context context) {
        Logger.d(TAG, "initializeManager() called with: context = [" + context + "]");
        PersisterManager instance = getInstance();
        if (instance.initialized) {
            return;
        }
        instance.memoryPersister = new MemoryPersister();
        instance.dataBasePersister = new DataBasePersister(context);
        instance.filePersister = new FilePersister();
        instance.initialized = true;
    }

    public static DataPersister getDataPersister(int strategy) throws DataPersistingException {
        PersisterManager instance = getInstance();
        switch (strategy) {
            case STRATEGY_MEMORY: {
                return instance.memoryPersister;
            }
            case STRATEGY_DATABASE: {
                return instance.dataBasePersister;
            }
            case STRATEGY_FILE: {
                return instance.filePersister;
            }
            default: {
                throw new DataPersistingException("Unknown persistence strategy: " + strategy);
            }
        }
    }

    public static void persist(Data data, int strategy) throws DataPersistingException {
        DataPersister dataPersister = getDataPersister(strategy);
        dataPersister.persist(data);
    }

    public static void persist(List<Data> dataList, int strategy) throws DataPersistingException {
        DataPersister dataPersister = getDataPersister(strategy);
        dataPersister.persist(dataList);
    }

    @Override
    public String toString() {
        return toMarkdownElement().toString();
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName())
                .append(":\n\n");

        sb.append(memoryPersister.toMarkdownElement()).append("\n\n");
        sb.append(dataBasePersister.toMarkdownElement()).append("\n\n");
        sb.append(filePersister.toMarkdownElement());

        return new NormalText(sb);
    }

    public static void logPersistedData() {
        try {
            getDataPersister(PersisterManager.STRATEGY_MEMORY).logPersistedData();
            getDataPersister(PersisterManager.STRATEGY_DATABASE).logPersistedData();
            getDataPersister(PersisterManager.STRATEGY_FILE).logPersistedData();
        } catch (DataPersistingException ex) {
            Logger.e(TAG, "logPersistedData:", ex);
        }
    }

    public MemoryPersister getMemoryPersister() {
        return memoryPersister;
    }

    public DataBasePersister getDataBasePersister() {
        return dataBasePersister;
    }

    public FilePersister getFilePersister() {
        return filePersister;
    }

}
