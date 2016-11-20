package com.zurich.authenticator.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class TimeUtils {

    public static final List<Long> DURATIONS = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1),
            TimeUnit.MILLISECONDS.toMillis(1));

    public static final List<String> LONG_UNITS = Arrays.asList(
            "year",
            "month",
            "day",
            "hour",
            "minute",
            "second",
            "millisecond");

    public static final List<String> SHORT_UNITS = Arrays.asList(
            "year",
            "month",
            "day",
            "h",
            "m",
            "s",
            "ms");

    public static String getReadableDuration(long duration) {
        return getReadableDuration(duration, false);
    }

    public static String getReadableDuration(long duration, boolean shortUnits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DURATIONS.size(); i++) {
            Long current = DURATIONS.get(i);
            long temp = duration / current;
            if (temp > 0) {
                sb.append(temp);
                if (shortUnits) {
                    sb.append(SHORT_UNITS.get(i));
                } else {
                    sb.append(" ").append(LONG_UNITS.get(i)).append(temp > 1 ? "s" : "");
                }
                break;
            }
        }
        if ("".equals(sb.toString())) {
            return "0";
        } else {
            return sb.toString();
        }
    }

    public static String getReadableTimeSince(long timestamp) {
        long duration = System.currentTimeMillis() - timestamp;
        return getReadableDuration(duration, false) + " ago";
    }
}
