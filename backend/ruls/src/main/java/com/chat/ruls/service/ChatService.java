package com.chat.ruls.service;

import com.chat.ruls.model.Message;
import com.chat.ruls.model.Recent;
import com.chat.ruls.model.User;

import java.util.List;
import java.util.Map;

public interface ChatService {
    List<Message> getMessages(Map<String, String> paramMap);
    String sendMessage(Message msg);
    List<User> searchUsers(Map<String, String> paramMap);
    List<Recent> getRecent(Map<String, String> paramMap);
    List<Message> getUnreadMessages(Map<String, String> paramMap);
}
