package com.carbo.job.model;

import java.util.HashSet;
import java.util.Set;

public class PadInfo {
    private String jobNumber;
    private Integer padStageTotal = 0;
    private Integer padStageCompleted = 0;
    private Set<Float> completedStages = new HashSet<>();

    public void addCompleteStage(Float stage) {
        if (!completedStages.contains(stage)) {
            completedStages.add(stage);
        }
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public Integer getPadStageTotal() {
        return padStageTotal;
    }

    public void setPadStageTotal(Integer padStageTotal) {
        this.padStageTotal = padStageTotal;
    }

    public Integer getPadStageCompleted() {
        return padStageCompleted;
    }

    public void setPadStageCompleted(Integer padStageCompleted) {
        this.padStageCompleted = padStageCompleted;
    }

    public Set<Float> getCompletedStages() {
        return completedStages;
    }

    public void setCompletedStages(Set<Float> completedStages) {
        this.completedStages = completedStages;
    }

    public void increaseCompletedCount() {
        this.padStageCompleted += 1;
    }
}
