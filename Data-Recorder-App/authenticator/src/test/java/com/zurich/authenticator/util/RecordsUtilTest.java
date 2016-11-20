package com.zurich.authenticator.util;

import com.zurich.authenticator.data.recorder.Record;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RecordsUtilTest {

    @Test
    public void getRecordFromFile_validFile_validRecord() throws Exception {
        List<File> recordFiles = RecordsUtil.getRecordFilesFromResourcesDirectory();
        for (File recordFile : recordFiles) {
            assertRecordFileIsInRightDirectory(recordFile);

            // don't deserialize recalculated records
            if (recordFile.getName().contains(RecordsUtil.FILE_TYPE_RECALCULATED_RECORD)) {
                continue;
            }

            Record record = RecordsUtil.getRecordFromFile(recordFile);
            assertRecordIsValid(record);
        }
    }

    @Test
    public void getRecordFilesWithLabel_labelAvailable_matchingFiles() throws Exception {
        String label = "testing";
        List<File> recordFiles = RecordsUtil.getRecordFilesWithLabel(label);
        assertTrue("No record files available with label " + label, recordFiles.size() > 0);
    }

    @Test
    public void getRecordFilesFromResourcesDirectory_filesAvailable_findsFiles() throws Exception {
        List<File> recordFiles = RecordsUtil.getRecordFilesFromResourcesDirectory();
        assertTrue("No record files available", recordFiles.size() > 0);
    }

    public void assertRecordIsValid(Record record) throws Exception {
        // generic
        assertNotNull("Record is null", record);
        assertNotNull("Record user is null", record.getUser());
        assertNotNull("Record label is null", record.getLabel());
        assertNotNull("Record comment is null", record.getComment());
        assertNotNull("Record device name is null", record.getDeviceName());
        assertNotNull("Record device ID is null", record.getDeviceId());

        // data batches
        assertNotNull("Record data batches are null", record.getDataBatches());
        assertTrue("Record data batches are empty", record.getDataBatches().size() > 0);

        // recorder
        assertNotNull("Record recorder is null", record.getRecorder());
        assertTrue("Record recorder start timestamp is 0", record.getRecorder().getStartTimestamp() != 0);
        assertTrue("Record recorder stop timestamp is 0", record.getRecorder().getStopTimestamp() != 0);
    }

    public void assertRecordFileIsInRightDirectory(File file) throws Exception {
        String fileName = file.getName();
        String filePath = file.getPath();
        filePath = filePath.substring(filePath.indexOf("/test/resources"));
        filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));

        String fileType = RecordsUtil.getFileTypeFromFileName(fileName);
        String deviceName = RecordsUtil.getDeviceNameFromFileName(fileName);
        String label = RecordsUtil.getLabelFromFileName(fileName);
        String comment = RecordsUtil.getCommentFromFileName(fileName);
        String user = RecordsUtil.getUserFromFileName(fileName);
        String startTimestamp = RecordsUtil.getStartTimestampFromFileName(fileName);

        String exceptionMessage = fileName + " should not be in directory " + filePath;
        assertTrue(exceptionMessage, filePath.contains(label));

        if (RecordsUtil.FILE_TYPE_RECORDED_RECORD.equals(fileType)) {
            assertTrue(exceptionMessage, filePath.contains(RecordsUtil.FOLDER_RECORDED_RECORDS));
        } else if (RecordsUtil.FILE_TYPE_RECALCULATED_RECORD.equals(fileType)) {
            assertTrue(exceptionMessage, filePath.contains(RecordsUtil.FOLDER_RECALCULATED_RECORDS));
        } else {
            throw new Exception("Unknown file type: " + fileType);
        }

        if (RecordsUtil.PHONE_DEVICE_NAMES.contains(deviceName)) {
            assertTrue(exceptionMessage, filePath.contains(RecordsUtil.FOLDER_PHONE));
        } else if (RecordsUtil.WEAR_DEVICE_NAMES.contains(deviceName)) {
            assertTrue(exceptionMessage, filePath.contains(RecordsUtil.FOLDER_WEAR));
        } else {
            throw new Exception("Unknown device name: " + deviceName);
        }
    }

}