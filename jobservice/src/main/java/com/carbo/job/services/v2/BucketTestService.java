package com.carbo.job.services.v2;

import com.carbo.job.exception.ErrorException;
import com.carbo.job.model.Error.Error;
import com.carbo.job.model.v2.BucketStatusOption;
import com.carbo.job.model.v2.LeatestBucketTest;
import com.carbo.job.model.v2.dto.*;
import com.carbo.job.repository.JobMongoDbRepository;
import com.carbo.job.repository.v2.BucketStatusOptionRepository;
import com.carbo.job.repository.v2.LeatestBucketTestRepository;
import com.carbo.job.utils.ErrorConstants;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.carbo.job.utils.ControllerUtil.*;

@Service
public class BucketTestService {

    private static final Logger logger = LoggerFactory.getLogger(BucketTestService.class);

    private final LeatestBucketTestRepository bucketTestRepository;
    private final BucketStatusOptionRepository statusOptionRepository;
    private final JobMongoDbRepository jobMongoDbRepository;

    public BucketTestService(LeatestBucketTestRepository bucketTestRepository,
                             BucketStatusOptionRepository statusOptionRepository,
                             JobMongoDbRepository jobMongoDbRepository) {
        this.bucketTestRepository = bucketTestRepository;
        this.statusOptionRepository = statusOptionRepository;
        this.jobMongoDbRepository = jobMongoDbRepository;
    }

    // ==================== Bucket Test CRUD ====================

    public BucketTestResponseDto create(HttpServletRequest request, BucketTestRequestDto dto) {
        try {
            String organizationId = getOrganizationId(request);
            String userName = getUserName(request);

            if (!jobMongoDbRepository.existsById(dto.getJobId())) {
                throw new ErrorException(Error.builder()
                        .errorCode(ErrorConstants.JOB_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.JOB_NOT_FOUND_MESSAGE)
                        .httpStatus(HttpStatus.NOT_FOUND).build());
            }

            LeatestBucketTest entity = toEntity(dto);
            entity.setOrganizationId(organizationId);
            entity.setCreatedBy(userName);
            entity.setCreatedTime(System.currentTimeMillis());
            entity.setUpdatedBy(userName);
            entity.setUpdatedTime(System.currentTimeMillis());

            LeatestBucketTest saved = bucketTestRepository.save(entity);
            return toResponseDto(saved);
        } catch (ErrorException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating bucket test: {}", e.getMessage(), e);
            throw new ErrorException(Error.builder()
                    .errorCode(ErrorConstants.BUCKET_TEST_V2_ERROR_CODE)
                    .errorMessage(ErrorConstants.BUCKET_TEST_V2_ERROR_MESSAGE)
                    .httpStatus(HttpStatus.BAD_REQUEST).build());
        }
    }

    public BucketTestResponseDto update(HttpServletRequest request, String id, BucketTestRequestDto dto) {
        try {
            String organizationId = getOrganizationId(request);
            String userName = getUserName(request);

            LeatestBucketTest existing = bucketTestRepository.findByIdAndOrganizationId(id, organizationId)
                    .orElseThrow(() -> new ErrorException(Error.builder()
                            .errorCode(ErrorConstants.BUCKET_TEST_V2_NOT_FOUND_CODE)
                            .errorMessage(ErrorConstants.BUCKET_TEST_V2_NOT_FOUND_MESSAGE)
                            .httpStatus(HttpStatus.NOT_FOUND).build()));

            existing.setUnitNumber(dto.getUnitNumber());
            existing.setDate(dto.getDate());
            existing.setFleet(dto.getFleet());
            existing.setPersonnel(dto.getPersonnel());
            existing.setPumpStatuses(toPumpStatusEntities(dto.getPumpStatuses()));
            existing.setNotes(dto.getNotes());
            existing.setUpdatedBy(userName);
            existing.setUpdatedTime(System.currentTimeMillis());

            LeatestBucketTest saved = bucketTestRepository.save(existing);
            return toResponseDto(saved);
        } catch (ErrorException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating bucket test: {}", e.getMessage(), e);
            throw new ErrorException(Error.builder()
                    .errorCode(ErrorConstants.BUCKET_TEST_V2_ERROR_CODE)
                    .errorMessage(ErrorConstants.BUCKET_TEST_V2_ERROR_MESSAGE)
                    .httpStatus(HttpStatus.BAD_REQUEST).build());
        }
    }

