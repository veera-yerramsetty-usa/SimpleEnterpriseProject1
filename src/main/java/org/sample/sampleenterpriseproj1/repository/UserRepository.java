package org.sample.sampleenterpriseproj1.repository;

import org.sample.sampleenterpriseproj1.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
