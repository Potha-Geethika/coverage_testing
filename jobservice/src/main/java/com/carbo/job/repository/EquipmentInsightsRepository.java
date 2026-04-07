package com.carbo.job.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.carbo.job.model.EquipmentInsights;

@Repository
public interface EquipmentInsightsRepository extends MongoRepository<EquipmentInsights, String> {
    List<EquipmentInsights> findByEquipmentIdInAndFleetIdIn(List<String> equipmentId, List<String> fleetId);

    List<EquipmentInsights> findByFleetIdAndEquipmentIdIn(String fleetId, List<String> equipmentIds);

    @Query("{ 'fleetId': ?0, 'equipmentId': { $in: ?1 }, 'startDate': { $gte: ?2 }, 'endDate': { $lte: ?3 } }")
    List<EquipmentInsights> findByFleetIdAndEquipmentIdInAndJobNumberIsNullAndStartEndDateBetween(
            String fleetId,
            List<String> equipmentIds,
            Long expectedStartDate,
            Long expectedEndDate
    );

    List<EquipmentInsights> findByFleetIdAndOrganizationIdAndJobNumberIsNullAndEquipmentIdInOrderByCreatedTimeDesc(String id, String organizationId, List<String> equipmentIds);

    List<EquipmentInsights> findByFleetIdAndEquipmentIdInAndJobNumberIsNull(String id, List<String> equipmentIds);
}
