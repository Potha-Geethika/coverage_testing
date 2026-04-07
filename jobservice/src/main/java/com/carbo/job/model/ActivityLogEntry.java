package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Document(collection = "activity-log-entries")
public class ActivityLogEntry {
    private static final DateTimeFormatter startEndTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter startEndDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");

    @Id
    private String id;

    @Field("date")
    @NotEmpty(message = "date can not be empty")
    private Date date;

    @Field("day")
    private Integer day;

    @Field("jobId")
    private String jobId;

    @Field("well")
    private String well;

    @Field("stage")
    private Float stage;

    @Field("start")
    private String start;

    @Field("end")
    private String end;

    @Field("opsActivity")
    private String opsActivity;

    @Field("eventOrNptCode")
    private String eventOrNptCode;

    @Field("complete")
    private Boolean complete = false;

    @Field("subNptCode")
    private String subNptCode;

    @Field("equipment")
    private String equipment;

    @Field("equipmentIssueId")
    private String equipmentIssueId;

    @Field("comments")
    private String comments;

    @Field("endTimeOnNextDay")
    private Boolean endTimeOnNextDay;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("ts")
    private Long ts;

    @Field("organizationId")
    private String organizationId;

    @Field("bank")
    private String bank;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
    }

    public Float getStage() {
        return stage;
    }

    public void setStage(Float stage) {
        this.stage = stage;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getOpsActivity() {
        return opsActivity;
    }

    public void setOpsActivity(String opsActivity) {
        this.opsActivity = opsActivity;
    }

    public String getEventOrNptCode() {
        return eventOrNptCode;
    }

    public void setEventOrNptCode(String eventOrNptCode) {
        this.eventOrNptCode = eventOrNptCode;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public String getSubNptCode() {
        return subNptCode;
    }

    public void setSubNptCode(String subNptCode) {
        this.subNptCode = subNptCode;
    }

    public String getEquipmentIssueId() {
        return equipmentIssueId;
    }

    public void setEquipmentIssueId(String equipmentIssueId) {
        this.equipmentIssueId = equipmentIssueId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public Float getHoursSpan() {
        return getMillisecondsSpan()/3600000.0f;
    }

    public Boolean getEndTimeOnNextDay() {
        return endTimeOnNextDay;
    }

    public void setEndTimeOnNextDay(Boolean endTimeOnNextDay) {
        this.endTimeOnNextDay = endTimeOnNextDay;
    }

    public String getBank() {return bank;}

    public void setBank(String bank) {this.bank = bank;}

    public long getMillisecondsSpan() {
        Duration dur = getDuration();
        return dur.toMillis();
    }

    private Duration getDuration() {
        if (start == null || end == null) {
            return Duration.ZERO;
        }
        else {
            LocalDateTime startTime = convertToLocalDateTime(start);
            LocalDateTime endTime = convertToLocalDateTime(end);
            if (end.length() == 5 && endTime.toLocalTime() == LocalTime.MIDNIGHT) {
                endTime = endTime.plusDays(1);
            }
            if (startTime.isAfter(endTime)) {
                throw new IllegalStateException("Start time cannot be after end time");
            }
            return Duration.between(startTime, endTime);
        }
    }

    public Float getDecimalDuration() {
        Duration duration = getDuration();
        long mins = duration.toMinutes();
        long hours = duration.toHours();
        long remain = mins;
        if (hours > 0) {
            remain = mins % (hours*60);
        }
        float decimal = remain/60.0f;
        return hours + decimal;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    private LocalDateTime convertToLocalDateTime(String time) {
        if (time.length() == 5) {
            LocalDate curDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime curTime = LocalTime.parse(time, startEndTimeFormatter);
            return LocalDateTime.of(curDate, curTime);
        }
        else {
            return LocalDateTime.parse(time, startEndDateTimeFormatter);
        }
    }

    public Long getModified() {
        return modified;
    }
}
