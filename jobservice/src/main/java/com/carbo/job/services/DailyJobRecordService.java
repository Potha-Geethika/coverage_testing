package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.analytics.DailyJobRecord;
import com.carbo.job.repository.DailyJobRecordMongoDbRepository;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DailyJobRecordService {
    private final DailyJobRecordMongoDbRepository dailyJobRecordMongoDbRepository;
    private final MongoTemplate mongoTemplate;

    private static final Logger logger = LoggerFactory.getLogger(DailyJobRecordService.class);

    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Autowired
    private SslContext sslContext;

    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;
    @Autowired
    public DailyJobRecordService(DailyJobRecordMongoDbRepository dailyJobRecordMongoDbRepository, MongoTemplate mongoTemplate) {
        this.dailyJobRecordMongoDbRepository = dailyJobRecordMongoDbRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<DailyJobRecord> getByOrganizationId(HttpServletRequest request, String organizationId, int offset, int limit) {
        //return dailyJobRecordMongoDbRepository.findByOrganizationId(organizationId);
        return getByOrganizationIdFromService(request, organizationId, offset, limit);
    }

    public List<DailyJobRecord> getBySharedOrganizationId(HttpServletRequest request, String organizationId, int offset, int limit) {
        //return dailyJobRecordMongoDbRepository.findBySharedOrganizationId(organizationId);
        return getByShareOrganizationIdFromService(request, organizationId, offset, limit);
    }

    public List<DailyJobRecord> getByJobIdAndDate(String jobId, LocalDate date) {
        return dailyJobRecordMongoDbRepository.findByJobIdAndDate(jobId, Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public DailyJobRecord saveDailyJobRecord(DailyJobRecord record) {
        return dailyJobRecordMongoDbRepository.save(record);
    }

    public List<DailyJobRecord> getByOrganizationIdFromService (HttpServletRequest request, String organizationId, int offset, int limit) {

        logger.info("<-------------Invoked DailyJobRecordService :: getByOrganizationIdFromService ------------->");
        Sort sortByDateDesc = Sort.by(Sort.Direction.DESC, "date");
        Pageable pageable = PageRequest.of(offset, limit,sortByDateDesc);
        List<DailyJobRecord> dailyJobRecords = dailyJobRecordMongoDbRepository.findByOrganizationId(organizationId, pageable).getContent();

        if (!ObjectUtils.isEmpty(dailyJobRecords)) {
            return dailyJobRecords;
        } else {
            return new ArrayList<>();
        }
    }

    public List<DailyJobRecord> getByShareOrganizationIdFromService(HttpServletRequest request, String organizationId, int offset, int limit) {

        logger.info("<-------------Invoked DailyJobRecordService :: getByShareOrganizationIdFromService ------------->");
        logger.info("sharedOrganizationId:" + organizationId);
        Sort sortByDateDesc = Sort.by(Sort.Direction.DESC, "date");
        Pageable pageable = PageRequest.of(offset, limit,sortByDateDesc);
        List<DailyJobRecord> dailyJobRecords = dailyJobRecordMongoDbRepository.findBySharedOrganizationId(organizationId, pageable).getContent();
        if (!ObjectUtils.isEmpty(dailyJobRecords)) {
            return dailyJobRecords;
        } else {
            return new ArrayList<>();
        }
    }
}

    
