package com.zurich.authenticator.data.merger;

import com.zurich.authenticator.data.generic.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DataMerger<T extends Data> {

    public static final int MEAN = 1;
    public static final int MEDIAN = 2;
    public static final int MODE = 3;

    protected Map<Integer, MergeFunction<T>> mergeFunctionMap = new HashMap<>();

    protected DataMerger() {
        setupMergeFunctions();
    }

    protected abstract void setupMergeFunctions();

    public T mergeData(List<T> dataList, int mergeMode) throws MergeException {
        MergeFunction<T> mergeFunction = getMergeFunction(mergeMode);
        return mergeFunction.merge(dataList);
    }

    public MergeFunction<T> getMergeFunction(int mergeMode) throws MergeException {
        if (!mergeFunctionMap.containsKey(mergeMode)) {
            throw new MergeException("No mergeData function available for mode: " + mergeMode);
        }
        return mergeFunctionMap.get(mergeMode);
    }

    public T getMidstData(List<T> dataList) throws MergeException {
        if (dataList.size() < 1) {
            throw new MergeException("No data available");
        }
        return dataList.get((int) Math.floor(dataList.size() / 2));
    }
}
