package com.example.ryan.studentassistant;

/**
 * Created by Ryan on 4/8/2017.
 */

import org.joda.time.LocalDateTime;

public class TimeDate {
    public int sec, min, hr, dayOfMonth, month, yr;

    public TimeDate() {
        sec = 0;
        min = 0;
        hr = 0;
        dayOfMonth = 0;
        month = 0;
        yr = 0;
    }

    public TimeDate(String time) {
        setTime(time);
    }

    public final void setTime(String time) {
        LocalDateTime l = LocalDateTime.parse(time);
        sec = l.getSecondOfMinute();
        min = l.getMinuteOfHour();
        hr = l.getHourOfDay();
        dayOfMonth = l.getDayOfMonth();
        month = l.getMonthOfYear();
        yr = l.getYear();
    }
}