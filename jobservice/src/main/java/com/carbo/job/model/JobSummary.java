package com.carbo.job.model;

public class JobSummary {
    private String jobNumber;
    private PadInfo padInfo;

    public JobSummary(PadInfo padInfo) {
        this.jobNumber = padInfo.getJobNumber();
        this.padInfo = padInfo;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public PadInfo getPadInfo() {
        return padInfo;
    }
}
