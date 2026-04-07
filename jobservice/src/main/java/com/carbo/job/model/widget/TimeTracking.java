package com.carbo.job.model.widget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeTracking {

  Map<String,Double> frac;

  Map<String,Double> wireline;

  Map<String,Double> swap;

  Map<String,Double> maintenance;



}
