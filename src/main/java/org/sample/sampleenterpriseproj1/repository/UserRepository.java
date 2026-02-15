package org.sample.sampleenterpriseproj1.repository;

import org.sample.sampleenterpriseproj1.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface UserRepository extends CassandraRepository<User, UUID> {
}
