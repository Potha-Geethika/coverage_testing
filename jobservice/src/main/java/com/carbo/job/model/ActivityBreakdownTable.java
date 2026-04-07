package com.carbo.job.model;

import lombok.Data;

import java.util.List;

@Data
public class ActivityBreakdownTable {
    private String categoryName;

    private Double totalTime;

    private List<ActivityBreakdownSubCategory> subCategories;
}
