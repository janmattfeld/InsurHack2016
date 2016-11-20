package com.zurich.authenticator.util;

public final class StringUtils {

    public static String serializeToCsv(String[] values) {
        if (values == null || values.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    public static String serializeToCsv(float[] values) {
        if (values == null || values.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(String.format(java.util.Locale.US, "%.4f", values[i]));
        }
        return sb.toString();
    }

    public static String[] deserializeCsvToStrings(String csv) {
        if (csv == null || csv.length() == 0) {
            return new String[]{};
        }
        return csv.split(", ");
    }

    public static float[] deserializeCsvToFloats(String csv) {
        if (csv == null || csv.length() == 0) {
            return new float[]{};
        }
        String[] stringValues = csv.split(", ");
        float[] values = new float[stringValues.length];
        for (int i = 0; i < stringValues.length; i++) {
            values[i] = Float.parseFloat(stringValues[i]);
        }
        return values;
    }

}
