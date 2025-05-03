package org.example.userservice.database.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ChangeUnit(id = "add-otp-fields", order = "004")
public class AddOtpFieldsMigration {
    @Execution
    public void changeSet(MongoTemplate mongoTemplate) {
        Query query = new Query();
        Update update = new Update()
                .set("otp", null)
                .set("otpExpiresAt", null)
                .set("otpVerified", false);

        mongoTemplate.updateMulti(query, update, "users");
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        Query query = new Query();
        Update update = new Update()
                .unset("otp")
                .unset("otpExpiresAt")
                .unset("otpVerified");

        mongoTemplate.updateMulti(query, update, "users");
    }
}
