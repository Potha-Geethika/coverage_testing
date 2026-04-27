package com.carbo.pad.controllers;

import com.carbo.pad.services.JobDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.*;
import java.nio.file.*;
import java.security.Principal;
import java.time.*;
import java.util.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;






@WebMvcTest(JobDashboardController.class)
class JobDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobDashboardService jobDashboardService;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
    }

    @Test
    void getPadDetails_ShouldReturn200_OK_WhenJobIsFound() throws Exception {
        String jobId = "jobId123";
        ResponseEntity<?> responseEntity = ResponseEntity.ok("Pad details");
        when(jobDashboardService.getPadDetails(request, jobId)).thenReturn(responseEntity);

        mockMvc.perform(get("/v1/job-complete-dashboard/pad-details").param("jobId", jobId)
                .header("Time-Zone", "UTC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Pad details"));

        verify(jobDashboardService).getPadDetails(request, jobId);
    }

    @Test
    void getPadDetails_ShouldReturn404_NotFound_WhenJobIsNotFound() throws Exception {
        String jobId = "jobId123";
        when(jobDashboardService.getPadDetails(request, jobId)).thenReturn(
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found or access denied"));

        mockMvc.perform(get("/v1/job-complete-dashboard/pad-details").param("jobId", jobId))
                .andExpect(status().isNotFound());

        verify(jobDashboardService).getPadDetails(request, jobId);
    }

    @Test
    void getPadDetails_ShouldReturn500_InternalServerError_WhenExceptionOccurs() throws Exception {
        String jobId = "jobId123";
        when(jobDashboardService.getPadDetails(request, jobId)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/v1/job-complete-dashboard/pad-details").param("jobId", jobId))
                .andExpect(status().isInternalServerError());

        verify(jobDashboardService).getPadDetails(request, jobId);
    }

    @Test
    void getWellCompletionInformation_ShouldReturn200_OK_WhenJobAndWellAreFound() throws Exception {
        String jobId = "jobId123";
        String wellId = "wellId123";
        ResponseEntity<?> responseEntity = ResponseEntity.ok("Well completion information");
        when(jobDashboardService.getWellCompletionInformation(request, jobId, wellId)).thenReturn(responseEntity);

        mockMvc.perform(get("/v1/job-complete-dashboard/well-completion-information")
                .param("jobId", jobId)
                .param("wellId", wellId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Well completion information"));

        verify(jobDashboardService).getWellCompletionInformation(request, jobId, wellId);
    }

    @Test
    void getWellCompletionInformation_ShouldReturn404_NotFound_WhenJobIsNotFound() throws Exception {
        String jobId = "jobId123";
        String wellId = "wellId123";
        when(jobDashboardService.getWellCompletionInformation(request, jobId, wellId)).thenReturn(
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found"));

        mockMvc.perform(get("/v1/job-complete-dashboard/well-completion-information")
                .param("jobId", jobId)
                .param("wellId", wellId))
                .andExpect(status().isNotFound());

        verify(jobDashboardService).getWellCompletionInformation(request, jobId, wellId);
    }

    @Test
    void getCleanPerStage_ShouldReturn200_OK_WhenJobAndWellAreFound() throws Exception {
        String jobId = "jobId123";
        String wellId = "wellId123";
        ResponseEntity<?> responseEntity = ResponseEntity.ok("Clean per stage information");
        when(jobDashboardService.getCleanPerStage(request, jobId, wellId)).thenReturn(responseEntity);

        mockMvc.perform(get("/v1/job-complete-dashboard/clean-per-stage")
                .param("jobId", jobId)
                .param("wellId", wellId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Clean per stage information"));

        verify(jobDashboardService).getCleanPerStage(request, jobId, wellId);
    }

    @Test
    void getCleanPerStage_ShouldReturn404_NotFound_WhenJobIsNotFound() throws Exception {
        String jobId = "jobId123";
        String wellId = "wellId123";
        when(jobDashboardService.getCleanPerStage(request, jobId, wellId)).thenReturn(
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found"));

        mockMvc.perform(get("/v1/job-complete-dashboard/clean-per-stage")
                .param("jobId", jobId)
                .param("wellId", wellId))
                .andExpect(status().isNotFound());

        verify(jobDashboardService).getCleanPerStage(request, jobId, wellId);
    }

    @Test
    void getFinalISIPAndFG_ShouldReturn200_OK_WhenJobAndWellAreFound() throws Exception {
        String jobId = "jobId123";
        String wellId = "wellId123";
        ResponseEntity<?> responseEntity = ResponseEntity.ok("Final ISIP and FG information");
        when(jobDashboardService.getFinalISIPAndFG(request, jobId, wellId)).thenReturn(responseEntity);

        mockMvc.perform(get("/v1/job-complete-dashboard/final-isip-and-fg")
                .param("jobId", jobId)
                .param("wellId", wellId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Final ISIP and FG information"));

        verify(jobDashboardService).getFinalISIPAndFG(request, jobId, wellId);
    }

    @Test
    void getAveragePressureAndRate_ShouldReturn200_OK_WhenJobAndWellAreFound() throws Exception {
        String jobId = "jobId123";
        String wellId = "wellId123";
        ResponseEntity<?> responseEntity = ResponseEntity.ok("Average pressure and rate information");
        when(jobDashboardService.getAveragePressureAndRate(request, jobId, wellId)).thenReturn(responseEntity);

        mockMvc.perform(get("/v1/job-complete-dashboard/average-pressure-rate")
                .param("jobId", jobId)
                .param("wellId", wellId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Average pressure and rate information"));

        verify(jobDashboardService).getAveragePressureAndRate(request, jobId, wellId);
    }

    @Test
    void getAverageVsMax_ShouldReturn200_OK_WhenJobAndWellAreFound() throws Exception {
        String jobId = "jobId123";
        String wellId = "wellId123";
        ResponseEntity<?> responseEntity = ResponseEntity.ok("Average vs Max information");
        when(jobDashboardService.getAverageVsMax(request, jobId, wellId)).thenReturn(responseEntity);

        mockMvc.perform(get("/v1/job-complete-dashboard/average-vs-max")
                .param("jobId", jobId)
                .param("wellId", wellId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Average vs Max information"));

        verify(jobDashboardService).getAverageVsMax(request, jobId, wellId);
    }
}