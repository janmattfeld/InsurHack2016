package com.zurich.authenticator.data.DummyData;

import com.zurich.authenticator.data.generic.Data;

public interface DummyDataManipulator<T extends Data> {

    T manipulateData(int index, T data);

}
