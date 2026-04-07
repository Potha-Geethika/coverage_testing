package com.carbo.job.model;

public class JobUpdateRequest {
    private String jobId;
    private String oldFleetId;
    private String newFleetId;
    private Long startDate;
    private Long endDate;

    // Getters & Setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getOldFleetId() { return oldFleetId; }
    public void setOldFleetId(String oldFleetId) { this.oldFleetId = oldFleetId; }
    public String getNewFleetId() { return newFleetId; }
    public void setNewFleetId(String newFleetId) { this.newFleetId = newFleetId; }
    public Long getStartDate() { return startDate; }
    public void setStartDate(Long startDate) { this.startDate = startDate; }
    public Long getEndDate() { return endDate; }
    public void setEndDate(Long endDate) { this.endDate = endDate; }
}

