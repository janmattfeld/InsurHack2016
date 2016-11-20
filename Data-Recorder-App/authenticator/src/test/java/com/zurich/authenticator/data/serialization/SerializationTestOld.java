package com.zurich.authenticator.data.serialization;

abstract class SerializationTestOld {

    /*
    private static final String JSON_START = "{\"dataList\":[";
    private static final String JSON_VALUE = "{\"value\":";
    private static final String JSON_VALUES = "{\"values\":[";
    private static final String JSON_TIMESTAMP = ",\"timestamp\":";
    private static final String JSON_TYPE = ",\"type\":";
    private static final String JSON_SUB_TYPE = ",\"subType\":";

    String createJsonString(int type, int subType, List<Long> timestamps, float[] values) throws Exception {
        if (timestamps.size() != values.length) {
            throw new Exception("Number of Parameters is not matching");
        }
        String json = JSON_START;
        for (int i = 0; i < timestamps.size(); i++) {
            if (i != 0) {
                json += ",";
            }
            json += JSON_VALUE;
            json += values[i];
            json += JSON_TIMESTAMP + timestamps.get(i) + "}";
        }
        json += "]" + JSON_TYPE + type;
        json += JSON_SUB_TYPE + subType + "}";
        return json;
    }

    String createJsonString(int type, int subType, List<Long> timestamps, float[][] values) throws Exception {
        if (timestamps.size() != values.length) {
            throw new Exception("Number of Parameters is not matching");
        }
        String json = JSON_START;
        for (int i = 0; i < timestamps.size(); i++) {
            if (i != 0) {
                json += ",";
            }
            json += JSON_VALUES;
            for (int j = 0; j < values[i].length; j++) {
                if (j != 0) {
                    json += ",";
                }
                json += values[i][j];
            }
            json += "]";
            json += JSON_TIMESTAMP + timestamps.get(i) + "}";
        }
        json += "]" + JSON_TYPE + type;
        json += JSON_SUB_TYPE + subType + "}";
        return json;
    }
    */
}
