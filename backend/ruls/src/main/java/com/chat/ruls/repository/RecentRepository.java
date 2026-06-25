package com.chat.ruls.repository;

import com.chat.ruls.model.Recent;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public interface RecentRepository {
    List<Recent> find(Query query);
    Recent create(Recent recent);
    UpdateResult update(Query query, Update update);
}
