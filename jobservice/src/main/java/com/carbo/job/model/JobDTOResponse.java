package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JobDTOResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("jobNumber")
    private String jobNumber;

    @JsonProperty("fleet")
    private String fleet;

    @JsonProperty("operator")
    private String operator;

    @JsonProperty("pad")
    private String pad;

    @JsonProperty("location")
    private String location;

    @JsonProperty("receiveEBol")
    private Boolean receiveEBol;

    @JsonProperty("zipper")
    private Boolean zipper;

    @JsonProperty("proppantSchematicType")
    private String proppantSchematicType;

    @JsonProperty("targetStagesPerDay")
    private int targetStagesPerDay;

    @JsonProperty("status")
    private String status;

    @JsonProperty("sharedWithOrganizationId")
    private String sharedWithOrganizationId;

    @JsonProperty("organizationId")
    private String organizationId;

    @JsonProperty("districtId")
    private String districtId;

    @JsonProperty("wellheadCo")
    private String wellheadCo;

    @JsonProperty("wirelineCo")
    private String wirelineCo;

    @JsonProperty("waterTransferCo")
    private String waterTransferCo;

    @JsonProperty("activityLogStartTime")
    private String activityLogStartTime = "00:00";

    @JsonProperty("includeToeStage")
    private Boolean includeToeStage;

    @JsonProperty("ts")
    private Long ts;

    @JsonProperty("rts")
    private Long rts;

    @JsonProperty("connectJobTime")
    private boolean connectJobTime;

    @JsonProperty("automatize")
    private boolean automatize;

    @JsonProperty("metric")
    private boolean metric;

    @JsonProperty("btu")
    private float btu;

    @JsonProperty("latestBtu")
    private float latestBtu;

    @JsonProperty("swapOverTime")
    private float swapOverTime;

    @JsonProperty("targetWirelineTimePerStage")
    private float targetWirelineTimePerStage;

    @JsonProperty("targetMaintenanceTimePerDay")
    private float targetMaintenanceTimePerDay;

    @JsonProperty("wells")
    private List<WellDTO> wells = new ArrayList<>();

}
