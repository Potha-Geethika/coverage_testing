package com.carbo.job.repository;

import com.carbo.job.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMongoDbRepository extends MongoRepository<User, String> {
    List<User> findByOrganizationId(String organizationId);
    Optional<User> findByUserNameIgnoreCase(String userName);
    List<User> findByIdIn(List<String> usersIds);
}
