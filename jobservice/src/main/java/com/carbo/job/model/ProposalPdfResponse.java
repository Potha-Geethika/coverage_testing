package com.carbo.job.model;

public class ProposalPdfResponse {

    private String uploadedPdf;
    private String fileName;

    private String wellId;

    private String jobId;

    public String getUploadedPdf() {
        return uploadedPdf;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setUploadedPdf(String uploadedPdf) {
        this.uploadedPdf = uploadedPdf;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
