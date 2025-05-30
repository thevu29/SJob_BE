package org.example.reportservice.repository;

import org.example.reportservice.entity.Report;
import org.example.reportservice.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    @Query("{ $and: [ " +
            "  { $or: [ " +
            "    { 'jobSeekerId': { $in: ?0 } }, " +
            "    { 'recruiterId': { $in: ?1 } }, " +
            "    { $and: [ " +
            "      { $expr: { $eq: [ ?2, false ] } }, " +
            "      { $expr: { $eq: [ ?3, false ] } } " +
            "    ] } " +
            "  ] }, " +
            "  { $or: [ " +
            "    { 'status': ?4 }, " +
            "    { $expr: { $eq: [ ?5, false ] } } " +
            "  ] } " +
            "] }")
    Page<Report> getPaginatedReports(
            List<String> jobSeekerIds,
            List<String> recruiterIds,
            boolean hasJobSeekerIds,
            boolean hasRecruiterIds,
            ReportStatus status,
            boolean hasStatus,
            Pageable pageable
    );
}
