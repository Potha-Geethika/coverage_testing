package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class AcidTitration {
    @Id
    private String id;

    @Field("weightOfAcid")
    private String weightOfAcid;

    @Field("normalityOfNaoh")
    private String normalityOfNaoh;

    @Field("acidSampleSize")
    private String acidSampleSize;

    @Field("amountOfNaohUsed")
    private String amountOfNaohUsed;

    @Field("hcl")
    private String hcl;

    @Field("date")
    private  String date;

    @Field("jobId")
    private String jobId;

    @Field("wellId")
    private String wellId;

    @Field("stage")
    private String stage;

    @Field("organizationId")
    private String organizationId;

    @Field ("ts")
    private long ts;

    @Field ("created")
    private long created;

    @Field("label")
    public String label;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeightOfAcid() {
        return weightOfAcid;
    }

    public void setWeightOfAcid(String weightOfAcid) {
        this.weightOfAcid = weightOfAcid;
    }

    public String getNormalityOfNaoh() {
        return normalityOfNaoh;
    }

    public void setNormalityOfNaoh(String normalityOfNaoh) {
        this.normalityOfNaoh = normalityOfNaoh;
    }

    public String getAcidSampleSize() {
        return acidSampleSize;
    }

    public void setAcidSampleSize(String acidSampleSize) {
        this.acidSampleSize = acidSampleSize;
    }

    public String getAmountOfNaohUsed() {
        return amountOfNaohUsed;
    }

    public void setAmountOfNaohUsed(String amountOfNaohUsed) {
        this.amountOfNaohUsed = amountOfNaohUsed;
    }

    public String getHcl() {
        return hcl;
    }

    public void setHcl(String hcl) {
        this.hcl = hcl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

