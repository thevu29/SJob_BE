package org.example.userservice.database.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.List;

@ChangeUnit(id = "make-email-and-google-id-unique", order = "006")
public class MakeEmailAndGoogleIdUnique {
    @Execution
    public void changeSet(MongoTemplate mongoTemplate) {
        IndexOperations indexOperations = mongoTemplate.indexOps("users");

        mongoTemplate.updateMulti(
                new org.springframework.data.mongodb.core.query.Query(
                        org.springframework.data.mongodb.core.query.Criteria.where("googleId").is(null)
                ),
                new org.springframework.data.mongodb.core.query.Update().unset("googleId"),
                "users"
        );

        indexOperations.ensureIndex(
                new org.springframework.data.mongodb.core.index.IndexDefinition() {
                    @Override
                    public Document getIndexKeys() {
                        return new Document("email", 1);
                    }

                    @Override
                    public Document getIndexOptions() {
                        Document options = new Document();
                        options.put("unique", true);
                        options.put("name", "email_unique_idx");
                        return options;
                    }
                }
        );

        indexOperations.ensureIndex(
                new IndexDefinition() {
                    @Override
                    public Document getIndexKeys() {
                        return new Document("googleId", 1);
                    }

                    @Override
                    public Document getIndexOptions() {
                        Document options = new Document();
                        options.put("unique", true);
                        options.put("sparse", true);
                        options.put("name", "google_id_unique_idx");
                        return options;
                    }
                }
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        IndexOperations indexOperations = mongoTemplate.indexOps("users");

        List<IndexInfo> existingIndexes = indexOperations.getIndexInfo();

        for (IndexInfo index : existingIndexes) {
            if ("email_unique_idx".equals(index.getName())) {
                indexOperations.dropIndex("email_unique_idx");
            }
            if ("google_id_unique_idx".equals(index.getName())) {
                indexOperations.dropIndex("google_id_unique_idx");
            }
        }
    }
}