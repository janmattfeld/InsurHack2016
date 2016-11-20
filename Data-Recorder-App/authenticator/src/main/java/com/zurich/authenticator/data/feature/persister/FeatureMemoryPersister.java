package com.zurich.authenticator.data.feature.persister;

import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.memory.MultiTypeMemoryPersister;

public class FeatureMemoryPersister extends MultiTypeMemoryPersister {

    public FeatureMemoryPersister() {
        super(Data.TYPE_FEATURE);
    }

    public FeatureMemoryPersister(int capacity) {
        super(Data.TYPE_FEATURE, capacity);
    }

    @Override
    public int getDataType(Data data) {
        return ((FeatureData) data).getFeatureType();
    }

}
