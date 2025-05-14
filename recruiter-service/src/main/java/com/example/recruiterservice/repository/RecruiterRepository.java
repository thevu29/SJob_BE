package com.example.recruiterservice.repository;

import com.example.recruiterservice.entity.Recruiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;

public interface RecruiterRepository extends MongoRepository<Recruiter, String> {
    List<Recruiter> findByNameIn(Collection<String> recruiterNames);

    @Query(value = """
            {
              $or: [
                { user_id: { $in: ?1 } },
                {
                  $and: [
                    { ?0: { $ne: null } },
                    { ?0: { $ne: "" } },
                    {
                      $or: [
                        { name: { $regex: ?0, $options: "i" } },
                        { address: { $regex: ?0, $options: "i" } }
                      ]
                    }
                  ]
                }
              ]
            }
            """)
    Page<Recruiter> findBySearchCriteria(String query, List<String> userIds, Pageable pageable);
}
