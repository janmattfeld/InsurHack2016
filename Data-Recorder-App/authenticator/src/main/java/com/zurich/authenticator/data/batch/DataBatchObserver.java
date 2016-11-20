package com.zurich.authenticator.data.batch;

public interface DataBatchObserver {

    void onDataBatchCapacityReached(DataBatch dataBatch);

}
