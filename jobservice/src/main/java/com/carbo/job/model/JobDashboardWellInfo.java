package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "job-dashboard-well-information")
@CompoundIndex(name = "jobId_organizationId_wellId_idx", def = "{'jobId': 1, 'organizationId': 1, 'wellId': 1}", unique = true)
public class JobDashboardWellInfo {

    @Id
    private String id;

    @Field("wellId")
    private String wellId;

    @Field("wellName")
    private String wellName;

    @Field("api")
    private String api;

    @Field("afe")
    private String afe;

    @Field("formation")
    private String formation;

    @Field("bhst")
    private Double bhst;

    @Field("wellType")
    private String wellType;

    @Field("completionType")
    private String completionType;

    @Field("stageIntervals")
    private Integer stageIntervals;

    @Field("maxPressure")
    private Double maxPressure;

    @Field("maxTvd")
    private Double maxTvd;

    @Field("mainFluidType")
    private String mainFluidType;

    @Field("proppantTypes")
    private List<String> proppantTypes;

    @Field("designFluidBbls")
    private Double designFluidBbls;

    @Field("designProppantLbs")
    private Double designProppantLbs;

    @Field("totalPumpTimeHrs")
    private Double totalPumpTimeHrs;

    @Field("jobId")
    private String jobId;

    @Field("organizationId")
    private String organizationId;

    @Field("modified")
    private Long modified;

    @Field("created")
    private Long created;

}