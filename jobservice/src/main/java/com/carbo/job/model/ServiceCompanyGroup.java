package com.carbo.job.model;

import lombok.Data;

import java.util.List;

@Data
public class ServiceCompanyGroup {
    private String serviceCompany;
    private int jobCount;
    private List<JobWidgets> jobWidgets;
}
