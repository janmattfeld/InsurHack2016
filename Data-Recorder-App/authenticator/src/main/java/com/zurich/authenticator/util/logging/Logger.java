package com.zurich.authenticator.util.logging;

import android.util.Log;

import com.zurich.authenticator.data.persister.DataPersistingException;
import com.zurich.authenticator.data.persister.PersisterManager;
import com.zurich.authenticator.data.persister.database.DataBasePersister;

import java.util.HashMap;
import java.util.Map;

public final class Logger {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;

    public static final Map<Integer, String> readableLogLevels = getReadableLogLevelMap();

    private static Logger instance;

    boolean logCatEnabled = false;
    boolean consoleEnabled = true;
    boolean dataBaseEnabled = false;

    private int minimumLevel = VERBOSE;
    private int minimumLogCatLevel = VERBOSE;
    private int minimumConsoleLevel = VERBOSE;
    private int minimumDataBaseLevel = INFO;

    private Logger() {
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public static void log(int level, String tag, String message) {
        log(level, tag, message, null);
    }

    public static void log(int level, String tag, String message, Throwable throwable) {
        Logger instance = getInstance();
        if (level < instance.minimumLevel) {
            return;
        }
        instance.logToLogCat(level, tag, message, throwable);
        instance.logToConsole(level, tag, message, throwable);
        instance.logToDataBase(level, tag, message, throwable);
    }

    public void logToLogCat(int level, String tag, String message, Throwable throwable) {
        if (!logCatEnabled || level < minimumLogCatLevel) {
            return;
        }
        if (level == ERROR && throwable != null) {
            Log.e(tag, message, throwable);
        } else if (level == WARN && throwable != null) {
            Log.w(tag, message, throwable);
        } else {
            Log.println(level, tag, message);
        }
    }

    public void logToConsole(int level, String tag, String message, Throwable throwable) {
        if (!consoleEnabled || level < minimumConsoleLevel) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(readableLogLevels.get(level)).append(" - ");
        sb.append(tag).append(": ");
        sb.append(message);

        if (throwable != null) {
            sb.append(" - ").append(throwable.getMessage());
        }
        System.out.println(sb.toString());
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    public void logToDataBase(int level, String tag, String message, Throwable throwable) {
        if (!dataBaseEnabled || level < minimumDataBaseLevel) {
            return;
        }
        try {
            DataBasePersister dataBasePersister = (DataBasePersister) PersisterManager.getDataPersister(PersisterManager.STRATEGY_DATABASE);
            if (dataBasePersister != null) {
                dataBasePersister.logMessageIntoDataBase(level, tag, message, throwable);
            }
        } catch (DataPersistingException ex) {
            ex.printStackTrace();
        }
    }

    public static void v(String tag, String message) {
        log(VERBOSE, tag, message);
    }

    public static void d(String tag, String message) {
        log(DEBUG, tag, message);
    }

    public static void i(String tag, String message) {
        log(INFO, tag, message);
    }

    public static void w(String tag, String message) {
        log(WARN, tag, message);
    }

    public static void w(String tag, String message, Throwable throwable) {
        log(WARN, tag, message, throwable);
    }

    public static void e(String tag, String message) {
        log(ERROR, tag, message);
    }

    public static void e(String tag, String message, Throwable throwable) {
        log(ERROR, tag, message, throwable);
    }

    private static Map<Integer, String> getReadableLogLevelMap() {
        Map<Integer, String> logLevelMap = new HashMap<>();
        logLevelMap.put(VERBOSE, "Verbose");
        logLevelMap.put(DEBUG, "Debug");
        logLevelMap.put(INFO, "Info");
        logLevelMap.put(WARN, "Warning");
        logLevelMap.put(ERROR, "Error");
        return logLevelMap;
    }

    public static void setMinimumLevel(int minimumLevel) {
        Logger instance = getInstance();
        instance.minimumLevel = minimumLevel;
        instance.minimumLogCatLevel = minimumLevel;
        instance.minimumConsoleLevel = minimumLevel;
        instance.minimumDataBaseLevel = minimumLevel;
    }

    public static void setMinimumLogCatLevel(int minimumLogCatLevel) {
        getInstance().minimumLogCatLevel = minimumLogCatLevel;
    }

    public static void setMinimumConsoleLevel(int minimumConsoleLevel) {
        getInstance().minimumConsoleLevel = minimumConsoleLevel;
    }

    public static void setMinimumDataBaseLevel(int minimumDataBaseLevel) {
        getInstance().minimumDataBaseLevel = minimumDataBaseLevel;
    }

    public static void setLogCatEnabled(boolean logCatEnabled) {
        getInstance().logCatEnabled = logCatEnabled;
    }

    public static void setConsoleEnabled(boolean consoleEnabled) {
        getInstance().consoleEnabled = consoleEnabled;
    }

    public static void setDataBaseEnabled(boolean dataBaseEnabled) {
        getInstance().dataBaseEnabled = dataBaseEnabled;
    }

}
