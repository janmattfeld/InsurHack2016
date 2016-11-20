package com.zurich.authenticator.data.classification.persister;

import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.memory.MultiTypeMemoryPersister;

public class ClassificationMemoryPersister extends MultiTypeMemoryPersister {

    public ClassificationMemoryPersister() {
        super(Data.TYPE_CLASSIFICATION);
    }

    public ClassificationMemoryPersister(int capacity) {
        super(Data.TYPE_CLASSIFICATION, capacity);
    }

    @Override
    public int getDataType(Data data) {
        return ((ClassificationData) data).getClassificationType();
    }

}
