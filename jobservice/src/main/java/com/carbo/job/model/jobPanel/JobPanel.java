package com.carbo.job.model.jobPanel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JobPanel {

    private double utilization;
    private List<JobData>jobData;
}
