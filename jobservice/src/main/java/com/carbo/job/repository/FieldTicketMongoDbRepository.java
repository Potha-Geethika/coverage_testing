package com.carbo.job.repository;

import com.carbo.job.model.FieldTicket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FieldTicketMongoDbRepository extends MongoRepository<FieldTicket, String> {
    List<FieldTicket> findByOrganizationId(String organizationId);
    List<FieldTicket> findByOrganizationIdAndWellAndName(String organizationId, String well, String name);
    List<FieldTicket> findByOrganizationIdAndJobId(String organizationId, String jobId);
    List<FieldTicket> findByJobIdAndWellAndName(String jobId, String well, String name);
    FieldTicket findByJobIdAndNameAndWell(String jobId,String name,String well);
    List<FieldTicket> findByJobIdAndWellAndNameIn(String jobId, String well, Set<String> name);
    List<FieldTicket> findByJobId(String jobId);

}
