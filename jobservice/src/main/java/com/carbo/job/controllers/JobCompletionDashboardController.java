package com.carbo.job.controllers;

import com.carbo.job.model.JobDashboardWellInfo;
import com.carbo.job.services.JobCompletionDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/job-complete-dashboard")
public class JobCompletionDashboardController {

    @Autowired
    JobCompletionDashboardService jobCompletionDashboardService;

    @RequestMapping(value = "/well-completion-report", method = RequestMethod.GET)
    public JobDashboardWellInfo getWellCompletionReport(HttpServletRequest request,
                                                        @RequestParam(name = "jobId", required = true) String jobId,
                                                        @RequestParam(name= "wellId", required = true) String wellId){

        // The Controller just passes the raw ID.
        // The Service MUST perform the database lookup to verify access and get the organization ID.
        return jobCompletionDashboardService.getWellCompletionReport(request, jobId, wellId);
    }

    @PostMapping(value = "/submit-well-data")
    public JobDashboardWellInfo getJobCompletionDashBoardInfo(HttpServletRequest request,
                                                              @RequestBody JobDashboardWellInfo jobDashboardWellInfo) {
        return jobCompletionDashboardService.saveWellInfo(request, jobDashboardWellInfo);
    }
}