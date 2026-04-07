package com.carbo.job.model;

import lombok.Data;

import java.util.List;

@Data
public class JobWidgetResponse {
    List<ServiceCompanyGroup> serviceCompanies;
}