    public BucketTestResponseDto getById(HttpServletRequest request, String id) {
        String organizationId = getOrganizationId(request);
        LeatestBucketTest entity = bucketTestRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ErrorException(Error.builder()
                        .errorCode(ErrorConstants.BUCKET_TEST_V2_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.BUCKET_TEST_V2_NOT_FOUND_MESSAGE)
                        .httpStatus(HttpStatus.NOT_FOUND).build()));
        return toResponseDto(entity);
    }

    public Map<String, List<BucketTestResponseDto>> getByJobId(HttpServletRequest request, String jobId) {
        String organizationId = getOrganizationId(request);
        List<LeatestBucketTest> tests = bucketTestRepository.findByJobIdAndOrganizationId(jobId, organizationId);

        // Sort by unitNumber asc, then date desc (latest first), then createdTime desc as tiebreaker
        Comparator<BucketTestResponseDto> sorter = Comparator
                .comparing((BucketTestResponseDto dto) -> dto.getUnitNumber() != null ? dto.getUnitNumber() : "Unknown")
                .thenComparing((BucketTestResponseDto dto) -> dto.getDate() != null ? dto.getDate() : java.time.LocalDate.MIN, Comparator.reverseOrder())
                .thenComparing((BucketTestResponseDto dto) -> dto.getCreatedTime() != null ? dto.getCreatedTime() : 0L, Comparator.reverseOrder());

        return tests.stream()
                .map(this::toResponseDto)
                .sorted(sorter)
                .collect(Collectors.groupingBy(
                        dto -> dto.getUnitNumber() != null ? dto.getUnitNumber() : "Unknown",
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public void delete(HttpServletRequest request, String id) {
        String organizationId = getOrganizationId(request);
        LeatestBucketTest existing = bucketTestRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ErrorException(Error.builder()
                        .errorCode(ErrorConstants.BUCKET_TEST_V2_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.BUCKET_TEST_V2_NOT_FOUND_MESSAGE)
                        .httpStatus(HttpStatus.NOT_FOUND).build()));
        bucketTestRepository.delete(existing);
    }

    // ==================== CSV Export ====================

    public byte[] exportCsv(HttpServletRequest request, String jobId) {
        try {
            String organizationId = getOrganizationId(request);
            List<LeatestBucketTest> tests = bucketTestRepository.findByJobIdAndOrganizationId(jobId, organizationId);

            // Sort by unitNumber asc, then date desc (latest first), then createdTime desc as tiebreaker
            tests.sort(Comparator
                    .comparing((LeatestBucketTest t) -> t.getUnitNumber() != null ? t.getUnitNumber() : "Unknown")
                    .thenComparing((LeatestBucketTest t) -> t.getDate() != null ? t.getDate() : java.time.LocalDate.MIN, Comparator.reverseOrder())
                    .thenComparing((LeatestBucketTest t) -> t.getCreatedTime() != null ? t.getCreatedTime() : 0L, Comparator.reverseOrder()));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

            String[] headers = {"Unit Number", "Date", "Fleet", "Personnel", "Pump Number", "Max GPM",
                    "Hi Scale", "Status", "Notes"};

            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                    .setHeader(headers).build());

            for (LeatestBucketTest test : tests) {
                String personnel = test.getPersonnel() != null ? String.join(", ", test.getPersonnel()) : "";

                if (test.getPumpStatuses() != null && !test.getPumpStatuses().isEmpty()) {
                    for (LeatestBucketTest.PumpStatus ps : test.getPumpStatuses()) {
                        csvPrinter.printRecord(
                                test.getUnitNumber(),
                                test.getDate(),
                                test.getFleet(),
                                personnel,
                                ps.getPumpNumber(),
                                ps.getMaxGpm(),
                                ps.getHiScale(),
                                ps.getStatus(),
                                test.getNotes()
                        );
                    }
                } else {
                    csvPrinter.printRecord(
                            test.getUnitNumber(),
                            test.getDate(),
                            test.getFleet(),
                            personnel,
                            "", "", "", "",
                            test.getNotes()
                    );
                }
            }

            csvPrinter.flush();
            csvPrinter.close();
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Error exporting bucket test CSV: {}", e.getMessage(), e);
            throw new ErrorException(Error.builder()
                    .errorCode(ErrorConstants.BUCKET_TEST_V2_ERROR_CODE)
                    .errorMessage("Unable to export bucket test data as CSV")
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    // ==================== Bucket Status Options ====================

    public BucketStatusOptionResponseDto createStatus(HttpServletRequest request, BucketStatusOptionRequestDto dto) {
        try {
            String organizationId = getOrganizationId(request);
            String userName = getUserName(request);

            Optional<BucketStatusOption> existing = statusOptionRepository
                    .findByStatusNameAndOrganizationId(dto.getStatusName(), organizationId);
            if (existing.isPresent()) {
                throw new ErrorException(Error.builder()
                        .errorCode(ErrorConstants.BUCKET_STATUS_DUPLICATE_CODE)
                        .errorMessage(ErrorConstants.BUCKET_STATUS_DUPLICATE_MESSAGE)
                        .httpStatus(HttpStatus.CONFLICT).build());
            }

            BucketStatusOption entity = new BucketStatusOption();
            entity.setStatusName(dto.getStatusName());
            entity.setDefault(dto.isDefault());
            entity.setOrganizationId(organizationId);
            entity.setCreatedBy(userName);
            entity.setCreatedTime(System.currentTimeMillis());

            BucketStatusOption saved = statusOptionRepository.save(entity);
            return toStatusResponseDto(saved);
        } catch (ErrorException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating bucket status option: {}", e.getMessage(), e);
            throw new ErrorException(Error.builder()
                    .errorCode(ErrorConstants.BUCKET_TEST_V2_ERROR_CODE)
                    .errorMessage("Unable to create bucket status option")
                    .httpStatus(HttpStatus.BAD_REQUEST).build());
        }
    }

    public List<BucketStatusOptionResponseDto> getAllStatuses(HttpServletRequest request) {
        String organizationId = getOrganizationId(request);
        return statusOptionRepository.findByOrganizationId(organizationId)
                .stream()
                .map(this::toStatusResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteStatus(HttpServletRequest request, String id) {
        String organizationId = getOrganizationId(request);
        BucketStatusOption existing = statusOptionRepository.findById(id)
                .orElseThrow(() -> new ErrorException(Error.builder()
                        .errorCode(ErrorConstants.BUCKET_STATUS_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.BUCKET_STATUS_NOT_FOUND_MESSAGE)
                        .httpStatus(HttpStatus.NOT_FOUND).build()));

        if (!existing.getOrganizationId().equals(organizationId)) {
            throw new ErrorException(Error.builder()
                    .errorCode(ErrorConstants.BUCKET_STATUS_NOT_FOUND_CODE)
                    .errorMessage(ErrorConstants.BUCKET_STATUS_NOT_FOUND_MESSAGE)
                    .httpStatus(HttpStatus.NOT_FOUND).build());
        }

        if (existing.isDefault()) {
            throw new ErrorException(Error.builder()
                    .errorCode(ErrorConstants.BUCKET_STATUS_DEFAULT_DELETE_CODE)
                    .errorMessage(ErrorConstants.BUCKET_STATUS_DEFAULT_DELETE_MESSAGE)
                    .httpStatus(HttpStatus.BAD_REQUEST).build());
        }

        statusOptionRepository.delete(existing);
    }

    // ==================== Mapping Helpers ====================

    private LeatestBucketTest toEntity(BucketTestRequestDto dto) {
        LeatestBucketTest entity = new LeatestBucketTest();
        entity.setUnitNumber(dto.getUnitNumber());
        entity.setDate(dto.getDate());
        entity.setFleet(dto.getFleet());
        entity.setPersonnel(dto.getPersonnel());
        entity.setPumpStatuses(toPumpStatusEntities(dto.getPumpStatuses()));
        entity.setNotes(dto.getNotes());
        entity.setJobId(dto.getJobId());
        return entity;
    }

    private List<LeatestBucketTest.PumpStatus> toPumpStatusEntities(List<PumpStatusDto> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream().map(d -> {
            LeatestBucketTest.PumpStatus ps = new LeatestBucketTest.PumpStatus();
            ps.setPumpNumber(d.getPumpNumber());
            ps.setMaxGpm(d.getMaxGpm());
            ps.setHiScale(d.getHiScale());
            ps.setStatus(d.getStatus());
            return ps;
        }).collect(Collectors.toList());
    }

    private BucketTestResponseDto toResponseDto(LeatestBucketTest entity) {
        BucketTestResponseDto dto = new BucketTestResponseDto();
        dto.setId(entity.getId());
        dto.setUnitNumber(entity.getUnitNumber());
        dto.setDate(entity.getDate());
        dto.setFleet(entity.getFleet());
        dto.setPersonnel(entity.getPersonnel());
        dto.setPumpStatuses(toPumpStatusDtos(entity.getPumpStatuses()));
        dto.setNotes(entity.getNotes());
        dto.setJobId(entity.getJobId());
        dto.setOrganizationId(entity.getOrganizationId());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedTime(entity.getUpdatedTime());
        return dto;
    }

    private List<PumpStatusDto> toPumpStatusDtos(List<LeatestBucketTest.PumpStatus> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(ps -> {
            PumpStatusDto dto = new PumpStatusDto();
            dto.setPumpNumber(ps.getPumpNumber());
            dto.setMaxGpm(ps.getMaxGpm());
            dto.setHiScale(ps.getHiScale());
            dto.setStatus(ps.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    private BucketStatusOptionResponseDto toStatusResponseDto(BucketStatusOption entity) {
        BucketStatusOptionResponseDto dto = new BucketStatusOptionResponseDto();
        dto.setId(entity.getId());
        dto.setStatusName(entity.getStatusName());
        dto.setOrganizationId(entity.getOrganizationId());
        dto.setDefault(entity.isDefault());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setCreatedBy(entity.getCreatedBy());
        return dto;
    }
}
