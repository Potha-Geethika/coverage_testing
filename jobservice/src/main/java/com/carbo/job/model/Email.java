package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Email {
    @Id
    protected String id;

    @Field("to")
    private String to;

    @Field("cc")
    private String cc;

    @Field("subject")
    private String subject;

    @Field("body")
    private String body;

    @Field("status")
    private String status;

    @Field("attachedFiles")
    public List<AttachedFile> attachedFiles = new ArrayList<>();

    @Field("sentAt")
    private Date sentAt;

    @Field("by")
    private String by;

    @Field("organizationId")
    @Indexed(unique = false)
    private String organizationId;

    @Field("jobId")
    @Indexed(unique = false)
    private String jobId;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<AttachedFile> getAttachedFiles() {
        return attachedFiles;
    }

    public void setAttachedFiles(List<AttachedFile> attachedFiles) {
        this.attachedFiles = attachedFiles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
