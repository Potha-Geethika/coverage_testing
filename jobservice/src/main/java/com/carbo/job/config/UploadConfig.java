package com.carbo.job.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
public class UploadConfig {
    @Value("${upload.location}")
    private String uploadLocation;
    @Value("${azure.blob.storage.sas-token}")
    private String sasToken;
    @Value("${azure.blob.storage.sas-token-pjr}")
    private String saSTokenPjr;
    @Value("${azure.blob.storage.sas-token-on-site-equipment}")
    private String saSTokenOnSiteEquipment;
    @Value("${azure.blob.storage.blob-url}")
    private String azureBlobUrl;
    @Value("${azure.blob.storage.base-container-name}")
    private String containerName;
    @Value("${azure.blob.storage.container-name-onsite}")
    private String onsitecontainerName;
    @Value("${azure.blob.storage.container-name-pjr}")
    private String pjrcontainerName;

//    private String azureBlobUrlForPjr = "https://uatemailattachments.blob.core.windows.net";
//    private String azureBlobUrlForOnSite = "https://uatemailattachments.blob.core.windows.net";
}