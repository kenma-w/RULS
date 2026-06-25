package com.chat.ruls;

import com.chat.ruls.service.MessageRoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableMongoRepositories
@EnableWebMvc
public class RulsApplication {

	@Autowired
	private MessageRoutingService messageRoutingService;

	public static void main(String[] args) {
		SpringApplication.run(RulsApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		messageRoutingService.registerServer();
	}

	@EventListener(ContextClosedEvent.class)
	public void onApplicationShutdown() {
		messageRoutingService.deregisterServer();
	}
}
