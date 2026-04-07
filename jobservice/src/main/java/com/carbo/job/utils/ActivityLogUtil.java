package com.carbo.job.utils;

import com.carbo.job.model.ActivityLogEntry;
import com.carbo.job.model.Job;

import jakarta.servlet.http.HttpServletRequest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class ActivityLogUtil {

    public static final String GET_ACTIVITY_BY_ORGANIZATION_ID_AND_JOB_ID = "/api/activitylog/v1/activity-logs/getActivityByOrganizationIdAndJobId";
    private static final DateTimeFormatter startEndDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");

    public static Integer getActivityDayFromTodayDate(Job job) {
        return getActivityDayFromDate(job, LocalDate.now());
    }

    public static LocalDate toLocalDate(Date from, ZoneId zoneId) {
        return from.toInstant().atZone(zoneId).toLocalDate();
    }

    public static LocalDate toLocalDate(Long from) {
        return Instant.ofEpochMilli(from).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Integer getActivityDayFromDate(Job job, LocalDate date) {
        if (job.getStartDate() != null) {
            LocalDate startLocalDate = toLocalDate(job.getStartDate());
            return (int) startLocalDate.until(date, DAYS) + 1;
        }
        else {
            return 0;
        }
    }

    public static LocalDate getReportDate(Job job, Integer day) {
        if (job.getStartDate() != null) {
            LocalDate startDate = toLocalDate(job.getStartDate());
            return startDate.plusDays(day - 1);
        }
        else {
            return null;
        }
    }

    public static ZonedDateTime getReportDate(Job job, Integer day, ZoneId zoneId) {
        if (job.getStartDate() != null && zoneId != null) {
            ZonedDateTime zonedDateTime = toLocalDate(job.getStartDate()).atStartOfDay(zoneId);
            return zonedDateTime.plusDays(day - 1);
        }
        else {
            return null;
        }
    }

    public static Float getTotalPumpTimeInMins(List<ActivityLogEntry> pumpTimeActivityLogEntries) {
        return pumpTimeActivityLogEntries
                .stream()
                .map(x -> x.getMillisecondsSpan())
                .reduce(0L, Long::sum)/60000.0f;
    }

    public static String formatTimeWithDate(String dateTime, ZonedDateTime date, DateTimeFormatter formatter, Boolean isEndTime) {
        if (dateTime == null) {
            return "";
        }
        if (dateTime.length() == 5) {
            LocalDate localDate =  isEndTime && dateTime.equals("00:00") ? date.toLocalDate().plusDays(1) : date.toLocalDate();
            ZonedDateTime tmp = ZonedDateTime.of(localDate.atTime(LocalTime.parse(dateTime)), date.getZone());
            return tmp.format(formatter);
        }
        else {
            LocalDate localDate = LocalDateTime.parse(dateTime, startEndDateTimeFormatter).toLocalDate();
            ZonedDateTime tmp = ZonedDateTime.of(localDate.atTime(LocalTime.parse(dateTime.substring(9))), date.getZone());
            return tmp.format(formatter);
        }
    }

    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
