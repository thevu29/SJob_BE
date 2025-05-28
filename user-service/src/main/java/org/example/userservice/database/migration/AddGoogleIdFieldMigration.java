package org.example.userservice.database.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ChangeUnit(order = "005", id = "add-google-id-field")
public class AddGoogleIdFieldMigration {
    @Execution
    public void changeSet(MongoTemplate mongoTemplate) {
        Query query = new Query();
        Update update = new Update().set("googleId", null);

        mongoTemplate.updateMulti(query, update, "users");
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        Query query = new Query();
        Update update = new Update().unset("googleId");

        mongoTemplate.updateMulti(query, update, "users");
    }
}
