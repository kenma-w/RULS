package com.chat.ruls.service;

import com.chat.ruls.model.ChatMessage;
import com.chat.ruls.model.Message;
import com.chat.ruls.repository.ChatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @KafkaListener(topics = "chat-messages-server1", groupId = "ruls-chat-group1")
    public void consumeMessage(ChatMessage message) {
        logger.info("Consumed message: {}", message);
        try {
            Message dbMessage = convertToMessage(message);

            chatRepository.create(dbMessage);
            logger.info("Stored message in MongoDB: {}", dbMessage);
            // deliver msg via websocket

            messagingTemplate.convertAndSendToUser(message.getReceiverId(), "/queue/reply", message);
            logger.info("Delivered message to WebSocket: {}", message);
        } catch (Exception e) {
            logger.error("Failed to process message", e);
        }
    }

    private Message convertToMessage(ChatMessage chatMessage) {
        Message message = new Message();
        message.setSenderId(chatMessage.getSenderId());
        message.setReceiverId(chatMessage.getReceiverId());
        message.setContent(chatMessage.getContent());
        message.setTimestamp(chatMessage.getTimestamp());
        return message;
    }
}

