package com.zurich.authenticator.data.serialization.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zurich.authenticator.data.recorder.Record;
import com.zurich.authenticator.data.serialization.SerializationException;

public class JsonSerializer {

    private Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    private static JsonSerializer instance;

    public static JsonSerializer getInstance() {
        if (instance == null) {
            instance = new JsonSerializer();
        }
        return instance;
    }

    public static String serialize(Record record) throws SerializationException {
        try {
            JsonSerializer instance = getInstance();
            return instance.gson.toJson(record);
        } catch (Exception ex) {
            throw new SerializationException("JSON Export failed: " + ex.getMessage());
        }
    }

}
