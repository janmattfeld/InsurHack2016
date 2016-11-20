package com.zurich.authenticator.data.persister;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.util.markdown.MarkdownSerializable;

import java.util.List;

public abstract class DataPersister implements MarkdownSerializable {

    public abstract boolean canPersist(Data data);

    public abstract void persist(Data data) throws DataPersistingException;

    public abstract void persist(List<Data> dataList) throws DataPersistingException;

    public abstract void logPersistedData();

    public abstract void clearPersistedData();

}