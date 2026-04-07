package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimatedDuration {
    private double stage;

    private double swapOver;

    private double maintenance;

    private double toLocation;

    private double fromLocation;

    private double total;
}