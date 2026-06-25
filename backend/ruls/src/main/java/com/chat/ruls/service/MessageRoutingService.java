package com.chat.ruls.service;

import com.chat.ruls.model.ChatMessage;
import com.chat.ruls.model.ServerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageRoutingService {
    private static final Logger logger = LoggerFactory.getLogger(MessageRoutingService.class);
    private final Map<String, ServerNode> serverRegistry = new ConcurrentHashMap<>();
    private final Map<String, String> userServerMap = new ConcurrentHashMap<>();

    @Autowired
    private KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${local.server.id}")
    private String localServerId;

    public void registerServer() {
        ServerNode serverNode = new ServerNode(localServerId, 0, true);
        serverRegistry.put(localServerId, serverNode);
        logger.info("Server registered: {}", localServerId);
    }

    public void deregisterServer() {
        serverRegistry.remove(localServerId);
        logger.info("Server deregistered: {}", localServerId);
    }

    public void routeMessage(ChatMessage message) {
        String targetServer = determineTargetServer(message.getReceiverId());
        logger.info("Routing message: {} -> {} via server {}",
                message.getSenderId(),
                message.getReceiverId(),
                targetServer);


        forwardToServer(message, targetServer);

    }

    private boolean isLocalServer(String serverId) {
        return localServerId.equals(serverId);
    }

    private void deliverLocally(ChatMessage message) {
        logger.info("Local delivery to user: {}", message.getReceiverId());
        messagingTemplate.convertAndSendToUser(message.getReceiverId(), "/queue/reply", message);
    }

    private void forwardToServer(ChatMessage message, String targetServer) {
        logger.info("Forwarding to server: {}", targetServer);
        String topic = "chat-messages-" + targetServer;
        kafkaTemplate.send(topic, message);
    }

    private String determineTargetServer(String userId) {
        return userServerMap.getOrDefault(userId, findLeastLoadedServer().getServerId());
    }

    private ServerNode findLeastLoadedServer() {
        ServerNode server = new ServerNode();
        logger.info("The server registry in the method [findLeastLoadedServer] : {}", serverRegistry.toString());
        try {
            server = serverRegistry.values().stream()
                    .filter(ServerNode::isActive)
                    .min(Comparator.comparingInt(ServerNode::getCurrentLoad))
                    .orElseThrow(() -> new RuntimeException("No active servers available"));
        } catch (RuntimeException e) {
            logger.info("The server registry in the method [findLeastLoadedServer] : {}", serverRegistry.toString());
        }

        return server;
    }
}
