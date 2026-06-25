package com.chat.ruls.repository;

import com.chat.ruls.model.Message;
import com.chat.ruls.model.User;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public interface ChatRepository {
    List<Message> find(Query query);
    List<User> findUsers(Query query);
    Message create(Message msg);
    UpdateResult update(Query query, Update update);
}
