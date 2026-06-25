package com.chat.ruls.controller;

import com.chat.ruls.model.ChatMessage;
import com.chat.ruls.model.Message;
import com.chat.ruls.model.Recent;
import com.chat.ruls.model.User;
import com.chat.ruls.service.ChatService;
import com.chat.ruls.service.MessageRoutingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class ChatController {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageRoutingService messageRoutingService;

    @CrossOrigin
    @PostMapping("/sendMsg")
    public ResponseEntity<String> sendMessage(@RequestBody Message message) {
        LOG.info("Sending message from {} to {}", message.getSenderId(), message.getReceiverId());
        String response = chatService.sendMessage(message);
        return ResponseEntity.ok(response);
    }

    @CrossOrigin
    @PostMapping("/getMsg")
    public ResponseEntity<List<Message>> getMessages(@RequestBody Map<String, String> paramMap) {
        LOG.info("Fetching messages between {} and {}", paramMap.get("senderId"), paramMap.get("receiverId"));
        List<Message> messages = chatService.getMessages(paramMap);
        return ResponseEntity.ok(messages);
    }

    @CrossOrigin
    @PostMapping("/getUnreadMessages")
    public ResponseEntity<List<Message>> getUnreadMessages(@RequestBody Map<String, String> paramMap) {
        LOG.info("Fetching unread messages for {}", paramMap.get("receiverId"));
        List<Message> messages = chatService.getUnreadMessages(paramMap);
        return ResponseEntity.ok(messages);
    }

    @CrossOrigin
    @PostMapping("/searchUsers")
    public ResponseEntity<List<User>> searchUsers(@RequestBody Map<String, String> paramMap) {
        LOG.info("Searching users with query {}", paramMap.get("data"));
        List<User> users = chatService.searchUsers(paramMap);
        return ResponseEntity.ok(users);
    }

    @CrossOrigin
    @PostMapping("/getRecent")
    public ResponseEntity<List<Recent>> getRecent(@RequestBody Map<String, String> paramMap) {
        LOG.info("Fetching recent interactions for {}", paramMap.get("userEmail"));
        List<Recent> recent = chatService.getRecent(paramMap);
        return ResponseEntity.ok(recent);
    }

    @MessageMapping("/chat.sendMessage")
    @SendToUser("/queue/reply")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        LOG.info("Message received - From: {}, To: {}, Content: {}",
                chatMessage.getSenderId(),
                chatMessage.getReceiverId(),
                chatMessage.getContent());

        chatMessage.setTimestamp(new java.util.Date().toString());
        messageRoutingService.routeMessage(chatMessage);

        LOG.info("Message processed successfully");
        return chatMessage;
    }
}

