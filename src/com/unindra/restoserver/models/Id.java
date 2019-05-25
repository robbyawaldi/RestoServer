package com.unindra.restoserver.models;

import org.joda.time.LocalDateTime;

class Id {
    static String getIdByDateTime(LocalDateTime dateTime) {
        return String.format(
                "%d%d%d%d%d%d%d",
                dateTime.getSecondOfMinute(),
                dateTime.getDayOfMonth(),
                dateTime.getHourOfDay(),
                dateTime.getYear(),
                dateTime.getMonthOfYear(),
                dateTime.getMinuteOfHour(),
                dateTime.getMillisOfSecond());
    }

    static String getIdStringFromInt(int integer) {
        return String.format("%010d", integer);
    }
}
