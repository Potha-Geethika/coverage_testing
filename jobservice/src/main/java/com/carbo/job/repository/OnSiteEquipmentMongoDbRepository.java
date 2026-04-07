package com.carbo.job.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.carbo.job.model.OnSiteEquipment;

@Repository
public interface OnSiteEquipmentMongoDbRepository extends MongoRepository<OnSiteEquipment, String> {
    List<OnSiteEquipment> findByOrganizationId(String organizationId);
    List<OnSiteEquipment> findByFleetIdAndOrganizationId(String fleetId, String organizationId);
    Optional<OnSiteEquipment> findById(String id);
}
