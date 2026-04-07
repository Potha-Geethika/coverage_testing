package com.carbo.job.model;

import com.carbo.job.model.analytics.DailyJobRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyJobRecordWrapper {
    Set<String> ids;
    List<DailyJobRecord> dailyJobRecords;
}
