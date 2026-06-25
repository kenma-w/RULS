package com.chat.ruls;

import com.chat.ruls.model.ChatMessage;
import com.chat.ruls.service.MessageRoutingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@SpringBootTest(properties = {"spring.config.name=application-test"})
public class RulsApplicationTests {

	@Mock
	private KafkaTemplate<String, ChatMessage> kafkaTemplate;

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private MessageRoutingService messageRoutingService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void contextLoads() {
	}
}
