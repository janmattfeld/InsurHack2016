package com.zurich.authenticator.data.persister.memory;

import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersister;
import com.zurich.authenticator.data.persister.DataPersistingException;

import java.util.List;

public abstract class GenericMemoryPersister extends DataPersister {

    protected int dataType;
    protected int capacity = DataBatch.CAPACITY_DEFAULT;

    public GenericMemoryPersister(int dataType) {
        this.dataType = dataType;
    }

    public GenericMemoryPersister(int dataType, int capacity) {
        this.dataType = dataType;
        this.capacity = capacity;
    }

    public boolean canPersist(Data data) {
        return dataType == data.getType();
    }

    @Override
    public void persist(Data data) throws DataPersistingException {
        getDataBatch(data).add(data);
    }

    @Override
    public void persist(List<Data> dataList) throws DataPersistingException {
        if (dataList.size() < 1) {
            return;
        }
        Data data = dataList.get(0);
        getDataBatch(data).add(dataList);
    }

    public abstract DataBatch getDataBatch(Data data) throws DataPersistingException;

}
