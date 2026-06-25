package com.chat.ruls.repository;

import com.chat.ruls.model.Recent;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecentRepositoryImpl implements RecentRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Recent> find(Query query) {
        return mongoTemplate.find(query, Recent.class);
    }

    @Override
    public Recent create(Recent recent) {
        return mongoTemplate.insert(recent, "Recent");
    }

    @Override
    public UpdateResult update(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, Recent.class);
    }
}
