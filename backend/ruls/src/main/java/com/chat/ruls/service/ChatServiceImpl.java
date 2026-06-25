package com.chat.ruls.service;

import com.chat.ruls.model.Message;
import com.chat.ruls.model.Recent;
import com.chat.ruls.model.User;
import com.chat.ruls.repository.ChatRepository;
import com.chat.ruls.repository.RecentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class ChatServiceImpl implements ChatService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private RecentRepository recentRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Message> getMessages(Map<String, String> paramMap) {
        String senderId = paramMap.get("senderId");
        String receiverId = paramMap.get("receiverId");
        String cacheKey = "messages_" + senderId + "_" + receiverId;

        // Check if messages are cached in Redis
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String cachedMessages = valueOps.get(cacheKey);

        if (cachedMessages != null) {
            try {
                // Return cached messages
                return objectMapper.readValue(cachedMessages, objectMapper.getTypeFactory().constructCollectionType(List.class, Message.class));
            } catch (JsonProcessingException e) {
                LOG.error("Failed to deserialize cached messages", e);
            }
        }

        // Fetch messages from MongoDB
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("senderId").is(senderId).and("receiverId").is(receiverId));
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("senderId").is(receiverId).and("receiverId").is(senderId));
        List<Message> messages = mongoTemplate.find(query1, Message.class);
        messages.addAll(mongoTemplate.find(query2, Message.class));

        try {
            // Cache messages in Redis
            valueOps.set(cacheKey, objectMapper.writeValueAsString(messages), 1, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize messages for caching", e);
        }

        LOG.info("The messages between " + senderId + " and " + receiverId + " are " + messages.toString());
        return messages;
    }

    @Override
    public String sendMessage(Message msg) {
        Message createdMsg = chatRepository.create(msg);
        updateRecentInteractions(msg);

        // Invalidate cache
        String cacheKey1 = "messages_" + msg.getSenderId() + "_" + msg.getReceiverId();
        String cacheKey2 = "messages_" + msg.getReceiverId() + "_" + msg.getSenderId();
        redisTemplate.delete(cacheKey1);
        redisTemplate.delete(cacheKey2);

        // Notify WebSocket server
        sendWebSocketMessage("/topic/messages", msg.toString());

        // Send message to Kafka
        kafkaProducer.sendMessage(msg);

        return createdMsg.getId() != null ? "True" : "False";
    }

    @Override
    public List<Message> getUnreadMessages(Map<String, String> paramMap) {
        String receiverId = paramMap.get("receiverId");
        Query query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(receiverId));
        List<Message> messages = mongoTemplate.find(query, Message.class);
        return messages;
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

    @Override
    public List<User> searchUsers(Map<String, String> paramMap) {
        Query query = new Query();
        Pattern pattern = Pattern.compile(paramMap.get("data"), Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("name").regex(pattern));

        return chatRepository.findUsers(query);
    }

    @Override
    public List<Recent> getRecent(Map<String, String> paramMap) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userEmail").is(paramMap.get("userEmail")));

        return recentRepository.find(query);
    }

    private void sendWebSocketMessage(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }
}
