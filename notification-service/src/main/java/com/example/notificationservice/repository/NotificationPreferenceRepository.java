package com.example.notificationservice.repository;

import com.example.notificationservice.entity.NotificationPreference;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationPreferenceRepository extends MongoRepository<NotificationPreference, String> {
    NotificationPreference findByUserId(String userId);
}
