package org.sample.sampleenterpriseproj1.repository;

import org.sample.sampleenterpriseproj1.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
