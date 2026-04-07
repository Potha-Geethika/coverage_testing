package com.carbo.job.controllers;
import static com.carbo.job.utils.Constants.DATA_UPDATED_SUCCESSFULLY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.carbo.job.model.JobStatus;
import com.carbo.job.services.OrganizationService;

@RestController
@RequestMapping (value = "v1/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PutMapping ("/update-data")
    public ResponseEntity updateData(@RequestParam String tableName, @RequestParam String currentOrganizationId, @RequestParam String newOrganizationId,
            @RequestParam(required = false) JobStatus jobStatus, @RequestParam (required = false) boolean isCopyRecord) {

        organizationService.updateData(tableName,currentOrganizationId,newOrganizationId,jobStatus,isCopyRecord);
        return ResponseEntity.ok(DATA_UPDATED_SUCCESSFULLY);
    }
}
