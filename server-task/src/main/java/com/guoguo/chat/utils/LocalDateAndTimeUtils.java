package com.guoguo.chat.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

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
        LocalDateTime localDateTime = timestamToDatetime(1588837561923L);
        LocalDateTime localDateTime1 = localDateTime.plusMinutes(1);
        Duration between = Duration.between(localDateTime, localDateTime1);
        long l = between.toMinutes();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String format = dateTimeFormatter.format(localDateTime);
        String now = dateTimeFormatter.format(localDateTime1);
        System.out.println(l);
        System.out.println(format);
        System.out.println(now);
    }
}
