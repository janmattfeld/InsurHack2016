package com.zurich.authenticator.data.trustlevel.persister;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.memory.SingleTypeMemoryPersister;

public class TrustLevelMemoryPersister extends SingleTypeMemoryPersister {

    public TrustLevelMemoryPersister() {
        super(Data.TYPE_TRUST_LEVEL);
    }

    public TrustLevelMemoryPersister(int capacity) {
        super(Data.TYPE_TRUST_LEVEL, capacity);
    }

}
