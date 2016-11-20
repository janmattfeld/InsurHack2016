package com.zurich.authenticator.data.persister.file;

import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.persister.DataPersister;
import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.recorder.Record;
import com.zurich.authenticator.data.serialization.SerializationException;
import com.zurich.authenticator.data.serialization.json.JsonSerializer;
import com.zurich.authenticator.util.logging.Logger;
import com.zurich.authenticator.util.markdown.MarkdownElement;
import com.zurich.authenticator.util.markdown.text.NormalText;
import com.zurich.authenticator.util.storage.ExternalStorageUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FilePersister extends DataPersister {

    private static final String TAG = FilePersister.class.getSimpleName();
    //TODO: implement persister

    @Override
    public boolean canPersist(Data data) {
        return false;
    }

    @Override
    public void persist(Data data) throws DataPersistingException {
        throw new DataPersistingException("Persister not yet implemented");
    }

    @Override
    public void persist(List<Data> dataList) throws DataPersistingException {
        throw new DataPersistingException("Persister not yet implemented");
    }

    public File persist(Record record) throws DataPersistingException {
        if (!ExternalStorageUtils.isExternalStorageWritable()) {
            throw new DataPersistingException("No access to external storage directory");
        }

        String serializedRecord;
        try {
            serializedRecord = JsonSerializer.serialize(record);
        } catch (SerializationException e) {
            throw new DataPersistingException("Unable to serialize record", e);
        }

        File exportedDirectory = ExternalStorageUtils.getDocumentsDirectory("records");
        File exportFile = new File(exportedDirectory, record.getFileName());

        try {
            ExternalStorageUtils.writeStringToFile(serializedRecord, exportFile);
            Logger.d(TAG, "Persisted record to " + exportFile);
            return exportFile;
        } catch (IOException e) {
            throw new DataPersistingException("Unable to write record to file", e);
        }
    }

    @Override
    public void clearPersistedData() {
        // TODO: evaluate if we should delete records from filesystem
    }

    @Override
    public String toString() {
        return toMarkdownElement().toString();
    }

    @Override
    public MarkdownElement toMarkdownElement() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        return new NormalText(sb);
    }

    @Override
    public void logPersistedData() {
        Logger.v(TAG, toMarkdownElement().toString());
    }

}
