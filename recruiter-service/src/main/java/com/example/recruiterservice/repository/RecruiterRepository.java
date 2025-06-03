package com.example.recruiterservice.repository;

import com.example.recruiterservice.entity.Recruiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RecruiterRepository extends MongoRepository<Recruiter, String> {
    @Aggregation(pipeline = {
            "{ $project: { month: { $month: '$created_at' }, year: { $year: '$created_at' } } }",
            "{ $match: { year: ?0, month: ?1 } }",
            "{ $count: 'count' }"
    })
    Integer countRecruitersInMonth(int year, int month);

    @Aggregation(pipeline = {
            "{ $project: { month: { $month: '$created_at' }, year: { $year: '$created_at' } } }",
            "{ $match: { year: ?0 } }",
            "{ $group: { _id: '$month', count: { $sum: 1 } } }",
            "{ $sort: { _id: 1 } }"
    })
    List<Map<String, Object>> countRecruitersByMonthInYear(int year);

    Optional<Recruiter> findByUserId(String userId);

    List<Recruiter> findByNameIn(Collection<String> recruiterNames);

    @Query(value = """
            {
              $or: [
                { name: { $regex: ?0, $options: "i" } },
                { address: { $regex: ?0, $options: "i" } },
                { user_id: { $in: ?1 } }
              ]
            }
            """)
    Page<Recruiter> findBySearchCriteria(String query, List<String> userIds, Pageable pageable);

    List<Recruiter> findByIdIn(Collection<String> ids);
}
