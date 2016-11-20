package com.zurich.authenticator.data.batch;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersistingException;

import java.util.ArrayList;
import java.util.List;

public class ObservableDataBatch<T extends Data> extends DataBatch implements DataBatchObserver {

    protected List<DataBatchObserver> dataBatchObservers = new ArrayList<>();

    public void addDataBatchObserver(DataBatchObserver dataBatchObserver) {
        if (!dataBatchObservers.contains(dataBatchObserver)) {
            dataBatchObservers.add(dataBatchObserver);
        }
    }

    public void removeDataBatchObserver(DataBatchObserver dataBatchObserver) {
        if (dataBatchObservers.contains(dataBatchObserver)) {
            dataBatchObservers.remove(dataBatchObserver);
        }
    }

    @Override
    public void onDataBatchCapacityReached(DataBatch dataBatch) {
        for (DataBatchObserver dataBatchObserver : dataBatchObservers) {
            dataBatchObserver.onDataBatchCapacityReached(dataBatch);
        }
    }

    private void checkIfDataBatchCapacityReached() {
        if (dataList.size() >= capacity) {
            onDataBatchCapacityReached(this);
        }
    }

    @Override
    public void add(Data data, boolean trim) throws DataPersistingException {
        super.add(data, trim);
        checkIfDataBatchCapacityReached();
    }

    @Override
    public void add(List list, boolean trim) {
        super.add(list, trim);
        checkIfDataBatchCapacityReached();
    }
}
