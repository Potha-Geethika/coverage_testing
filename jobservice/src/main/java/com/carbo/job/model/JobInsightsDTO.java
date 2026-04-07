package com.carbo.job.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class JobInsightsDTO extends JobInsights{
    private String fleetName;
    private String company;
}
