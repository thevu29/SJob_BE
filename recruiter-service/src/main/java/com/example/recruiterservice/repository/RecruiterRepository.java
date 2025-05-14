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

    @Query("""
    {
        $or: [
            { $and: [
                { $expr: { $eq: ["", ?0] }},
                { $or: [
                    { $expr: { $eq: [?1, null] }},
                    { $expr: { $in: ["$user_id", { $ifNull: [?1, []] }] }}
                ]}
            ]}
            ,
            { $and: [
                { $expr: { $ne: ["", ?0] }},
                { $expr: { $ne: [?1, null] }},
                { $expr: { $in: ["$user_id", { $ifNull: [?1, []] }] }}
            ]},
            { $and: [
                { $expr: { $ne: ["", ?0] }},
                { $expr: { $eq: [?1, null] }},
                { $or: [
                    { name: { $regex: ?0, $options: 'i' }},
                    { address: { $regex: ?0, $options: 'i' }},
                    { website: { $regex: ?0, $options: 'i' }},
                    { members: { $eq: { $convert: { input: ?0, to: "int", onError: -1 } } }},
                    { about: { $regex: ?0, $options: 'i' }},
                ]},
            ]}
        ]
    }
""")
    Page<Recruiter> findBySearchCriteria(String query, List<String> userIds, Pageable pageable);

    List<Recruiter> findByIdIn(Collection<String> ids);

}
