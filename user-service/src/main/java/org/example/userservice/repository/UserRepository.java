package org.example.userservice.repository;

import org.example.common.enums.UserRole;
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

    @Query("{'email': {$regex: ?0, $options: 'i'}, 'active': ?1, 'role': ?2}")
    Page<User> findByEmailAndActive(String emailRegex, boolean active, UserRole role, Pageable pageable);

    @Query("{'email': {$regex: ?0, $options: 'i'}, 'role': ?1}")
    Page<User> findByEmail(String emailRegex, UserRole role, Pageable pageable);
}
