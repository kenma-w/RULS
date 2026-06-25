package com.chat.ruls.repository;

import com.chat.ruls.model.User;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public interface UserRepository {
    User findOne(Query query);
    User create(User newUser);
    UpdateResult update(Query query, Update update);
}
