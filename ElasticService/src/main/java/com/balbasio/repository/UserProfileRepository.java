package com.balbasio.repository;

import com.balbasio.repository.entity.UserProfile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableElasticsearchRepositories
public interface UserProfileRepository extends ElasticsearchRepository<UserProfile, String> {
    Optional<UserProfile> findOptionalByUserId(String id);

    Optional<UserProfile> findOptionalByAuthId(Long authId);
}
