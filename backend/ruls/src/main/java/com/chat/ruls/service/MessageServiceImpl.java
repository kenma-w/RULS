package com.chat.ruls.service;

import com.chat.ruls.model.Message;
import com.chat.ruls.model.Recent;
import com.chat.ruls.repository.ChatRepository;
import com.chat.ruls.repository.RecentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {
    private final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private RecentRepository recentRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public List<Message> getMessages(Map<String, String> paramMap) {
        LOG.info("Fetching messages for {}", paramMap);
        Query query1 = new Query();
        Criteria criteria1 = new Criteria();

        criteria1.and("receiverId").is(paramMap.get("receiverId"));
        criteria1.and("senderId").is(paramMap.get("senderId"));

        query1.addCriteria(criteria1);
        List<Message> result1 = chatRepository.find(query1);

        Query query2 = new Query();
        Criteria criteria2 = new Criteria();

        criteria2.and("senderId").is(paramMap.get("receiverId"));
        criteria2.and("receiverId").is(paramMap.get("senderId"));

        query2.addCriteria(criteria2);
        List<Message> result2 = chatRepository.find(query2);
        result1.addAll(result2);
        return result1;
    }

    @Override
    public String sendMessage(Message msg) {
        LOG.info("Sending message from {} to {}", msg.getSenderId(), msg.getReceiverId());
        Message createdMsg = chatRepository.create(msg);
        updateRecentInteractions(msg);

        // Send message to Kafka
        kafkaProducer.sendMessage(msg);

        return createdMsg.getId() != null ? "True" : "False";
    }

    private void updateRecentInteractions(Message msg) {
        // Update recent interactions for sender and receiver
        Query query = new Query();
        query.addCriteria(Criteria.where("userEmail").is(msg.getReceiverId()));
        query.addCriteria(Criteria.where("fromEmail").is(msg.getSenderId()));

        List<Recent> existingDoc = recentRepository.find(query);

        if (!existingDoc.isEmpty()) {
            Update update = new Update().set("message", msg.getContent()).set("timestamp", msg.getTimestamp());
            recentRepository.update(query, update);
        } else {
            Recent newData = new Recent();
            newData.setUserEmail(msg.getReceiverId());
            newData.setMessage(msg.getContent());
            newData.setTimestamp(msg.getTimestamp());
            recentRepository.create(newData);
        }

        query = new Query();
        query.addCriteria(Criteria.where("userEmail").is(msg.getSenderId()));
        query.addCriteria(Criteria.where("fromEmail").is(msg.getReceiverId()));

        existingDoc = recentRepository.find(query);

        if (!existingDoc.isEmpty()) {
            Update update = new Update().set("message", msg.getContent()).set("timestamp", msg.getTimestamp());
            recentRepository.update(query, update);
        } else {
            Recent newData = new Recent();
            newData.setUserEmail(msg.getSenderId());
            newData.setMessage(msg.getContent());
            newData.setTimestamp(msg.getTimestamp());
            recentRepository.create(newData);
        }
    }
}
