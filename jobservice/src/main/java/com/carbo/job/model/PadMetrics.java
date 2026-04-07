package com.carbo.job.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PadMetrics {

    private String dateformat;
    private Long date;
    private Long stagesCompleted;
    private Double avgPressure;
    private Double avgRate;
    private Double avgProducedWaterUsedPercentage;
    private Double efficiency;
    private Double medianOfTBS;
    private Double avgStartPump;
    private Double cleanVolBBLs;
    private Double tonsPumped;
    private float avgSub;
    private Double pumpingHours;
    private Double serviceCompanyNameNPT; //pfNpt
    private Double nonServiceCompanyNameNPT; //nonpfNpt
    private Double scheduledTime;
    private String well;
    private String organizationName;
}