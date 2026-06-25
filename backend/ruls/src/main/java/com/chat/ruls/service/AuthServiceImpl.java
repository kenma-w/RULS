package com.chat.ruls.service;

import com.chat.ruls.model.User;
import com.chat.ruls.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthServiceImpl implements AuthService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public User loginUser(Map<String, String> paramMap) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(paramMap.get("email")));

        User user = userRepository.findOne(query);
        if (user == null) {
            LOG.info("No user with given email");
            return null;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(paramMap.get("password"), user.getPassword())) {
            LOG.info("Password matches");

            // Notify WebSocket server about the login status
            sendWebSocketMessage("/topic/login", user.getEmail() + " logged in");

            return user;
        } else {
            LOG.info("Password does not match");
            return null;
        }
    }

    @Override
    public User signUp(User newUser) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(newUser.getEmail()));

        User checkUser = userRepository.findOne(query);
        if (checkUser != null) {
            LOG.info("User with given email already exists");
            return null;
        }

        newUser.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
        return userRepository.create(newUser);
    }

    @Override
    public String forgotPassword1(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        User user = userRepository.findOne(query);
        if (user == null) {
            return "Email not found";
        }

        Integer randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
        Update update = new Update().set("otp", randomNumber.toString());
        userRepository.update(query, update);

        return "OTP has been sent to your email";
    }

    @Override
    public String forgotPassword2(String email, String otp) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        User user = userRepository.findOne(query);
        if (user != null && user.getOtp().equals(otp)) {
            return "true";
        } else {
            return "Invalid OTP";
        }
    }

    @Override
    public User forgotPassword3(String email, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        Update update = new Update().set("password", new BCryptPasswordEncoder().encode(password));
        userRepository.update(query, update);

        return userRepository.findOne(query);
    }

    private void sendWebSocketMessage(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }
}

