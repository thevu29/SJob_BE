package org.example.userservice.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ChangeUnit(id = "change-is-active-field-name", order = "002")
public class ChangeIsActiveFieldNameMigration {
    @Execution
    public void changeSet(MongoTemplate mongoTemplate) {
        Query query = new Query();
        Update update = new Update().rename("isActive", "active");
        mongoTemplate.updateMulti(query, update, "users");
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        Query query = new Query();
        Update update = new Update().rename("active", "isActive");
        mongoTemplate.updateMulti(query, update, "users");
    }
}