package com.carbo.job.controllers;

import com.carbo.job.exception.DemoDataException;
import com.carbo.job.model.Error.Error;
import com.carbo.job.services.DemoFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.carbo.job.utils.ControllerUtil.getOrganizationId;

@RestController
@RequestMapping("/v1/demofile")
public class DemoFileController {

    private final DemoFileService demoFileService;

    private static final Logger logger = LoggerFactory.getLogger(DemoFileController.class);
    public DemoFileController(DemoFileService demoFileService) {
        this.demoFileService = demoFileService;
    }

    @PostMapping("/createjob")
    public ResponseEntity<?> createJobFromSheet(HttpServletRequest request, @RequestParam("organizationId") String organizationId, @RequestParam("fileName") String fileName) {
            MultipartFile files;
            try {
                files = demoFileService.loadAsFileSystemResource(getOrganizationId(request), fileName);
            } catch (Exception e) {
                logger.error("Error while loading file from file system", e);
                Error error = Error.builder()
                        .errorCode(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .errorMessage("Failed to load file from file system.")
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Map<String, Object> convertedJson;
            try {
                convertedJson = demoFileService.processFiles(files);
            } catch (Exception e) {
                logger.error("Error while Processing file", e);
                Error error = Error.builder()
                        .errorCode(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                        .errorMessage("File processing failed.")
                        .build();
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
            }

            try {
                return demoFileService.toCreateJob(request, organizationId, convertedJson, fileName);
            }catch (DemoDataException ex){
                logger.error("Error creating job", ex);
                Error error = Error.builder()
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .errorMessage(ex.getMessage())
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }catch (Exception e) {
                logger.error("Error creating job", e);
                Error error = Error.builder()
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .errorMessage("Failed to create demo data.")
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
    }

    @DeleteMapping("/deleteDemoData")
    public ResponseEntity<?> deleteDemoData(HttpServletRequest request ,@RequestParam("organizationId") String organizationId,
                                            @RequestParam("fileName") String fileName) throws Exception {
        try {
            return demoFileService.deleteDemoData(fileName, organizationId, request);
        }catch(Exception e){
            logger.error("error while deletion of data {}",e.getMessage(), e);
            Error error = Error.builder()
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .errorMessage("Failed to delete demo data")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);

        }
    }

    @DeleteMapping("/deleteWholeData")
    public ResponseEntity<?> deleteWholeData(@RequestParam("organizationId") String organizationId,
                                             @RequestParam("fileName") String fileName,
                                             HttpServletRequest request) throws Exception {
        try {
        return demoFileService.deleteWholeData(fileName, organizationId, request);
        }catch (Exception e) {
            logger.error("error while deleting job data {}",e.getMessage(), e);
            Error error = Error.builder()
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .errorMessage("Failed to delete demo data")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/deletejobData")
    public ResponseEntity<?> deleteJobData(HttpServletRequest request,
                                             @RequestParam("jobNum") String jobNum) throws Exception {
            return demoFileService.deleteJobData(request, jobNum);
    }




}