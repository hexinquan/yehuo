package com.guoguo.chat.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Timer;

public class LocalDateAndTimeUtils {
    public static LocalDateTime timestamToDatetime(long timestamp){
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
    public static long datatimeToTimestamp(LocalDateTime ldt){
        long timestamp = ldt.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return timestamp;
    }

    public static void main(String[] args) {
        LocalDateTime endTime = timestamToDatetime(1589119197362L);
        LocalDateTime sendTime = timestamToDatetime(1589032797362L);
        String str= "17954494259798028";
        String substring = str.substring(0, 1);
        String s = str.replaceFirst(substring, "9");
        System.out.println(Long.valueOf(s));
    }
}
