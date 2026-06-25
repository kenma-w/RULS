package com.chat.ruls.repository;

import com.chat.ruls.model.User;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public User findOne(Query query) {
        return mongoTemplate.findOne(query, User.class);
    }

    @Override
    public User create(User newUser) {
        return mongoTemplate.insert(newUser, "User");
    }

    @Override
    public UpdateResult update(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, User.class);
    }
}
