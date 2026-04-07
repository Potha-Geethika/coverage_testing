package com.carbo.job.repository;

import com.carbo.job.model.ProppantDeliveryEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProppantDeliveryMongoDbRepository extends MongoRepository<ProppantDeliveryEntry, String> {
    List<ProppantDeliveryEntry> findByOrganizationIdAndJobId(String organizationId, String jobId);

    List<ProppantDeliveryEntry> findByJobId(String jobId);

    List<ProppantDeliveryEntry> findByJobIdAndProppantAndBolAndPoAndSource(String jobId, String proppant, String bol, String po, String source);

    Optional<ProppantDeliveryEntry> findByOrganizationIdAndAutoOrderId(String organizationId, int autoOrderId);

    Optional<ProppantDeliveryEntry> findByJobIdAndBolAndPo(String jobId, String bol, String po);

    Optional<ProppantDeliveryEntry> findByJobIdAndBolAndPoAndAutoOrderId(String id, String bol, String po, int autoOrderId);

    List<ProppantDeliveryEntry> findByJobIdAndProppantAndBolAndPo(String id, String name, String bol, String po);
}
