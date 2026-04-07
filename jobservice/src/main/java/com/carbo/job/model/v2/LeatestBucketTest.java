package com.carbo.job.model.v2;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "jobs-bucket-tests-v2")
public class LeatestBucketTest {
    @Id
    private String id;

    @Field("unitNumber")
    private String unitNumber;

    @Field("date")
    private LocalDate date;

    @Field("fleet")
    private String fleet;

    @Field("personnel")
    private List<String> personnel;

    @Field("pumpStatuses")
    private List<PumpStatus> pumpStatuses;

    @Field("notes")
    private String notes;

    @Field("organizationId")
    private String organizationId;

    @Field("jobId")
    private String jobId;

    @Field("createdBy")
    private String createdBy;

    @Field("createdTime")
    private Long createdTime;

    @Field("updatedBy")
    private String updatedBy;

    @Field("updatedTime")
    private Long updatedTime;



@Data
public static class PumpStatus {
    private String pumpNumber; // CP1, CP2, etc.
    private Double maxGpm;
    private Double hiScale;
    private String status;
}
}
