package com.carbo.job.model;

public class FileDescription {

    private String fileName;
    private String jobCreatedOrgId;
    private Long fileSize;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getJobCreatedOrgId() {
        return jobCreatedOrgId;
    }

    public void setJobCreatedOrgId(String jobCreatedOrgId) {
        this.jobCreatedOrgId = jobCreatedOrgId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
