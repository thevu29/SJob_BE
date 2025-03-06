package org.example.userservice.repository;

import org.example.userservice.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findAllByDeletedAtIsNull();

    User findByEmail(String email);

    Optional<User> findByIdAndDeletedAtIsNull(String id);

    boolean existsByEmail(String email);
}
