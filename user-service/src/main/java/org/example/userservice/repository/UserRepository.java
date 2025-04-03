package org.example.userservice.repository;

import org.example.userservice.entity.User;
import org.example.userservice.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findAllByDeletedAtIsNull();

    @Query("{'email': {$regex: ?0, $options: 'i'}, 'role': ?1, $or: [{'active': ?2}, {$expr: {$eq: [?3, false]}}], 'deleted_at': null}")
    List<User> findUsers(String email, UserRole role, boolean active, boolean filterByActive);

    Optional<User> findByIdAndDeletedAtIsNull(String id);

    List<User> findByIdInAndDeletedAtIsNull(List<String> ids);

    boolean existsByEmail(String email);

    @Query("{'email': {$regex: ?0, $options: 'i'}, $or: [{'active': ?1}, {$expr: {$eq: [?2, false]}}], 'deleted_at': null, 'role': 'ADMIN'}")
    Page<User> findAdmins(String emailPattern, boolean active, boolean filterByStatus, Pageable pageable);
}
