package com.chat.ruls.service;

import com.chat.ruls.model.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {
    List<Message> getMessages(Map<String, String> paramMap);
    String sendMessage(Message msg);
}
