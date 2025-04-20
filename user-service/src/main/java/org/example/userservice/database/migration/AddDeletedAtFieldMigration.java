package org.example.userservice.database.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ChangeUnit(id = "add-deleted-at-field", order = "001")
public class AddDeletedAtFieldMigration {
    @Execution
    public void changeSet(MongoTemplate mongoTemplate) {
        Query query = new Query(Criteria.where("deletedAt").exists(false));
        Update update = new Update().set("deletedAt", null);
        mongoTemplate.updateMulti(query, update, "users");
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        Query query = new Query(Criteria.where("deletedAt").exists(true));
        Update update = new Update().unset("deletedAt");
        mongoTemplate.updateMulti(query, update, "users");
    }
}
