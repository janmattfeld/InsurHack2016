package com.zurich.authenticator.util;

import com.google.common.io.Files;
import com.zurich.authenticator.data.recorder.Record;
import com.zurich.authenticator.data.serialization.DeserializationException;
import com.zurich.authenticator.data.serialization.SerializationException;
import com.zurich.authenticator.data.serialization.json.JsonDeserializer;
import com.zurich.authenticator.data.serialization.json.JsonSerializer;
import com.zurich.authenticator.util.storage.ExternalStorageUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public abstract class RecordsUtil {

    public static final String FILE_TYPE_RECORDED_RECORD = "record";
    public static final String FILE_TYPE_RECALCULATED_RECORD = "recalculated";
    public static final String FOLDER_RECORDED_RECORDS = "recorded_records";
    public static final String FOLDER_RECALCULATED_RECORDS = "recalculated_records";
    public static final String FOLDER_PHONE = "phone";
    public static final String FOLDER_WEAR = "wear";

    public static final List<String> PHONE_DEVICE_NAMES = Arrays.asList(
            "sailfish" // Pixel
    );

    public static final List<String> WEAR_DEVICE_NAMES = Arrays.asList(
            "todo" // Huawei Watch
    );

    public static Record getRecordFromFile(File file) throws DeserializationException {
        String json;
        try {
            json = readFile(file);
        } catch (IOException e) {
            throw new DeserializationException("Unable to read file: " + file, e);
        }
        return JsonDeserializer.deserializeRecord(json);
    }

    public static File getResourcesDirectory() {
        String resourcesPath = System.getProperty("user.dir") + "/src/test/resources";
        File resourcesDirectory = new File(resourcesPath);
        return resourcesDirectory;
    }

    public static File getResourcesDirectory(String subfolder) {
        File file = new File(getResourcesDirectory(), subfolder);
        file.mkdirs();
        return file;
    }

    public static List<File> getRecordFilesWithLabel(String activity) {
        List<File> files = getRecordFilesFromResourcesDirectory();
        List<File> activityFiles = new ArrayList<>();
        for (File file : files) {
            // TODO: adjust parsing
            if (file.getName().contains(activity.toLowerCase())) {
                activityFiles.add(file);
            }
        }
        return activityFiles;
    }

    public static List<File> getRecordFilesFromResourcesDirectory() {
        return getRecordFilesFromDirectory(getResourcesDirectory());
    }

    public static List<File> getRecordFilesFromDirectory(File directory) {
        return ExternalStorageUtils.getJsonFilesInDirectory(directory);
    }

    private static File getFileFromPath(Object obj, String fileName) throws IOException {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IOException("File doesn't exist: " + fileName);
        }
        URI uri;
        try {
            uri = new URI(resource.toString());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        String path = uri.getPath();
        return new File(path);
    }

    public static String readResource(Object obj, String fileName) throws IOException {
        return readFile(getFileFromPath(obj, fileName));
    }

    private static String readFile(File file) throws IOException {
        return new Scanner(file).useDelimiter("\\Z").next();
    }

    public static void exportRecalculatedRecord(Record record) {
        String fileName = record.getFileName();
        fileName = fileName.replace(FILE_TYPE_RECORDED_RECORD, FILE_TYPE_RECALCULATED_RECORD);
        File file = new File(getResourcesDirectory(FOLDER_RECALCULATED_RECORDS), fileName);
        try {
            Files.createParentDirs(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writeRecordToFile(record, file);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    public static File writeRecordToFile(Record record, File file) throws SerializationException {
        String json = JsonSerializer.serialize(record);
        return writeFile(json, file);
    }

    public static File writeFile(String data, File file) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }
        return file;
    }

    public static String getPartFromFileName(String fileName, int index) throws IOException {
        try {
            return fileName.split("_")[index];
        } catch (Exception e) {
            throw new IOException("Unable to get part " + index + " of file: " + fileName);
        }
    }

    public static String getFileTypeFromFileName(String fileName) throws IOException {
        return getPartFromFileName(fileName, 0);
    }

    public static String getDeviceNameFromFileName(String fileName) throws IOException {
        return getPartFromFileName(fileName, 1);
    }

    public static String getLabelFromFileName(String fileName) throws IOException {
        return getPartFromFileName(fileName, 2);
    }

    public static String getCommentFromFileName(String fileName) throws IOException {
        return getPartFromFileName(fileName, 3);
    }

    public static String getUserFromFileName(String fileName) throws IOException {
        return getPartFromFileName(fileName, 4);
    }

    public static String getStartTimestampFromFileName(String fileName) throws IOException {
        return getPartFromFileName(fileName, 5);
    }

}
