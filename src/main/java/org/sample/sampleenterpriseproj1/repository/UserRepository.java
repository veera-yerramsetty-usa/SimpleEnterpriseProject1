package org.sample.sampleenterpriseproj1.repository;

import org.sample.sampleenterpriseproj1.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserRepository extends Neo4jRepository<User, Long> {
}
