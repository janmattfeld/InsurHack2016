package com.zurich.authenticator.util.storage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.zurich.authenticator.util.logging.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExternalStorageUtils {

    private static final String TAG = ExternalStorageUtils.class.getSimpleName();

    /**
     * Checks if external storage is available for read and write
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to at least read
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getDocumentsDirectory(String subFolder) {
        File file;
        if (subFolder != null) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), subFolder);
        } else {
            file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        }
        file.mkdirs();
        return file;
    }

    public static File getCacheDirectory(Context context, String subFolder) {
        File file;
        if (subFolder != null) {
            file = new File(context.getExternalCacheDir(), subFolder);
        } else {
            file = context.getExternalCacheDir();
        }
        file.mkdirs();
        return file;
    }

    /**
     * Returns a list of {@link File}s which have the .json extension
     * in the specified directory. Also includes files in subfolders.
     *
     * @param directory
     * @return
     */
    public static List<File> getJsonFilesInDirectory(File directory) {
        ArrayList<File> jsonFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                jsonFiles.addAll(getJsonFilesInDirectory(file));
            } else {
                if (file.getName().endsWith(".json")) {
                    jsonFiles.add(file);
                }
            }
        }
        return jsonFiles;
    }

    /**
     * Invokes an implicit share intent for the specified file.
     *
     * @param file
     * @param context
     */
    public static void shareFile(File file, Context context) {
        if (file == null || !file.canRead()) {
            Logger.d(TAG, "Unable to share file");
            return;
        }

        Logger.d(TAG, "Sharing file: " + file.getName());

        Uri uri = Uri.fromFile(file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, file.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, file.getName());
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("text/plain");

        Intent chooserIntent = Intent.createChooser(shareIntent, "Share file");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooserIntent);
    }

    /**
     * Writes the contents of a string to the specified file.
     *
     * @param data the string that the file should contain
     * @param file the file that the string should be written to
     * @throws IOException
     */
    public static void writeStringToFile(String data, File file) throws IOException {
        FileWriter fw;
        fw = new FileWriter(file);
        try {
            fw.write(data);
            fw.close();
        } catch (IOException e) {
            if (fw != null) {
                fw.close();
            }
        }
    }

}
