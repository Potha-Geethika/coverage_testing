package com.carbo.job.repository;

import com.carbo.job.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Optional;

@Repository
public interface JobMongoDbRepository extends MongoRepository<Job, String> {
    @Query(value = "{ 'organizationId' : ?0, 'backupDate' : null }", fields = "{'id': 1, 'ts': 1, 'jobNumber': 1}")
    List<Job> findByOrganizationIdSimplified(String organizationId);

    @Query(value = "{ 'backupDate' : null, 'backupDate': { $exists: false }, organizationId: ?0 }")
    List<Job> findByOrganizationId(String organizationId);

    @Query(value = "{ 'backupDate' : null, 'backupDate': { $exists: false }, organizationId: ?0 }", fields = "{'id': 1, 'ts': 1, 'jobNumber': 1, 'operator': 1, 'pad': 1, 'fleet': 1, " +
            "'startDate': 1, 'targetStagesPerDay' : 1, 'targetDailyPumpTime': 1, 'wells': 1, 'rts': 1}")
    List<Job> findByOrganizationIdExternal(String organizationId);

    List<Job> findByOrganizationIdAndId(String organizationId, String jobId);

    @Query(value = "{ 'backupDate' : null, 'backupDate': { $exists: false }, organizationId: ?0, modified: { $gte: ?1, $lte: ?2 } }")
    List<Job> findByOrganizationIdAndModifiedBetween(String organizationId, Long start, Long end);

    @Query(value = "{ 'backupDate' : null, 'backupDate': { $exists: false }, 'organizationId' : ?0, 'wells.api' : ?1 }")
    List<Job> findByOrganizationIdAndWellApi(String organizationId, String wellApi);

    @Query(value = "{ 'backupDate' : null, 'backupDate': { $exists: false }, 'organizationId' : ?0, 'pad' : ?1 }")
    List<Job> findByOrganizationIdAndPad(String organizationId, String pad);

    @Query(value = "{ 'id' : ?0 }", fields = "{'id': 1, 'ts': 1, 'jobNumber': 1,  'proppantDeliveries': 1, 'startDate': 1, 'users': 1, 'wellheadCo': 1, 'wirelineCo': 1, 'waterTransferCo': 1, 'operationsType': 1, 'padEnergyType': 1, 'proposalId': 1, 'vendors': 1}")
    List<Job> getSimplifiedJob(String Id);

    @Query(value = "{ 'id' : ?0 }", fields = "{'id': 1, 'ts': 1, 'jobNumber': 1, 'operator': 1, 'pad': 1, 'fleet': 1, 'location': 1, 'startDate': 1, 'wellheadCo': 1, 'wirelineCo': 1}")
    List<Job> getSimplifiedJobForNPT(String id);

    @Query(fields = "{'id': 1, 'ts': 1, 'jobNumber': 1, 'backupDate': 1 }")
    List<Job> findByBackupDateBefore(Date timeAgo, Pageable pageable);

    @Query(value = "{ 'backupDate' : null, 'backupDate': { $exists: false } }")
    List<Job> findAllNoneBackup();

//    Job findByIdAndOrganizationId(String jobId, String organizationId);

    List<Job> findByStatus(String status);


    List<Job>findByOrganizationIdAndStatus(String organizationId,String status);



    List<Job> findByOrganizationIdAndStatusAndDistrictIdIn(String organizationId, String inProgress, List<String> districtIds);

    Page<Job> findByOperatorAndOrganizationId(String operator, String organizationId, Pageable pageable);


    List<Job> findBySharedWithOrganizationIdAndStatus(String organizationId, String status);

    List<Job> findByOrganizationIdAndDistrictIdIn(String organizationId, List<String> districtIds);

    List<Job> findBySharedWithOrganizationIdAndStatus(String organizationId, String status, List<String> districtIds);

    List<Job> findByIdNotIn(Set<String> existingJobIds);

    boolean existsByOrganizationIdAndJobNumber(String organizationId, String jobNumber);

    List<Job> findByOperatorInAndOrganizationIdInAndSharedWithOrganizationId(Set<String> operatorNames, Set<String> organizationIds, String sharedWithOrganizationId);

    List<Job> findByOrganizationIdAndStatusIn(String organizationId, List<String> statusList);

    List<Job> findByOrganizationIdAndDistrictIdInAndStatusIn(String organizationId, List<String> districtIds, Set<String> status);

    List<Job> findBySharedWithOrganizationIdAndDistrictIdInAndStatusIn(String organizationId, List<String> districtIds, Set<String> status);

    Optional<Job> findByIdAndOrganizationId(String id, String organizationId); // Overloaded for Optional return

    Optional<Job> findByIdAndSharedWithOrganizationId(String id, String sharedWithOrganizationId);

}
