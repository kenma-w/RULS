package com.chat.ruls.repository;

import com.chat.ruls.model.Message;
import com.chat.ruls.model.User;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatRepositoryImpl implements ChatRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Message> find(Query query) {
        return mongoTemplate.find(query, Message.class);
    }

    @Override
    public List<User> findUsers(Query query) {
        return mongoTemplate.find(query, User.class);
    }

    @Override
    public Message create(Message msg) {
        return mongoTemplate.insert(msg, "Message");
    }

    @Override
    public UpdateResult update(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, User.class);
    }
}
