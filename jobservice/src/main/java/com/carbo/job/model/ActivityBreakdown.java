package com.carbo.job.model;

import lombok.Data;

import java.util.Map;

@Data
public class ActivityBreakdown {
    private int day;
    private String date;
    private Map<String, Double> activityTime;
}
