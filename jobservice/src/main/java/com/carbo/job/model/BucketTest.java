package com.carbo.job.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "job-bucket-tests")
@Data
public class BucketTest {
    @Id
    private String id;

    @Field
    private String jobId;

    @Field
    private String organizationId;

    @Field("engineers")
    private String engineers;

    @Field("supervisors")
    private String supervisors;

    @Field("consultants")
    private String consultants;

    @Field("tests")
    private List<BucketTestItem> tests = new ArrayList<>();

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

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

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getEngineers() {
        return engineers;
    }

    public void setEngineers(String engineers) {
        this.engineers = engineers;
    }

    public String getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(String supervisors) {
        this.supervisors = supervisors;
    }

    public String getConsultants() {
        return consultants;
    }

    public void setConsultants(String consultants) {
        this.consultants = consultants;
    }

    public List<BucketTestItem> getTests() {
        return tests;
    }

    public void setTests(List<BucketTestItem> tests) {
        this.tests = tests;
    }
}
