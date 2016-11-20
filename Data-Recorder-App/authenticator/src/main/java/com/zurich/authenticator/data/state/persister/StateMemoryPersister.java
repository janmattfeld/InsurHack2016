package com.zurich.authenticator.data.state.persister;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.memory.MultiTypeMemoryPersister;
import com.zurich.authenticator.data.state.StateData;

public class StateMemoryPersister extends MultiTypeMemoryPersister {

    public StateMemoryPersister() {
        super(Data.TYPE_STATE);
    }

    public StateMemoryPersister(int capacity) {
        super(Data.TYPE_STATE, capacity);
    }

    @Override
    public int getDataType(Data data) {
        return ((StateData) data).getStateType();
    }

}
