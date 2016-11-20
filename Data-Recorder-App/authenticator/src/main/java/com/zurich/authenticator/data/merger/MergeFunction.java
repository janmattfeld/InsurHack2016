package com.zurich.authenticator.data.merger;

import com.zurich.authenticator.data.generic.Data;

import java.util.List;

public abstract class MergeFunction<T extends Data> {

    public abstract T merge(List<T> dataList) throws MergeException;

}
