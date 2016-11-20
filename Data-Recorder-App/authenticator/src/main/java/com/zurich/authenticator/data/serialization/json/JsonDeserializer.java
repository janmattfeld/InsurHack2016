package com.zurich.authenticator.data.serialization.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.generic.Data;
import com.zurich.authenticator.data.recorder.DataRecorder;
import com.zurich.authenticator.data.recorder.Record;
import com.zurich.authenticator.data.recorder.SensorDataRecorder;
import com.zurich.authenticator.data.sensor.SensorEventData;
import com.zurich.authenticator.data.serialization.DeserializationException;
import com.zurich.authenticator.data.state.StateData;
import com.zurich.authenticator.data.trustlevel.TrustLevelData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonDeserializer implements com.google.gson.JsonDeserializer<Record> {

    public static final String TAG = JsonDeserializer.class.getSimpleName();

    public static final TypeToken<DataBatch<SensorEventData>> TOKEN_SENSOR_EVENT = new TypeToken<DataBatch<SensorEventData>>() {
    };
    public static final TypeToken<DataBatch<StateData>> TOKEN_STATE = new TypeToken<DataBatch<StateData>>() {
    };
    public static final TypeToken<DataBatch<FeatureData>> TOKEN_FEATURE = new TypeToken<DataBatch<FeatureData>>() {
    };
    public static final TypeToken<DataBatch<ClassificationData>> TOKEN_CLASSIFICATION = new TypeToken<DataBatch<ClassificationData>>() {
    };
    public static final TypeToken<DataBatch<TrustLevelData>> TOKEN_TRUST_LEVEL = new TypeToken<DataBatch<TrustLevelData>>() {
    };
    public static final TypeToken<DataBatch<Data>> TOKEN_DATA = new TypeToken<DataBatch<Data>>() {
    };

    private Gson gson;

    private static JsonDeserializer instance;

    private JsonDeserializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Record.class, this);
        gson = gsonBuilder.create();
    }

    public static JsonDeserializer getInstance() {
        if (instance == null) {
            instance = new JsonDeserializer();
        }
        return instance;
    }

    public static Record deserializeRecord(String jsonString) throws DeserializationException {
        try {
            JsonDeserializer instance = getInstance();
            Record record = instance.gson.fromJson(jsonString, Record.class);
            return record;
        } catch (Exception ex) {
            throw new DeserializationException("Unable to deserialize JSON", ex);
        }
    }

    @Override
    public Record deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int version = json.getAsJsonObject().get("version").getAsInt();
        return deserialize(json, typeOfT, context, version);
    }

    public Record deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context, int version) throws JsonParseException {
        if (version != Record.VERSION_1) {
            throw new JsonParseException("Deserializer can't handle Records with version " + version);
        }

        Record record = new Record();
        JsonObject rootObject = json.getAsJsonObject();

        // generic
        record.setVersion(version);
        record.setUser(rootObject.get("user").getAsString());
        record.setLabel(rootObject.get("label").getAsString());
        record.setComment(rootObject.get("comment").getAsString());
        record.setDeviceName(rootObject.get("deviceName").getAsString());
        record.setDeviceId(rootObject.get("deviceId").getAsString());

        // data batches
        int dataType = Data.TYPE_NOT_SET;
        JsonArray dataBatchJsonArray = rootObject.getAsJsonArray("dataBatches");
        List<DataBatch> dataBatches = new ArrayList<>(dataBatchJsonArray.size());
        for (int i = 0; i < dataBatchJsonArray.size(); i++) {
            JsonObject dataBatchJsonObject = dataBatchJsonArray.get(i).getAsJsonObject();
            dataType = dataBatchJsonObject.get("type").getAsInt();
            DataBatch dataBatch = gson.fromJson(dataBatchJsonObject, getDataBatchType(dataType));
            dataBatch.applySubType();
            dataBatches.add(dataBatch);
        }
        record.setDataBatches(dataBatches);

        // recorder
        DataRecorder recorder = (DataRecorder) gson.fromJson(rootObject.getAsJsonObject("recorder"), getRecorderClass(dataType));
        record.setRecorder(recorder);

        return record;
    }

    public static Class getRecorderClass(int dataType) {
        switch (dataType) {
            case Data.TYPE_SENSOR_EVENT:
                return SensorDataRecorder.class;
            default:
                return DataRecorder.class;
        }
    }

    public static Type getDataBatchType(int dataType) {
        switch (dataType) {
            case Data.TYPE_SENSOR_EVENT:
                return TOKEN_SENSOR_EVENT.getType();
            case Data.TYPE_STATE:
                return TOKEN_STATE.getType();
            case Data.TYPE_CLASSIFICATION:
                return TOKEN_CLASSIFICATION.getType();
            case Data.TYPE_FEATURE:
                return TOKEN_FEATURE.getType();
            case Data.TYPE_TRUST_LEVEL:
                return TOKEN_TRUST_LEVEL.getType();
            default:
                return TOKEN_DATA.getType();
        }
    }

}
