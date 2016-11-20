package com.zurich.authenticator.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GoogleApiConnectionTest {

    @Test
    public void getPathFromMessageId_validId_correctPath() throws Exception {
        int id = 123;
        String actual = GoogleApiConnection.getPathFromMessageId(id);
        String expected = GoogleApiConnection.PATH_PREFIX + String.valueOf(id);
        assertEquals("Wrong message path", expected, actual);
    }

    @Test
    public void getMessageIdFromPath_validPath_correctId() throws Exception {
        int expected = 123;
        String path = GoogleApiConnection.getPathFromMessageId(expected);
        int actual = GoogleApiConnection.getMessageIdFromPath(path);
        assertEquals("Wrong message ID", expected, actual);
    }

    @Test
    public void getMessageIdFromPath_invalidPath_defaultId() throws Exception {
        int expected = 0;
        String path = "something_stupid";
        int actual = GoogleApiConnection.getMessageIdFromPath(path);
        assertEquals("Wrong message ID", expected, actual);

        path = GoogleApiConnection.PATH_PREFIX + "123ABC";
        actual = GoogleApiConnection.getMessageIdFromPath(path);
        assertEquals("Wrong message ID", expected, actual);
    }

    @Test
    public void getBytesFromBundle() throws Exception {
        // TODO: implement test test
    }

    @Test
    public void getBundleFromBytes_validBytes_correctBundle() throws Exception {
        // TODO: implement test test
        // http://stackoverflow.com/questions/40382882/how-to-create-a-bundle-in-a-unit-test

        /*
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        boolean containsKey = bundle.containsKey("key");
        assertTrue(containsKey);

        byte[] bytes = GoogleApiConnection.getBytesFromBundle(bundle);
        Bundle convertedBundle = GoogleApiConnection.getBundleFromBytes(bytes);
        assertEquals("Bundles don't match", bundle.get("key"), convertedBundle.get("key"));
        */
    }

}