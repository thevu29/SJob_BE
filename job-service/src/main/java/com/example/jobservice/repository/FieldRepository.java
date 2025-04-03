package com.example.jobservice.repository;

import com.example.jobservice.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FieldRepository extends JpaRepository<Field, String> {
    List<Field> findByNameIn(Collection<String> names);
}
