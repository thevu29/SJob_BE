package com.example.jobservice.repository;

import com.example.jobservice.entity.FieldDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FieldDetailRepository extends JpaRepository<FieldDetail, String> {
    List<FieldDetail> findByNameIn(Collection<String> names);
}
