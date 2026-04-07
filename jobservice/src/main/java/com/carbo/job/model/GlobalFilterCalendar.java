package com.carbo.job.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GlobalFilterCalendar {
    List<String> jobStatus = new ArrayList<>();
    List<String> districts = new ArrayList<>();
}
