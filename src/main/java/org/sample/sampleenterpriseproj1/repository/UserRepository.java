package org.sample.sampleenterpriseproj1.repository;

import org.sample.sampleenterpriseproj1.model.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepository extends ElasticsearchRepository<User, String> {
}
