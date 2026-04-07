package com.carbo.job.model.jobPanel;

import com.carbo.job.model.BankCountEnum;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JobData {

    private String jobId;

    private double fleetUtilization;

    private double jobNPTHours;

    private double  scheduledHours;


    private String jobNumber;

    private String fleetName;

    private String fleetType;

    private int dualFuelPumpCount;

    private String operatorName;

    private Long jobStartDate;

    private Long jobLatestReportDate;

    private double jobCompletionPercentage;

    private String latestActivity;

    private boolean isOpsActivity;

    private Boolean isAccess;

    private String curWellId;

    private String curStage;

    private String currWellName;

    private List<Coordinates> coordinates;

    private String organizationId;

    private String organizationName;

    private String padName;

    private Long created;

    private int numberOfWells;

    private String operationsType;

    private BankCountEnum bankCount;
}
