package com.carbo.job.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JobWidgets {
    private String jobId;
    private String jobNumber;
    private String curWellId;
    private String currWellName;
    private int numberOfWells;
    private String curStage;
    private String padName;
    private String organizationId;
    private String organizationName;
    private String sharedWithOrganizationId;
    private Long created;
    private String fleet;
    private String fleetType;
    private Boolean isAccess;
    private String operationsType;
    private StagesCompleted stagesCompleted;
    private Long startedOn;
    private int dualFuelPumpCount;
    private Long expectedCompletionBy;
//  Add other fields if required
    private Map<String, NptHours> nptBreakdown;

    private List<ActivityBreakdown> activityBreakdown;
    private List<ActivityBreakdownTable> activityBreakdownTable;
}
