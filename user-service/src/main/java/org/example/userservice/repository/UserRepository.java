package org.example.userservice.repository;

import org.common.enums.UserRole;
import org.example.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByIdIn(List<String> ids);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("{'email': {$regex: ?0, $options: 'i'}, $or: [{'active': ?1}, {$expr: {$eq: [?2, false]}}], 'role': ?3}")
    Page<User> findPagedUsers(String query, boolean active, boolean filterByStatus, UserRole role, Pageable pageable);
}
