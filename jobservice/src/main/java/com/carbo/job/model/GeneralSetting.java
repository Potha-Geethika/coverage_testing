package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Document(collection = "general-settings")
public class GeneralSetting {
    @Id
    private String id;

    @Field("organizationId")
    @Indexed(unique = true)
    private String organizationId;

    @Field("generateReportAt")
    private String generateReportAt;

    @Field("timezone")
    private String timezone;

    @Field("emailRecipients")
    private List<EmailGroup> emailRecipients = new ArrayList<>();

    @Field("automatize")
    private Boolean automatize;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getGenerateReportAt() {
        return generateReportAt;
    }

    public void setGenerateReportAt(String generateReportAt) {
        this.generateReportAt = generateReportAt;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<EmailGroup> getEmailRecipients() {
        return emailRecipients;
    }

    public void setEmailRecipients(List<EmailGroup> emailRecipients) {
        this.emailRecipients = emailRecipients;
    }

    public String toEmailTo() {
        return emailRecipients.stream().map(each -> each.toEmailTo()).collect(Collectors.joining(";"));
    }
    public Boolean getAutomatize() {
        return automatize;
    }

    public void setAutomatize(Boolean automatize) {
        this.automatize = automatize;
    }

}
