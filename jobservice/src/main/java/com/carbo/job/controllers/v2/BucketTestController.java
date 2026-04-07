package com.carbo.job.controllers.v2;

import com.carbo.job.model.v2.dto.BucketStatusOptionRequestDto;
import com.carbo.job.model.v2.dto.BucketStatusOptionResponseDto;
import com.carbo.job.model.v2.dto.BucketTestRequestDto;
import com.carbo.job.model.v2.dto.BucketTestResponseDto;
import com.carbo.job.services.v2.BucketTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v2/bucket-tests")
public class BucketTestController {

    private final BucketTestService bucketTestService;

    public BucketTestController(BucketTestService bucketTestService) {
        this.bucketTestService = bucketTestService;
    }

    @PostMapping
    public ResponseEntity<BucketTestResponseDto> create(HttpServletRequest request,
                                                         @RequestBody BucketTestRequestDto dto) {
        BucketTestResponseDto created = bucketTestService.create(request, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BucketTestResponseDto> update(HttpServletRequest request,
                                                         @PathVariable String id,
                                                         @RequestBody BucketTestRequestDto dto) {
        BucketTestResponseDto updated = bucketTestService.update(request, id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BucketTestResponseDto> getById(HttpServletRequest request,
                                                          @PathVariable String id) {
        BucketTestResponseDto bucketTest = bucketTestService.getById(request, id);
        return ResponseEntity.ok(bucketTest);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<Map<String, List<BucketTestResponseDto>>> getByJobId(HttpServletRequest request,
                                                                                @PathVariable String jobId) {
        Map<String, List<BucketTestResponseDto>> tests = bucketTestService.getByJobId(request, jobId);
        return ResponseEntity.ok(tests);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(HttpServletRequest request, @PathVariable String id) {
        bucketTestService.delete(request, id);
    }

    // ==================== CSV Export ====================

    @GetMapping("/export/csv/{jobId}")
    public ResponseEntity<byte[]> exportCsv(HttpServletRequest request,
                                            @PathVariable String jobId) {
        byte[] csvData = bucketTestService.exportCsv(request, jobId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "bucket-tests-" + jobId + ".csv");

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    // ==================== Bucket Status Options ====================

    @PostMapping("/statuses")
    public ResponseEntity<BucketStatusOptionResponseDto> createStatus(HttpServletRequest request,
                                                                       @RequestBody BucketStatusOptionRequestDto dto) {
        BucketStatusOptionResponseDto created = bucketTestService.createStatus(request, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<BucketStatusOptionResponseDto>> getAllStatuses(HttpServletRequest request) {
        List<BucketStatusOptionResponseDto> statuses = bucketTestService.getAllStatuses(request);
        return ResponseEntity.ok(statuses);
    }

    @DeleteMapping("/statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStatus(HttpServletRequest request, @PathVariable String id) {
        bucketTestService.deleteStatus(request, id);
    }
}
