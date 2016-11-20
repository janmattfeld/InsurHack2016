package com.zurich.authenticator.util;

import com.zurich.authenticator.BuildConfig;
import com.zurich.authenticator.data.persister.PersisterManager;
import com.zurich.authenticator.data.recorder.Record;
import com.zurich.authenticator.util.logging.Logger;
import com.zurich.authenticator.util.markdown.list.UnorderedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, instrumentedPackages = {"com.zurich.authenticator.data.generic", "com.zurich.authenticator.data.classifier", "com.zurich.authenticator.data.aggregation", "com.zurich.authenticator.data.batch", "com.zurich.authenticator.data.feature", "com.zurich.authenticator.data.classification", "com.zurich.authenticator.data.state", "com.zurich.authenticator.data.trustlevel"})
public class RecalculatorTest {

    private static final String TAG = Recalculator.class.getSimpleName();

    @Test
    public void recalculateRecords() throws Exception {
        Logger.setMinimumConsoleLevel(Logger.DEBUG);

        String pattern = "(?!.*)"; // doesn't match anything
        //pattern = "recorded_records.*walking";
        List<File> recordFiles = RecordsUtil.getRecordFilesFromResourcesDirectory();
        List<File> filteredFiles = getFilteredFiles(recordFiles, pattern);
        for (File file : filteredFiles) {
            Record record = RecordsUtil.getRecordFromFile(file);
            recalculateRecord(record);
        }
    }

    /**
     * Takes the {@link com.zurich.authenticator.data.sensor.SensorEventData} from
     * the specified record and re-aggregates everything. The new record will be
     * exported to the file system.
     *
     * @param record the original record, containing only sensor event data
     */
    public static void recalculateRecord(Record record) {
        Recalculator recalculator = new Recalculator(record);
        Record recalculatedRecord = recalculator.getRecalculatedRecord();
        PersisterManager.logPersistedData();
        RecordsUtil.exportRecalculatedRecord(recalculatedRecord);
    }

    /**
     * Use this test to check which files would match
     * a given pattern before starting the recalculator.
     */
    @Test
    public void getFilteredFiles_testPattern_logFiles() {
        String pattern = "recorded_records.*walking";
        List<File> recordFiles = RecordsUtil.getRecordFilesFromResourcesDirectory();
        List<File> filteredFiles = getFilteredFiles(recordFiles, pattern);
        UnorderedList unorderedList = new UnorderedList(new ArrayList<Object>(filteredFiles));
        Logger.d(TAG, "Filtered files:\n" + unorderedList.toString());
    }

    /**
     * Returns a list of files which paths match the passed pattern.
     *
     * @param files
     * @param patternString
     * @return
     */
    public static List<File> getFilteredFiles(List<File> files, String patternString) {
        List<File> matchedFiles = new ArrayList<>();
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher;
        for (File file : files) {
            matcher = pattern.matcher(file.getPath());
            if (matcher.find()) {
                matchedFiles.add(file);
            }
        }
        return matchedFiles;
    }

    public static File getFirstFilteredFile(List<File> files, String patternString) {
        List<File> matchedFiles = getFilteredFiles(files, patternString);
        if (matchedFiles.isEmpty()) {
            return null;
        }
        return matchedFiles.get(0);
    }

}