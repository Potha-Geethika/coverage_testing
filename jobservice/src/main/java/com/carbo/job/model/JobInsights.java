package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.*;
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "jobs-insights")
public class JobInsights {
    @Id
    private String id;

    @Field("jobId")
    private String jobId;

    @Field("jobNumber")
    private String jobNumber;

    @Field("organizationId")
    private String organizationId;

    @Field("fleetId")
    private String fleetId;

    @Field("startDate")
    private Long startDate;

    @Field("endDate")
    private Long endDate;

    @Field("created")
    private Long created;

    @Field("modified")
    private Long modified;

    @Field("modifiedBy")
    private String modifiedBy;

    @Field("modifiedFields")
    private List<String> modifiedFields;

    @Field("districtId")
    private String districtId;
}


