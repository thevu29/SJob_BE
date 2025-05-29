package com.example.jobservice.repository;

import com.example.jobservice.dto.FieldDetail.FieldDetailCountDTO;
import com.example.jobservice.entity.FieldDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface FieldDetailRepository extends JpaRepository<FieldDetail, String> {
    List<FieldDetail> findByNameIn(Collection<String> names);

    @Query(value = """
            SELECT fd.id, fd.name, COUNT(jf.job_id) as count
            FROM job_service.field_details fd
            LEFT JOIN job_service.job_field jf ON fd.id = jf.field_detail_id
            GROUP BY fd.id, fd.name
            ORDER BY COUNT(jf.job_id) DESC
            """, nativeQuery = true)
    List<FieldDetailCountDTO> findAllWithJobCount();
}
